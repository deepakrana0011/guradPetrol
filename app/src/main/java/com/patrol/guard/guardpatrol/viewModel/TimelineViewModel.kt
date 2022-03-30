package com.patrol.guard.guardpatrol.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TimelineViewModel(webServices: WebServices, frequentFunctions: FrequentFunctions, sharedPref: SharedPref) :
    ViewModel() {
    var webServices: WebServices
    var sharedPref: SharedPref
    var frequentFunctions: FrequentFunctions
    var shouldShowProgressBar = true


    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var feedBackMessage: MutableLiveData<String> = MutableLiveData()
    var onSuccessfullyGetTimelineResponse: MutableLiveData<GuardTourResponse> = MutableLiveData()

    init {
        this.webServices = webServices
        this.sharedPref = sharedPref
        this.frequentFunctions = frequentFunctions
    }

    fun getTimeLineResponse(tripId:String, progressBarVisibilty: Boolean) {

        if (progressBarVisibilty) {
            if (shouldShowProgressBar) {
                progressBar.value = true
            }

        }
        webServices.getTimeline(tripId).enqueue(object : Callback<GuardTourResponse> {
            override fun onResponse(call: Call<GuardTourResponse>, response: Response<GuardTourResponse>) {
                if (progressBarVisibilty) {
                    progressBar.value = false
                }
                if (response.code() == 200) {
                    onSuccessfullyGetTimelineResponse.value = response.body()
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


}