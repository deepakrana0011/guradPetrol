package com.patrol.guard.guardpatrol.viewModel

import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.SendIncidentResponse
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.sendIncidentDetail.SendIncidentDetailToServer
import com.patrol.guard.guardpatrol.model.uploadAudio.UploadAudioResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.fragment.audio.AudioFragment
import com.patrol.guard.guardpatrol.view.fragment.image.ImageFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MessageActivityViewModel(frequentFunctions: FrequentFunctions, webServices: WebServices, sharedPref: SharedPref) :
    ViewModel() {
    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var feedBackMessage: MutableLiveData<String> = MutableLiveData()
    var showProgress = true
    var onSuccessfullySendIncident: MutableLiveData<SendIncidentResponse> = MutableLiveData()
    var webServices: WebServices
    var frequentFunctions: FrequentFunctions
    var message: String = ""
    var sharedPref: SharedPref
    var nearestCheckPointChange = true
    var isPopUpShowing=false
    var showNearestCheckPointAlert: MutableLiveData<Boolean> = MutableLiveData()
    var sendIncidentClick: MutableLiveData<Boolean> = MutableLiveData()




    init {
        this.webServices = webServices
        this.frequentFunctions = frequentFunctions
        this.sharedPref = sharedPref
    }


    fun hitSendIncidentApi(name: String, audioFile: String) {
        if (showProgress) {
            progressBar.value = true
        }
        var detailToServer = SendIncidentDetailToServer()
        detailToServer.checkType = 2
        detailToServer.tripId = AllLocalHandler.singleTripDetail.tripId
        detailToServer.gaurdlat = BaseActivity.latitude
        detailToServer.gaurdlong = BaseActivity.longtiude
        detailToServer.name = name
        if (nearestCheckPointChange) {
            detailToServer.nearestCheckPoint = AllLocalHandler.singleTripDetail.currentCheckPoint
        } else {
            detailToServer.nearestCheckPoint = ""
        }
        detailToServer.audio = audioFile
        detailToServer.message = message
        detailToServer.supervisorId = sharedPref.getString(Constants.SUPERVISOR_ID)
        detailToServer.gaurdId = sharedPref.getString(Constants.USER_ID)
        detailToServer.images = ImageFragment.imagesListUploadedToServer!!

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

    fun sendMessageClick(view: View) {
        var isImageExist = false
        for (i in ImageFragment.imagesListUploadedToServer.indices) {
            if (!ImageFragment.imagesListUploadedToServer.get(i).equals("")) {
                isImageExist = true
                break
            }
        }


        if (message.equals("") && !isImageExist && AudioFragment.mergedAudioFilePath.equals("")) {
            feedBackMessage.value = view.context.getString(R.string.message_must_contain_audio_image_video)
        }else{
            if(isPopUpShowing){
                if (AudioFragment.mergedAudioFilePath.equals("")) {
                    hitSendIncidentApi(
                        view.context.getString(R.string.sent_message),
                        ""
                    )
                } else {
                    val file = File(AudioFragment.mergedAudioFilePath)
                    uploadIncidentAudio(file, view)
                }
            }else{
                if (nearestCheckPointChange) {
                    if (AudioFragment.mergedAudioFilePath.equals("")) {
                        hitSendIncidentApi(
                            view.context.getString(R.string.sent_message),
                            ""
                        )
                    } else {
                        val file = File(AudioFragment.mergedAudioFilePath)
                        uploadIncidentAudio(file, view)
                    }
                } else {
                    showNearestCheckPointAlert.value = true
                }
            }
        }


    }


    fun uploadIncidentAudio(file: File, view: View) {
        progressBar.value = true
        webServices.uploadIncidentAudio(file).enqueue(object : Callback<UploadAudioResponse> {
            override fun onResponse(call: Call<UploadAudioResponse>, response: Response<UploadAudioResponse>) {
                if (response.code() == 200) {
                    showProgress = false
                    hitSendIncidentApi(view.context.getString(R.string.sent_message), response.body()!!.audio!!)
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<UploadAudioResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }


    fun sendIncidentClick(view: View){
        sendIncidentClick.value=true
    }


    fun checkBoxChanged(buttonView: CompoundButton, isChecked: Boolean) {
        nearestCheckPointChange = isChecked
    }
}