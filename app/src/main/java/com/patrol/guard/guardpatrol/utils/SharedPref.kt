package com.patrol.guard.guardpatrol.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class SharedPref(context: Application) {
    public val sharedpreferences: SharedPreferences

    init {
        sharedpreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
    }

    fun setString(name: String, value: String) {
        sharedpreferences.edit().putString(name, value).commit()
    }

    fun getString(name: String): String {
        return sharedpreferences.getString(name, "")
    }

    fun setBoolean(name: String, value: Boolean?) {
        sharedpreferences.edit().putBoolean(name, value!!).commit()
    }

    fun getBoolean(name: String): Boolean {
        return sharedpreferences.getBoolean(name, false)
    }


    fun clear() {
        sharedpreferences.edit().clear().commit()
    }
}