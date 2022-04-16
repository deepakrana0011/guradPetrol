package com.patrol.guard.guardpatrol.repositry

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.Constants.USER_ID
import com.patrol.guard.guardpatrol.utils.SharedPref
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.android.ext.android.inject

class AuthorizationInterceptor(var context: Context, var sharedPreferences: SharedPref) : Interceptor {

 //   val sharedPref: SharedPref by inject()

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        //val authorization = null

      val  authorization = sharedPreferences.getString(Constants.jwtToken)
      var languageCode = sharedPreferences.getString(Constants.LANGUAGE_CODE)
      if (languageCode.isNullOrEmpty()){
          languageCode = "en"
      }
        if (authorization != null|| authorization!="") {
            request = request.newBuilder()
                .addHeader("authorization", authorization)
                .addHeader("Accept-Language",languageCode.lowercase()).build()
        }
        else{
            request = request.newBuilder()
                .addHeader("Accept-Language",languageCode.lowercase()).build()

        }


        return chain.proceed(request)
    }
}