package com.patrol.guard.guardpatrol.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.guardTour.GuardDetailToServer
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.model.guardTour.Tour
import com.patrol.guard.guardpatrol.model.login.LoginResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripDetailToServer
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.databinding.ObservableBoolean
import com.patrol.guard.guardpatrol.model.endTrip.EndDetailToServer
import com.patrol.guard.guardpatrol.model.endTrip.EndTripResponse
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointDetailToServer
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import kotlinx.android.synthetic.main.activity_scan_or_nfc.*


class HomeViewModel(webServices: WebServices, frequentFunctions: FrequentFunctions, sharedPref: SharedPref) :
    ViewModel() {
    var webServices: WebServices
    var sharedPref: SharedPref
    var frequentFunctions: FrequentFunctions
    var shouldShowProgressBar = true


    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var feedBackMessage: MutableLiveData<String> = MutableLiveData()
    var onSuccessfullyGetTourResponse: MutableLiveData<GuardTourResponse> = MutableLiveData()
    var onSuccessfullyStartTrip: MutableLiveData<StartTripResponse> = MutableLiveData()
    var onSuccessfullyEndTrip: MutableLiveData<EndTripResponse> = MutableLiveData()
    var onSuccessfullyScanGeoFence: MutableLiveData<ScanCheckPointResponse> = MutableLiveData()

    init {
        this.webServices = webServices
        this.sharedPref = sharedPref
        this.frequentFunctions = frequentFunctions
    }

    fun getGuardTour(latitude: String, longitude: String, progressBarVisibilty: Boolean) {
        var guardDetail = GuardDetailToServer()
        guardDetail.latitude = latitude.toDouble()
        guardDetail.longitude = longitude.toDouble()

        if (progressBarVisibilty) {
            if (shouldShowProgressBar) {
                progressBar.value = true
            }

        }

        var guardId = sharedPref.getString(Constants.USER_ID)
        webServices.getTour(guardDetail, guardId).enqueue(object : Callback<GuardTourResponse> {
            override fun onResponse(call: Call<GuardTourResponse>, response: Response<GuardTourResponse>) {
                if (progressBarVisibilty) {
                    progressBar.value = false
                }
                if (response.code() == 200) {
                    onSuccessfullyGetTourResponse.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<GuardTourResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }

    fun startTrip(tourId: String, latitude: String, longitude: String) {
        progressBar.value = true
        shouldShowProgressBar = false
        var detailToServer = StartTripDetailToServer()
        var guardId = sharedPref.getString(Constants.USER_ID)
        detailToServer.gaurdId = guardId
        detailToServer.latitude = latitude
        detailToServer.longitude = longitude
        detailToServer.tourId = tourId

        webServices.startTrip(detailToServer).enqueue(object : Callback<StartTripResponse> {
            override fun onResponse(call: Call<StartTripResponse>, response: Response<StartTripResponse>) {
                if (response.code() == 200) {
                    onSuccessfullyStartTrip.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<StartTripResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }


    fun endTrip(tourId: String, latitude: String, longitude: String) {
        shouldShowProgressBar = false
        progressBar.value = true
        var detailToServer = EndDetailToServer()
        var guardId = sharedPref.getString(Constants.USER_ID)
        detailToServer.latitude = latitude
        detailToServer.longitude = longitude
        detailToServer.updateId = tourId
        detailToServer.location = BasicFunctions.locationArrayList


        webServices.endTrip(detailToServer).enqueue(object : Callback<EndTripResponse> {
            override fun onResponse(call: Call<EndTripResponse>, response: Response<EndTripResponse>) {
                progressBar.value = false
                if (response.code() == 200) {
                    onSuccessfullyEndTrip.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<EndTripResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }


    fun scanGeoFence(checkPointId: String, nextCheckPointId: String) {
        var detailToServer = ScanCheckPointDetailToServer()
        detailToServer.latitude = BaseActivity.latitude
        detailToServer.longitude = BaseActivity.longtiude
        detailToServer.updateId = checkPointId
        detailToServer.nextUpdateId = nextCheckPointId
        shouldShowProgressBar = false
        progressBar.value = true
        webServices.scan(detailToServer).enqueue(object : Callback<ScanCheckPointResponse> {
            override fun onResponse(call: Call<ScanCheckPointResponse>, response: Response<ScanCheckPointResponse>) {
                progressBar.value = false
                if (response.code() == 200) {
                    onSuccessfullyScanGeoFence.value=response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())

                }
            }

            override fun onFailure(call: Call<ScanCheckPointResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t.message!!
            }
        })
    }
}