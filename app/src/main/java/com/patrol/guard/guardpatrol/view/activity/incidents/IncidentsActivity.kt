package com.patrol.guard.guardpatrol.view.activity.incidents

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.possibility.hr.utils.FilesFunctions
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.ActivityIncidentsBinding
import com.patrol.guard.guardpatrol.model.getIncidentsList.GetIncidentsList
import com.patrol.guard.guardpatrol.model.getIncidentsList.IncidentList
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.incidents.adapter.IncidentAdapter
import com.patrol.guard.guardpatrol.viewModel.IncidentActivityViewModel
import kotlinx.android.synthetic.main.activity_incidents.*
import org.koin.android.ext.android.inject
import androidx.appcompat.app.AppCompatActivity
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.SendIncidentResponse
import com.patrol.guard.guardpatrol.model.uploadImage.UploadImageResponse
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.activity.messages.MessagesActivity
import kotlinx.android.synthetic.main.activity_incidents.imageViewSos
import kotlinx.android.synthetic.main.activity_incidents.imageViewTorch
import kotlinx.android.synthetic.main.activity_incidents.relativeLayoutIncidents
import kotlinx.android.synthetic.main.activity_incidents.toolBar
import kotlinx.android.synthetic.main.app_bar_home.*


class IncidentsActivity : BaseActivity(), IncidentAdapter.ItemClickListener {


    lateinit var binding: ActivityIncidentsBinding
    val incidentViewModel: IncidentActivityViewModel by inject()
    val basicFunctions: BasicFunctions by inject()
    lateinit var incident: IncidentList
    lateinit var incidentList: List<IncidentList>
    var selectedList: MutableList<IncidentList> = ArrayList()
    lateinit var context: Context

    var adapter: IncidentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityIncidentsBinding>(this, R.layout.activity_incidents)
        context = this
        customToolBarWithBack(toolBar, getString(R.string.incidentsCaps))
        binding.incidentViewModel = incidentViewModel
        incidentViewModel.getIncidentsList()
        setData()
        clickEvent()
    }

    private fun clickEvent() {

        binding.imageViewTorch.setOnClickListener {
            val permission = arrayOf(Manifest.permission.CAMERA)
            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission[0]),
                    Constants.REQUEST_PERMISSION
                )
            } else {
                turnOnOffFlashLight()
            }
        }

        binding.imageViewSos.setOnClickListener {
            showSOSEventDialog()
        }
    }



    fun setData() {
        switchButtonNearestCheckPoint.isChecked = true
        if (AllLocalHandler.singleTripDetail.tripId.equals("") || AllLocalHandler.singleTripDetail.tripId==null) {
            linearLayoutNearestCheckPoint.visibility = View.GONE
        } else {
            linearLayoutNearestCheckPoint.visibility = View.VISIBLE
        }
        initObserver()
    }

    fun initObserver() {
        incidentViewModel.feedBackMessage.observe(this, object : Observer<String> {
            override fun onChanged(message: String?) {
                basicFunctions.showFeedbackMessage(this@IncidentsActivity!!, binding!!.root, message!!)
            }
        })

        incidentViewModel.progressBar.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(this@IncidentsActivity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })



        incidentViewModel.onSuccessfullyUploadIncidentImage.observe(this, object : Observer<UploadImageResponse> {
            override fun onChanged(t: UploadImageResponse?) {
                incident.imageUrl = t!!.image
            }
        })


        incidentViewModel.onMessageClick.observe(this,object :Observer<Boolean>{
            override fun onChanged(t: Boolean?) {
                startActivity(Intent(this@IncidentsActivity,MessagesActivity::class.java))
                finish()
            }
        })

        incidentViewModel.showNearestCheckPointAlert.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    alertDialogNearestCheckPoint()
                }
            }
        })


        incidentViewModel.onSuccessfullySendIncident.observe(this, object : Observer<SendIncidentResponse> {
            override fun onChanged(t: SendIncidentResponse?) {
                startActivity(
                    Intent(
                        this@IncidentsActivity,
                        HomeActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        })

        incidentViewModel.sendIncidentClick.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                selectedList.clear()
                for (i in incidentList.indices) {
                    if (incidentList.get(i).isSelected) {
                        if (incidentList.get(i).bitMap == null) {
                            basicFunctions.showFeedbackMessage(
                                this@IncidentsActivity,
                                rootLayout,
                                getString(R.string.select_image_for_incident)
                            )
                            break
                        } else {
                            selectedList.add(incidentList.get(i))
                        }
                    }

                    if (i == incidentList.size - 1) {
                        if (selectedList.size > 0) {
                            incidentViewModel.hitSendIncidentApi(
                                getString(R.string.send_incidents),
                                selectedList,
                                false
                            )
                        } else {
                            basicFunctions.showFeedbackMessage(
                                this@IncidentsActivity,
                                rootLayout,
                                getString(R.string.select_one_icident)
                            )
                        }
                    }
                }

            }
        })

        var observer = object : Observer<GetIncidentsList> {
            override fun onChanged(response: GetIncidentsList?) {
                if (response != null) {
                    incidentList = response!!.incidentList!!
                    setAdapter()
                }
            }
        }
        incidentViewModel.onSuccessfullyGetIncidentList.observe(context as AppCompatActivity, observer)
    }


    fun setAdapter() {
        if (adapter == null) {
            if (incidentList.size > 0) {
                recyclerView.visibility = View.VISIBLE
                adapter = IncidentAdapter(incidentList)
                adapter!!.itemCLickHandler(this)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter
            } else {
                recyclerView.visibility = View.GONE
            }
        } else {
            adapter!!.customNotify(incidentList!!)
        }
    }


    override fun onImageCrossClick(incident: IncidentList) {
        incident.bitMap = null
        incident.imageUrl = ""
        adapter!!.customNotify(incidentList)
    }


    override fun onImageClick(incident: IncidentList) {
        this.incident = incident
        val permission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
        if (checkPermission(permission) > 0) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission[0], permission[1]),
                Constants.REQUEST_PERMISSION
            )
        } else {
            dialogForCameraGallery()
        }
    }

    override fun onCheckBoxClick(isChecked: Boolean, incident: IncidentList) {
        incident.isSelected = isChecked
        adapter!!.customNotify(incidentList)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.CAMERA_INTENT -> {
                    val bitmap = BitmapFactory.decodeFile(mImageFile!!.getPath())
                    var newBitMap = FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    incident.bitMap = newBitMap
                    incidentViewModel.uploadIncidentImage(mImageFile!!)
                    setAdapter()

                }

                Constants.GALLERY_INTENT -> {
                    var path = FilesFunctions.getPathFromData(this!!, data!!)
                    val bitmap = MediaStore.Images.Media.getBitmap(this!!.getContentResolver(), data!!.data);
                    var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    incident.bitMap = newBitMap
                    incidentViewModel.uploadIncidentImage(mImageFile!!)
                    setAdapter()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        dialogForCameraGallery()
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                            val showRationale3 =
                                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storagePermission)
                                )
                            } else if (!showRationale2) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storagePermission)
                                )
                            } else if (!showRationale3) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.cameraPermission2)
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        incidentViewModel.onSuccessfullyGetIncidentList.value = null
    }


    fun alertDialogNearestCheckPoint() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val layoutInflater = this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_alert_dialog_nearest_check_point, null)

        var textViewAlert = view.findViewById<TextView>(R.id.textViewAlert)
        var textViewMessage = view.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.setText(getString(R.string.do_you_want_to_assign_to_nearest_check_point))
        var buttonYes = view.findViewById<Button>(R.id.buttonYes)
        var buttonNo = view.findViewById<Button>(R.id.buttonNo)

        textViewAlert.setText(
            frequentFunctions.customizeString(
                this!!,
                getString(R.string.alert),
                2,
                3
            ), TextView.BufferType.SPANNABLE
        )

        buttonYes.setOnClickListener {
            mDialog.dismiss()
            switchButtonNearestCheckPoint.isChecked = true
            incidentViewModel.hitSendIncidentApi(
                getString(R.string.send_incidents),
                selectedList,
                true
            )
        }


        buttonNo.setOnClickListener {
            mDialog.dismiss()
            switchButtonNearestCheckPoint.isChecked = false
            incidentViewModel.hitSendIncidentApi(
                getString(R.string.send_incidents),
                selectedList,
                true
            )
        }

        alertDialogBuilder.setView(view)
        mDialog = alertDialogBuilder.create()
        mDialog.setCancelable(false)
        mDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()
        val lp = WindowManager.LayoutParams()
        val window = mDialog.getWindow()
        window?.setGravity(Gravity.CENTER)
        lp.copyFrom(window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.setAttributes(lp)
    }


}
