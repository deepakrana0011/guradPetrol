package com.patrol.guard.guardpatrol.viewModel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.login.LoginDetailToServer
import com.patrol.guard.guardpatrol.model.login.LoginResponse
import com.patrol.guard.guardpatrol.model.setting.SettingDetailToServer
import com.patrol.guard.guardpatrol.model.setting.UpdatePinResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.utils.Validations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingViewModel (application: Application, webServices: WebServices, frequentFunctions: FrequentFunctions, sharedPref: SharedPref) : AndroidViewModel(application) {
    var webServices: WebServices
    var context: Application
    var frequentFunctions: FrequentFunctions
    var sharedPref: SharedPref
    init {
        this.webServices = webServices
        this.context=application
        this.frequentFunctions=frequentFunctions
        this.sharedPref=sharedPref
    }
    var guardId = MutableLiveData<String>()
    var guardPinOld = MutableLiveData<String>()
    var guardPin = MutableLiveData<String>()
    var guardPinComfirm = MutableLiveData<String>()
    var language = MutableLiveData<String>()
    var onError = MutableLiveData<String>()
    var changePin = MutableLiveData<UpdatePinResponse>()
    var showProgress= MutableLiveData<Boolean>()

    fun onChangePinClick(view: View) {
        if (Validations.isFieldEmpty(guardPinOld.value.toString())) {
            onError.value = context.resources.getString(R.string.empty_guard_pin)
        }
        else if (Validations.isFieldEmpty(guardPin.value.toString())){
            onError.value = context.resources.getString(R.string.empty_guard_pin)
        }
        else if (!Validations.isPinConfirm(guardPin.value.toString(), guardPinComfirm.value.toString())) {
            onError.value = context.resources.getString(R.string.new_pin_not_match)

        } else {
            showProgress.value=true
            var settingDetailToServer= SettingDetailToServer()
            settingDetailToServer.oldPin=guardPinOld.value.toString()
            settingDetailToServer.newPin= guardPin.value.toString()
            settingDetailToServer.gaurdid = sharedPref.getString(Constants.USER_ID)
            webServices.updatePin(settingDetailToServer).enqueue(object : Callback<UpdatePinResponse> {
                override fun onResponse(call: Call<UpdatePinResponse>, response: Response<UpdatePinResponse>) {
                    showProgress.value=false
                    if (response.code() == 200) {
                        changePin.value = response.body()
                    } else {
                        onError.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                    }
                }
                override fun onFailure(call: Call<UpdatePinResponse>, t: Throwable) {
                    showProgress.value=false
                    onError.value = t!!.message
                }
            })
        }
    }
}