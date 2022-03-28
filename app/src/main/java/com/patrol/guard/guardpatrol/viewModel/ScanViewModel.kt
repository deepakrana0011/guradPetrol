package com.patrol.guard.guardpatrol.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointDetailToServer
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanViewModel(webServices: WebServices,frequentFunctions: FrequentFunctions) : ViewModel() {

    var webServices: WebServices
    var frequentFunctions: FrequentFunctions
    var progressBar: MutableLiveData<Boolean> = MutableLiveData()
    var feedBackMessage: MutableLiveData<String> = MutableLiveData()
    var onSuccessfullyScan: MutableLiveData<ScanCheckPointResponse> = MutableLiveData()

    init {
        this.webServices = webServices
        this.frequentFunctions=frequentFunctions
    }


    fun scanCheckPoint(latitude: String, longitude: String,checkPointId:String,nextUpdatedId:String,scanData:String) {

        var detailToServer = ScanCheckPointDetailToServer()
        detailToServer.latitude = latitude
        detailToServer.longitude = longitude
        //detailToServer.status=1
        detailToServer.updateId=checkPointId
        detailToServer.nextUpdateId=nextUpdatedId
        detailToServer.qrScan=scanData
        detailToServer.location = BasicFunctions.locationArrayList


        progressBar.value = true
        webServices.scan(detailToServer).enqueue(object : Callback<ScanCheckPointResponse> {
            override fun onResponse(call: Call<ScanCheckPointResponse>, response: Response<ScanCheckPointResponse>) {
                progressBar.value = false
                if (response.code() == 200) {
                    onSuccessfullyScan.value = response.body()
                } else {
                    feedBackMessage.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                }
            }

            override fun onFailure(call: Call<ScanCheckPointResponse>, t: Throwable) {
                progressBar.value = false
                feedBackMessage.value = t!!.message
            }
        })
    }


}