package com.patrol.guard.guardpatrol.viewModel

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.patrol.guard.guardpatrol.model.getIncidentsList.GetIncidentsList
import com.patrol.guard.guardpatrol.model.getIncidentsList.IncidentList
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.SendIncidentResponse
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.sendIncidentDetail.Incident
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.sendIncidentDetail.SendIncidentDetailToServer
import com.patrol.guard.guardpatrol.model.uploadImage.UploadImageResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.messages.MessagesActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class IncidentActivityViewModel(webServices: WebServices, frequentFunctions: FrequentFunctions,sharedPref: SharedPref) : ViewModel() {
    var webServices: WebServices
    var frequentFunctions: FrequentFunctions
    var sharedPref: SharedPref
    var sendIncidentClick: MutableLiveData<Boolean> = MutableLiveData()
    var onMessageClick:MutableLiveData<Boolean> = MutableLiveData()
    var nearestCheckPointChange = true

    init {
        this.webServices = webServices
        this.frequentFunctions = frequentFunctions
        this.sharedPref=sharedPref
    }

    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var feedBackMessage: MutableLiveData<String> = MutableLiveData()
    var showNearestCheckPointAlert: MutableLiveData<Boolean> = MutableLiveData()
    var onSuccessfullyGetIncidentList: MutableLiveData<GetIncidentsList> = MutableLiveData()
    var onSuccessfullyUploadIncidentImage: MutableLiveData<UploadImageResponse> = MutableLiveData()
    var onSuccessfullySendIncident: MutableLiveData<SendIncidentResponse> = MutableLiveData()

    fun getIncidentsList() {
        progressBar.value = true
        webServices.getIncidentsList().enqueue(object : Callback<GetIncidentsList> {
            override fun onResponse(call: Call<GetIncidentsList>, response: Response<GetIncidentsList>) {
                progressBar.value = false
                if (response.code() == 200) {
                    onSuccessfullyGetIncidentList.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<GetIncidentsList>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })

    }

    fun sendIncident(view: View) {
        sendIncidentClick.value = true
    }

    fun hitSendIncidentApi(name: String, selectedList: MutableList<IncidentList>, isPopUpShowing: Boolean) {
        if (isPopUpShowing) {
            hitApiIncidentApi(name, selectedList)
        } else {
            if (nearestCheckPointChange) {
                hitApiIncidentApi(name, selectedList)
            } else {
                showNearestCheckPointAlert.value = true
            }
        }


    }

    fun uploadIncidentImage(file: File) {
        progressBar.value = true
        webServices.uploadIncidentImage(file).enqueue(object : Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: Response<UploadImageResponse>) {
                progressBar.value = false
                if (response.code() == 200) {
                    onSuccessfullyUploadIncidentImage.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }


    fun checkBoxChanged(buttonView: CompoundButton, isChecked: Boolean) {
        nearestCheckPointChange = isChecked
    }

    fun messageClick(view: View){
        onMessageClick.value=true
    }


    fun hitApiIncidentApi(name: String, selectedList: MutableList<IncidentList>) {
        progressBar.value = true
        var detailToServer = SendIncidentDetailToServer()
        detailToServer.checkType = 2
        detailToServer.tripId = AllLocalHandler.singleTripDetail.tripId
        detailToServer.gaurdlat = BaseActivity.latitude
        detailToServer.gaurdlong = BaseActivity.longtiude
        detailToServer.name = name
        detailToServer.supervisorId=sharedPref.getString(Constants.SUPERVISOR_ID)
        detailToServer.gaurdId=sharedPref.getString(Constants.USER_ID)
        if (nearestCheckPointChange) {
            detailToServer.nearestCheckPoint = AllLocalHandler.singleTripDetail.currentCheckPoint
        } else {
            detailToServer.nearestCheckPoint = ""
        }
        var incidentsList: MutableList<Incident> = ArrayList()
        for (i in selectedList.indices) {
            var detail = Incident()
            detail.name = selectedList.get(i).name
            detail.image = selectedList.get(i).imageUrl
            incidentsList.add(detail)
        }
        detailToServer.incidents = incidentsList

        webServices.sendIncident(detailToServer).enqueue(object : Callback<SendIncidentResponse> {
            override fun onResponse(call: Call<SendIncidentResponse>, response: Response<SendIncidentResponse>) {
                progressBar.value = false
                if (response.code() == 200) {
                    onSuccessfullySendIncident.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<SendIncidentResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }


}