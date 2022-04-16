package com.patrol.guard.guardpatrol.repositry

import android.app.Application
import com.patrol.guard.guardpatrol.model.about.AboutResponse
import com.patrol.guard.guardpatrol.model.about.SendSosNumber
import com.patrol.guard.guardpatrol.model.endTrip.EndDetailToServer
import com.patrol.guard.guardpatrol.model.endTrip.EndTripResponse
import com.patrol.guard.guardpatrol.model.getIncidentsList.GetIncidentsList
import com.patrol.guard.guardpatrol.model.guardTour.GuardDetailToServer
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.model.history.SendHistoryToServer
import com.patrol.guard.guardpatrol.model.history.TripHistoryResponse
import com.patrol.guard.guardpatrol.model.login.LoginDetailToServer
import com.patrol.guard.guardpatrol.model.login.LoginResponse
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointDetailToServer
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.SendIncidentResponse
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.sendIncidentDetail.SendIncidentDetailToServer
import com.patrol.guard.guardpatrol.model.setting.SettingDetailToServer
import com.patrol.guard.guardpatrol.model.setting.UpdatePinResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripDetailToServer
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.model.uploadAudio.UploadAudioResponse
import com.patrol.guard.guardpatrol.model.uploadImage.UploadImageResponse
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.SharedPref
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class WebServices(var context: Application, var sharedPref: SharedPref) {
    var retrofit: Retrofit? = null
    var api: Api

    companion object {
        var mInstance: WebServices? = null
    }

    init {
        mInstance = this


       // val logging = HttpLoggingInterceptor()
        val logging = AuthorizationInterceptor(context,sharedPref )
       //  logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        httpClient.addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        httpClient.writeTimeout(60, TimeUnit.SECONDS)

        retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        api = retrofit!!.create(Api::class.java)
    }

    fun login(loginDetailToServer: LoginDetailToServer): Call<LoginResponse> {
        val call = api.login(loginDetailToServer)
        return call;
    }


    fun getTour(guardDetailToServer: GuardDetailToServer, guardId: String): Call<GuardTourResponse> {
        val call = api.guardTour(guardDetailToServer, guardId)
        return call;
    }

    fun getIncidentsList(): Call<GetIncidentsList> {
        val call = api.getIncidentsList()
        return call;
    }


    fun startTrip(startTripDetailToServer: StartTripDetailToServer): Call<StartTripResponse> {
        val call = api.startTrip(startTripDetailToServer)
        return call;
    }


    fun scan(scanCheckPointDetailToServer: ScanCheckPointDetailToServer): Call<ScanCheckPointResponse> {
        val call = api.scan(scanCheckPointDetailToServer)
        return call
    }


    fun endTrip(endDetailToServer: EndDetailToServer): Call<EndTripResponse> {
        val call = api.endTrip(endDetailToServer)
        return call
    }


    fun sendIncident(detailToServer: SendIncidentDetailToServer): Call<SendIncidentResponse> {
        val call = api.sendIncident(detailToServer)
        return call
    }


    fun uploadIncidentImage(file: File):Call<UploadImageResponse>{
        var part: MultipartBody.Part? = null
        if (file != null) {
            val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            part = MultipartBody.Part.createFormData("image", file.name, requestBodies)
        }
        val call = api.uploadImage(part)
        return call
    }

    fun uploadIncidentAudio(file: File):Call<UploadAudioResponse>{
        var part: MultipartBody.Part? = null
        if (file != null) {
            val requestBodies = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            part = MultipartBody.Part.createFormData("audio", file.name, requestBodies)
        }
        val call = api.uploadAudio(part)
        return call
    }

    fun fetchSosNumber(): Call<AboutResponse> {
        val call = api.about()
        return call
    }

    fun submitSosNumber(sendSosNumber: SendSosNumber): Call<ResponseBody> {
        val call = api.sosNumber(sendSosNumber)
        return call
    }

    fun getTimeline(tripId: String): Call<GuardTourResponse> {
        val call = api.getTimeLine(tripId)
        return call;
    }

    fun fetchTourHistory(historyBody: SendHistoryToServer): Call<TripHistoryResponse> {
        val call = api.fetchTourHistory(historyBody)
        return call
    }

    fun updatePin(settingDetailToServer: SettingDetailToServer): Call<UpdatePinResponse> {
        val call = api.changePin(settingDetailToServer)
        return call;
    }

}
