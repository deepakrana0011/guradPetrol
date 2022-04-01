package com.patrol.guard.guardpatrol.view.activity


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.possibility.hr.utils.FilesFunctions
import com.google.android.gms.location.*
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FragmentFunctions
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.view_alert_dialog.*
import org.koin.android.ext.android.inject
import java.io.File

open class BaseActivity : AppCompatActivity(), View.OnClickListener {
    val frequentFunctions: FrequentFunctions by inject()
    var mImageFile: File? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    val homeViewModel: HomeViewModel by inject()

    lateinit var mDialog: AlertDialog


    companion object {
        var textViewTitleName: TextView? = null
        var textViewTimeLine: TextView? = null

        var latitude = ""
        var longtiude = ""
    }


    fun checkPermission(permission: Array<String>): Int {
        var permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permission.indices) {
                val result = ContextCompat.checkSelfPermission(this!!, permission[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }


    fun setGoogleClient() {
        createLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (checkPermission(permission) > 0) {
            requestPermissions(arrayOf(permission[0]), 111)
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest().apply {
            interval = 1000
            fastestInterval = 5000
            smallestDisplacement = 100.toFloat()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback()
    }

    fun locationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    latitude = location.latitude.toString()
                    longtiude = location.longitude.toString()
                }
            }
        }
    }

    fun customToolBarWithMenu(toolbar: Toolbar, drawerLayout: DrawerLayout) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_tool_bar_with_menu, null)
        val imageViewMenu = view!!.findViewById<ImageView>(R.id.imageViewMenu)
        textViewTitleName = view!!.findViewById<TextView>(R.id.textViewTitleName)
        textViewTimeLine = view!!.findViewById<TextView>(R.id.textViewTimeLine)
        imageViewMenu.setOnClickListener {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        supportActionBar!!.setCustomView(view)
    }


    fun customToolBarWithBack(toolbar: Toolbar, titleName: String) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_tool_bar_with_back, null)
        val imageViewBack = view!!.findViewById<ImageView>(R.id.imageViewBack)
        textViewTitleName = view!!.findViewById<TextView>(R.id.textViewTitleName)
        var textViewTitle = view!!.findViewById<TextView>(R.id.textViewTitleName)
        textViewTitle.setText(titleName)
        imageViewBack.setOnClickListener {
            finish()
        }
        supportActionBar!!.setCustomView(view)
    }


    fun showSOSEventDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val layoutInflater = this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_sos_event, null)

        var textViewSOSEvent = view.findViewById<TextView>(R.id.textViewSOSEvent)
        var buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        var circleProgress = view.findViewById<CircularProgressBar>(R.id.circularProgress)
        circleProgress.progressMax= 30F
        circleProgress.progress = 30F

        val timer = object: CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                circleProgress.progress = ((millisUntilFinished/1000).toFloat())
               // Log.e("timer=>", millisUntilFinished.toString()+"=>"+(millisUntilFinished/1000))
            }

            override fun onFinish() {
                homeViewModel.submitSosNumber(true)
                mDialog.dismiss()
            }
        }
        timer.start()




        textViewSOSEvent.setText(
            frequentFunctions.customizeString(
                this!!,
                getString(R.string.sos),
                1,
                2
            ), TextView.BufferType.SPANNABLE
        )

        buttonCancel.setOnClickListener {
            if (timer!=null){
                timer.cancel()
            }
            mDialog.dismiss()
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


    fun doneDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val layoutInflater = this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_done, null)
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


        Handler().postDelayed({
            mDialog.dismiss()
            startActivity(Intent(this,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }, 2000)
    }


    fun alertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val layoutInflater = this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_alert_dialog, null)

        var textViewAlert = view.findViewById<TextView>(R.id.textViewAlert)
        var textViewMessage = view.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.setText(getString(R.string.please_turn_on_nfc_setting_to_scan_nfc_tag))
        var buttonOk = view.findViewById<Button>(R.id.buttonOk)

        textViewAlert.setText(
            frequentFunctions.customizeString(
                this!!,
                getString(R.string.alert),
                2,
                3
            ), TextView.BufferType.SPANNABLE
        )

        buttonOk.setOnClickListener {
            startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            mDialog.dismiss()
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


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.textViewGallery -> {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, Constants.GALLERY_INTENT)
                mDialog!!.hide()
            }

            R.id.textViewTakePic -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                mImageFile = FilesFunctions.createImageFile()
                cameraIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this!!, packageName + ".provider", mImageFile!!)
                )
                startActivityForResult(cameraIntent, Constants.CAMERA_INTENT)
                mDialog!!.hide()
            }
            R.id.textViewCancel -> {
                mDialog!!.hide()
            }
        }
    }


    //<editor-fold desc="permission dialog to show user that this permission is necessary">
    fun alertDialogWithOKButton(title: String, message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, Constants.REQUEST_PERMISSION_SETTING)
            dialogInterface.dismiss()
        }
        builder.show()
    }
    //</editor-fold>

    fun dialogForCameraGallery() {
        val dialogBuilder = AlertDialog.Builder(this!!)
        val view = LayoutInflater.from(this).inflate(R.layout.popup_camera_gallery, null)
        val mTextViewGallery = view.findViewById<TextView>(R.id.textViewGallery)
        val mTextViewTakePic = view.findViewById<TextView>(R.id.textViewTakePic)
        val mTextViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        mTextViewGallery.setOnClickListener(this)
        mTextViewTakePic.setOnClickListener(this)
        mTextViewCancel.setOnClickListener(this)
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        mDialog = dialogBuilder.show()
        mDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun turnOnOffFlashLight(){
        if (!frequentFunctions.isFlashLightOn) {
            frequentFunctions.turnOnTheLight(this)
        } else {
            frequentFunctions.turnOfFlashLight()
        }
    }
}