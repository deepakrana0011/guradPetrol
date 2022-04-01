package com.patrol.guard.guardpatrol.view.activity.timeline

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.ActivityTimeLineBinding
import com.patrol.guard.guardpatrol.model.endTrip.EndTripResponse
import com.patrol.guard.guardpatrol.model.guardTour.CheckPoint
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.DateFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.activity.incidents.IncidentsActivity
import com.patrol.guard.guardpatrol.view.activity.messages.MessagesActivity
import com.patrol.guard.guardpatrol.view.activity.timelinedetail.TimeLineDetailActivity
import com.patrol.guard.guardpatrol.view.fragment.homFragment.adapter.HomeFragmentAdapter
import com.patrol.guard.guardpatrol.viewModel.TimelineViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject

class TimeLineActivity : BaseActivity(), TimeLineViewAdapter.ItemClickListener {
    lateinit var binding: ActivityTimeLineBinding
    val timelineViewModel: TimelineViewModel by inject()
    val basicFunctions: BasicFunctions by inject()
    val sharedPref: SharedPref by inject()
    val dateFunctions: DateFunctions by inject()
    var adapter :TimeLineViewAdapter?=null
    var tripId=""
    var checkPoints: MutableList<CheckPoint> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityTimeLineBinding>(this, R.layout.activity_time_line)
        binding.timelineViewModel = timelineViewModel
        intent?.let {
            tripId = it.getStringExtra("trip_id").toString()
        }

        intitView()
    }

    private fun intitView() {
        customToolBarWithBack(binding.toolBar, getString(R.string.timeline))
        initiateObserver()
        clickEvent()
    }

    override fun onResume() {
        super.onResume()
        callApi()
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

        binding.relativeLayoutIncidents.setOnClickListener {
            startActivity(Intent(this, IncidentsActivity::class.java))
        }

        binding.relativeLayoutMessages.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }
    }

    private fun callApi() {
        timelineViewModel.getTimeLineResponse(tripId, true)
    }

    fun initiateObserver() {
        timelineViewModel.feedBackMessage.observe(this, object : Observer<String> {
            override fun onChanged(message: String?) {
                if (refreshLayoutView.isRefreshing) {
                    refreshLayoutView.isRefreshing = false
                }
                binding.message = message
                //basicFunctions.showFeedbackMessage(activity!!, binding!!.root, message!!)
            }
        })

        timelineViewModel.progressBar.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(this@TimeLineActivity, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })

        timelineViewModel.onSuccessfullyGetTimelineResponse.observe(this, object :
            Observer<GuardTourResponse> {
            override fun onChanged(response: GuardTourResponse?) {
                binding.dateFunctions = dateFunctions

                binding.message = ""
                binding.tourDetail = response?.tour
                binding.guardId = sharedPref.getString(Constants.GUARD_ID)
                checkPoints = response?.tour?.checkPoints!!

                setAdapter()

            }


        }) }
    fun setAdapter() {
        adapter = TimeLineViewAdapter(this, checkPoints, dateFunctions)
        adapter?.itemClickHandler(this)
        binding.recyclerViewDutyProgress.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDutyProgress.adapter = adapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        turnOnOffFlashLight()
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                               val showRationale3 =
                                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                          if (!showRationale3) {
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

    override fun viewClick(position: Int) {
        BasicFunctions.checkPointListForTimeLine = checkPoints
        val intent = Intent(this, TimeLineDetailActivity::class.java)
        intent.putParcelableArrayListExtra(Constants.checkPointList, ArrayList(checkPoints))
        intent.putExtra(Constants.Position, position)
        startActivity(intent)
      //  startActivity(Intent(this, TimeLineDetailActivity::class.java))
    }

}