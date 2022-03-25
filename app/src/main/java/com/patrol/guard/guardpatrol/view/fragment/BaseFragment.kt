package com.patrol.guard.guardpatrol.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.possibility.hr.utils.FilesFunctions
import com.google.android.gms.location.*
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.utils.Constants
import java.io.File

open class BaseFragment: Fragment() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var locationRequest: LocationRequest
    var mImageFile: File? = null
    lateinit var mDialog: android.app.AlertDialog




    //<editor-fold desc="permission dialog to show user that this permission is necessary">
    fun alertDialogWithOKButton(title: String, message: String) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialogInterface, i ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", activity!!.packageName, null)
            intent.data = uri
            startActivityForResult(intent, Constants.REQUEST_PERMISSION_SETTING)
            dialogInterface.dismiss()
        }
        builder.show()
    }
    //</editor-fold>

    fun checkPermission(permission: Array<String>): Int {
        var permissionNeeded = 0
        if (Build.VERSION.SDK_INT >= 23) {
            for (i in permission.indices) {
                val result = ContextCompat.checkSelfPermission(activity!!, permission[i])
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionNeeded++
                }
            }
        }
        return permissionNeeded
    }

    fun dialogForCameraGallery() {
        val dialogBuilder = android.app.AlertDialog.Builder(activity!!)
        val view = LayoutInflater.from(activity).inflate(R.layout.popup_camera_gallery, null)
        val mTextViewGallery = view.findViewById<TextView>(R.id.textViewGallery)
        val mTextViewTakePic = view.findViewById<TextView>(R.id.textViewTakePic)
        val mTextViewCancel = view.findViewById<TextView>(R.id.textViewCancel)
        mTextViewGallery.setOnClickListener{
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, Constants.GALLERY_INTENT)
            mDialog!!.hide()
        }
        mTextViewTakePic.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            mImageFile = FilesFunctions.createImageFile()
            cameraIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(activity!!, activity!!.packageName + ".provider", mImageFile!!)
            )
            startActivityForResult(cameraIntent, Constants.CAMERA_INTENT)
            mDialog!!.hide()

        }
        mTextViewCancel.setOnClickListener{
            mDialog!!.hide()
        }
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(false)
        mDialog = dialogBuilder.show()
        mDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}