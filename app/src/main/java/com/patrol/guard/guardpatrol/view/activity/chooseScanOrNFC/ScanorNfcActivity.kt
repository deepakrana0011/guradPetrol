package com.patrol.guard.guardpatrol.view.activity.chooseScanOrNFC

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointDetailToServer
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.scannerActivity.ScannerActivity
import kotlinx.android.synthetic.main.activity_scan_or_nfc.*
import kotlinx.android.synthetic.main.activity_scan_or_nfc.toolBar
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class ScanorNfcActivity : BaseActivity(), View.OnClickListener {
    var nextCheckPointId = ""
    var checkPointId = ""
    var isNfc = false
    var isQr = false
    private var mNfcAdapter: NfcAdapter? = null


    val basicFunctions: BasicFunctions by inject()
    val webServices: WebServices by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_or_nfc)
        customToolBarWithBack(toolBar, getString(R.string.scan))
        checkPointId = intent.getStringExtra("checkPointId")
        nextCheckPointId = intent.getStringExtra("nextCheckPointId")
        isNfc = intent.getBooleanExtra("isNfc", false)
        isQr = intent.getBooleanExtra("isQr", false)
        setData()
        clickListener()
    }

    fun setData() {
        imageViewSos.setOnClickListener(this)
        imageViewTorch.setOnClickListener(this)

        if (isNfc) {
            linearLayoutNfc.visibility = View.VISIBLE
        } else {
            linearLayoutNfc.visibility = View.GONE
        }
        if (isQr) {
            linearLayoutQrCode.visibility = View.VISIBLE
        } else {
            linearLayoutQrCode.visibility = View.GONE
        }
    }

    fun clickListener() {
        buttonScanQR.setOnClickListener {
            startActivity(
                Intent(this@ScanorNfcActivity, ScannerActivity::class.java)
                    .putExtra("checkPointId", checkPointId)
                    .putExtra("nextCheckPointId", nextCheckPointId)
            )
        }

        buttonNfcTag.setOnClickListener {
            if (mNfcAdapter!!.isEnabled) {
                val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
                val ndefDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
                val techDetected = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                val nfcIntentFilter = arrayOf(techDetected, tagDetected, ndefDetected)
                val pendingIntent =
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        0
                    )
                if (mNfcAdapter != null)
                    mNfcAdapter!!.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null)
                nfcScanDialog()
            } else {
                alertDialog()
            }
        }
    }


    fun nfcScanDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val layoutInflater = this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_scan_nfc_tag, null)

        var textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        var buttonCancel = view.findViewById<Button>(R.id.buttonCancel)

        textViewTitle.setText(
            frequentFunctions.customizeString(
                this!!,
                getString(R.string.ready_to_scan_nfc_tag),
                7,
                8
            ), TextView.BufferType.SPANNABLE
        )

        buttonCancel.setOnClickListener {
            mNfcAdapter!!.disableForegroundDispatch(this@ScanorNfcActivity)
            mDialog.dismiss()
        }
        alertDialogBuilder.setView(view)
        mDialog = alertDialogBuilder.create()
        mDialog.setCancelable(false)
        mDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()
        val lp = WindowManager.LayoutParams()
        val window = mDialog.getWindow()
        window.setGravity(Gravity.CENTER)
        lp.copyFrom(window.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setAttributes(lp)
    }

    override fun onResume() {
        super.onResume()
        initNfc()
    }

    fun initNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onNewIntent(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            val ndef = Ndef.get(tag)
            try {
                ndef.connect()
                val ndefMessage = ndef.ndefMessage
                val message = String(ndefMessage.records[0].payload)
                ndef.close()
                mDialog.dismiss()
                scanCheckPoint(message)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: FormatException) {
                e.printStackTrace()
            }
        }
    }

    fun scanCheckPoint(scanData: String) {

        var detailToServer = ScanCheckPointDetailToServer()
        detailToServer.latitude = BaseActivity.latitude
        detailToServer.longitude = BaseActivity.longtiude
        //detailToServer.status=1
        detailToServer.updateId = checkPointId
        detailToServer.nextUpdateId = nextCheckPointId
        detailToServer.nfcScan = scanData


        basicFunctions.showProgressBar(this, getString(R.string.loading))
        webServices.scan(detailToServer).enqueue(object : Callback<ScanCheckPointResponse> {
            override fun onResponse(call: Call<ScanCheckPointResponse>, response: Response<ScanCheckPointResponse>) {
                basicFunctions.hideProgressBar()
                if (response.code() == 200) {
                    doneDialog()
                } else {
                    basicFunctions.showFeedbackMessage(
                        this@ScanorNfcActivity,
                        rootLayout,
                        frequentFunctions.errorMessage(response.errorBody()!!.string())
                    )
                }
            }

            override fun onFailure(call: Call<ScanCheckPointResponse>, t: Throwable) {
                mNfcAdapter!!.disableForegroundDispatch(this@ScanorNfcActivity)
                basicFunctions.hideProgressBar()
                basicFunctions.showFeedbackMessage(this@ScanorNfcActivity, rootLayout!!, t.message!!)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null)
            mNfcAdapter!!.disableForegroundDispatch(this)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageViewSos -> {
                showSOSEventDialog()
            }

            R.id.imageViewTorch -> {
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
        }
    }


    fun turnOnOffFlashLight() {
        if (!frequentFunctions.isFlashLightOn) {
            frequentFunctions.turnOnTheLight(this@ScanorNfcActivity)
        } else {
            frequentFunctions.turnOfFlashLight()
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            111 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                        //
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        val showRationaleCamer = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)

                        if (!showRationaleCamer && permissions[0] == Manifest.permission.CAMERA) {
                            alertDialogWithOKButton(
                                getString(R.string.permission),
                                getString(R.string.camera_permission)
                            )
                        }
                    }
                }
            }
        }
    }

}
