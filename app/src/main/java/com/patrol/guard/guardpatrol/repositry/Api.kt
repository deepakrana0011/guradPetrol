package com.patrol.guard.guardpatrol.repositry

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
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @POST("login")
    fun login(@Body loginDetailToServer: LoginDetailToServer): Call<LoginResponse>


    @POST("tour/{guardId}")
    fun guardTour(@Body detailToServer: GuardDetailToServer,@Path("guardId") guardId: String): Call<GuardTourResponse>

    @GET("incident")
    fun getIncidentsList(): Call<GetIncidentsList>


    @POST("trip")
    fun startTrip(@Body startTripDetailToServer: StartTripDetailToServer): Call<StartTripResponse>



    @PUT("scan")
    fun scan(@Body startTripDetailToServer: ScanCheckPointDetailToServer): Call<ScanCheckPointResponse>


    @PUT("endtrip")
    fun endTrip(@Body endDetailToServer: EndDetailToServer): Call<EndTripResponse>



    @POST("incident")
    fun sendIncident(@Body detailToServer: SendIncidentDetailToServer): Call<SendIncidentResponse>



    @Multipart
    @POST("incidentimage")
    fun uploadImage( @Part profileImage: MultipartBody.Part?):Call<UploadImageResponse>


    @Multipart
    @POST("audio")
    fun uploadAudio( @Part profileImage: MultipartBody.Part?):Call<UploadAudioResponse>

    @GET("about")
    fun about(): Call<AboutResponse>

    @POST("sos")
    fun sosNumber(@Body sendSosNumber: SendSosNumber): Call<ResponseBody>

    @GET("timeline/{trip_id}")
    fun getTimeLine(@Path("trip_id") camera_id :String): Call<GuardTourResponse>

    @POST("trip/history")
    fun fetchTourHistory(@Body history: SendHistoryToServer): Call<TripHistoryResponse>

    @PUT("pin")
    fun changePin(@Body settingDetailToServer: SettingDetailToServer): Call<UpdatePinResponse>

}