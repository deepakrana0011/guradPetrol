package com.patrol.guard.guardpatrol.view.activity.scannerActivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.example.possibility.hr.utils.FilesFunctions
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.ActivityScannerBinding
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.viewModel.ScanViewModel
import kotlinx.android.synthetic.main.activity_scan_or_nfc.*
import kotlinx.android.synthetic.main.activity_scan_or_nfc.toolBar
import kotlinx.android.synthetic.main.activity_scanner.*
import org.koin.android.ext.android.inject
import java.util.*

class ScannerActivity : BaseActivity() {
    var codeScanner: CodeScanner? = null
    val scannerViewMoel: ScanViewModel by inject()
    val basicFunctions: BasicFunctions by inject()

    var nextCheckPointId = ""
    var checkPointId = ""

    lateinit var binding: ActivityScannerBinding
    lateinit var scannerView: CodeScannerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scanner)
        binding.scannerViewModel = scannerViewMoel
        customToolBarWithBack(toolBar, getString(R.string.scan))
        scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        checkCameraPermission()
        checkPointId = intent.getStringExtra("checkPointId")!!
        nextCheckPointId = intent.getStringExtra("nextCheckPointId")!!

        initiateObserver()

    }


    fun checkCameraPermission() {
        val permission = arrayOf(Manifest.permission.CAMERA)
        if (checkPermission(permission) > 0) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission[0]),
                Constants.REQUEST_PERMISSION
            )
        } else {
            codeScanner = CodeScanner(this, scannerView)
            codeScanner!!.startPreview()
            codeScanner!!.decodeCallback = DecodeCallback {
                runOnUiThread {
                    scannerViewMoel.scanCheckPoint(latitude, longtiude, checkPointId, nextCheckPointId, it.text)
                }
            }
            codeScanner!!.errorCallback = ErrorCallback {
                runOnUiThread {

                    Toast.makeText(this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
            scannerView.setOnClickListener {
                codeScanner!!.startPreview()
            }
        }
    }


    fun initiateObserver() {
        scannerViewMoel.feedBackMessage.observe(this, object : androidx.lifecycle.Observer<String> {
            override fun onChanged(message: String?) {
                basicFunctions.showFeedbackMessage(this@ScannerActivity, binding!!.root, message!!)
            }
        })

        scannerViewMoel.progressBar.observe(this, object : androidx.lifecycle.Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(this@ScannerActivity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })

        scannerViewMoel.onSuccessfullyScan.observe(this, object : androidx.lifecycle.Observer<ScanCheckPointResponse> {
            override fun onChanged(response: ScanCheckPointResponse?) {
                startActivity(Intent(this@ScannerActivity,HomeActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.REQUEST_PERMISSION_SETTING -> {
                    checkCameraPermission()
                }
            }
        } else {
            checkCameraPermission()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        checkCameraPermission()
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.cameraPermission)
                                )
                            } else {
                                checkCameraPermission()
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (codeScanner != null) {
            codeScanner!!.startPreview()
        }
    }

    override fun onPause() {
        if (codeScanner != null) {
            codeScanner!!.releaseResources()
        }
        scannerViewMoel.feedBackMessage.removeObservers(this)
        super.onPause()
    }
}
