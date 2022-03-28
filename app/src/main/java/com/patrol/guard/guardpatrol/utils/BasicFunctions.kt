package com.patrol.guard.guardpatrol.utils

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.LocationData


class BasicFunctions {
    lateinit var mDialogProgress: AlertDialog

    companion object {
         var locationArrayList =  ArrayList<LocationData>()

        fun addLocationData(latitude: Double, longitude: Double, sharedPref: SharedPref){
            var locationData = LocationData(latitude, longitude)
            if (!locationArrayList.contains(locationData)){
                locationArrayList.add(locationData)
            }
            sharedPref.setLocationArray()

            Log.e("Location11", "=>"+ locationArrayList.size+"");


        }
    }

    fun showProgressBar(context:Activity,message: String) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setCancelable(false)
        val view = LayoutInflater.from(context).inflate(R.layout.view_progress_dialog, null)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.setText(message)
        alertDialog.setView(view)
        mDialogProgress = alertDialog.create()
        mDialogProgress.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (!mDialogProgress.isShowing) {
            mDialogProgress.show()
        }

        val lp = WindowManager.LayoutParams()
        val window = mDialogProgress.window
        window?.setGravity(Gravity.CENTER)
        lp.copyFrom(window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = lp
    }

    fun hideProgressBar() {
        mDialogProgress.dismiss()
    }

    fun showFeedbackMessage(context: Activity,view: View, message: String) {
        val snakbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snakbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        if (snakbar.isShown) {
            snakbar.dismiss()
        }
        snakbar.show()
    }

}