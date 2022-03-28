package com.patrol.guard.guardpatrol.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.patrol.guard.guardpatrol.model.LocationData
import java.lang.reflect.Type


class SharedPref(context: Application) {
    public val sharedpreferences: SharedPreferences

    init {
        sharedpreferences = context.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE)
    }

    fun setString(name: String, value: String) {
        sharedpreferences.edit().putString(name, value).commit()
    }

    fun getString(name: String): String {
        return sharedpreferences.getString(name, "").toString()
    }

    fun setBoolean(name: String, value: Boolean?) {
        sharedpreferences.edit().putBoolean(name, value!!).commit()
    }

    fun getBoolean(name: String): Boolean {
        return sharedpreferences.getBoolean(name, false)
    }

    fun setLocationArray() {
        val gson = Gson()
        val json = gson.toJson(BasicFunctions.locationArrayList)
        sharedpreferences.edit().putString("locationArray", json).commit()

    }


    fun getLocationArray() {
        BasicFunctions.locationArrayList  = ArrayList()
        val gson = Gson()
        val json: String = sharedpreferences.getString("locationArray", "")?:""
        if (json.isNotEmpty()) {
            val type: Type = object : TypeToken<List<LocationData?>?>() {}.type
            val arrayList: List<LocationData> = gson.fromJson<List<LocationData>>(json, type)
            if (arrayList.isNotEmpty()) {
                BasicFunctions.locationArrayList = arrayList as ArrayList<LocationData>
            }
        }

    }

    fun clearLocationData(){
        BasicFunctions.locationArrayList = ArrayList()
        val gson = Gson()
        val json = gson.toJson(BasicFunctions.locationArrayList)
        sharedpreferences.edit().putString("locationArray", json).commit()

    }



    fun clear() {
        sharedpreferences.edit().clear().commit()
    }
}