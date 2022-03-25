package com.patrol.guard.guardpatrol.viewModel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.login.LoginDetailToServer
import com.patrol.guard.guardpatrol.model.login.LoginResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.utils.Validations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application,webServices: WebServices,frequentFunctions: FrequentFunctions,sharedPref: SharedPref) : AndroidViewModel(application) {
    var webServices: WebServices
    var context:Application
    var frequentFunctions:FrequentFunctions
    var sharedPref:SharedPref
    init {
        this.webServices = webServices
        this.context=application
        this.frequentFunctions=frequentFunctions
        this.sharedPref=sharedPref
    }
    var guardId = MutableLiveData<String>()
    var guardPin = MutableLiveData<String>()
    var onError = MutableLiveData<String>()
    var loginSuccess = MutableLiveData<LoginResponse>()
    var showProgress= MutableLiveData<Boolean>()

    fun funtionClick(view: View) {
        if (Validations.isFieldEmpty(guardId.value.toString())) {
            onError.value = context.getString(R.string.emtpty_guard_id)
        } else if (Validations.isFieldEmpty(guardPin.value.toString())) {
            onError.value = context.getString(R.string.empty_guard_pin)
        } else {
            showProgress.value=true
            var loginDetailToServer=LoginDetailToServer()
            loginDetailToServer.deviceId="1233"
            loginDetailToServer.gaurdId=guardId.value.toString()
            loginDetailToServer.pin=guardPin.value.toString()
            webServices.login(loginDetailToServer).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    showProgress.value=false
                    if (response.code() == 200) {
                        sharedPref.setString(Constants.USER_ID,response!!.body()!!.gaurd!!._id!!)
                        sharedPref.setString(Constants.GUARD_ID,response!!.body()!!.gaurd!!.gaurdId!!)
                        sharedPref.setString(Constants.SUPERVISOR_ID,response!!.body()!!.gaurd!!.supervisorId!!)
                        sharedPref.setString(Constants.jwtToken,response!!.body()!!.gaurd!!.jwtToken!!)
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeActivity)
                    } else {
                        onError.value = frequentFunctions.errorMessage(response.errorBody()!!.string())
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                   showProgress.value=false
                    onError.value = t!!.message
                }
            })
        }
    }
}