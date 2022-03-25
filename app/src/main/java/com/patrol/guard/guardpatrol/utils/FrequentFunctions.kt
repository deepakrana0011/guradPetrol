package com.patrol.guard.guardpatrol.utils

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.patrol.guard.guardpatrol.R
import org.json.JSONException
import org.json.JSONObject
import android.hardware.camera2.CameraAccessException
import androidx.core.content.ContextCompat.getSystemService
import android.hardware.camera2.CameraManager
import android.os.Build


class FrequentFunctions {

    lateinit var mCameraManager: CameraManager
    lateinit var camera: Camera
    lateinit var parameters: Camera.Parameters

    var isFlashLightOn=false
    var camerId = ""
    fun customizeString(
        activity: Activity,
        textName: String,
        startPosition: Int,
        endPosition: Int
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        val spannableString = SpannableString(textName)
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(activity!!, R.color.colorRed)),
            startPosition,
            endPosition,
            0
        )
        builder.append(spannableString)
        return builder
    }

    fun errorMessage(errorBody: String): String {
        var error = ""
        try {
            val `object` = JSONObject(errorBody)
            error = `object`.getString("error")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return error
    }


    fun hideKeyBoard(activity: Context, view: View) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun turnOnTheLight(activity: Activity) {
        mCameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            camerId = mCameraManager.getCameraIdList()[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager.setTorchMode(camerId, true);
        } else {
            camera = Camera.open();
            parameters = camera.getParameters()
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
        }
        isFlashLightOn=true
    }


    fun turnOfFlashLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager.setTorchMode(camerId, false);
        } else {
            camera = Camera.open();
            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
        }

        isFlashLightOn=false
    }
}