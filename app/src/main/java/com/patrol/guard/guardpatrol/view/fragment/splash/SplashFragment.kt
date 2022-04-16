package com.patrol.guard.guardpatrol.view.fragment.splash


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import org.koin.android.ext.android.inject

class SplashFragment : BaseFragment() {
    val sharedPref : SharedPref by inject()
    val s : String?=null
    val basicFunctions: BasicFunctions by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_splash, null)
        setLanguagecode()
        navigateToNextScreen()
        return view
    }
    fun navigateToNextScreen(){
              Handler().postDelayed({
                  if(sharedPref.getString(Constants.USER_ID).equals("")){
                      findNavController().navigate(R.id.action_splashFragment_to_introductionFragment)

                  }else{
                      findNavController().navigate(R.id.action_splashFragment_to_homeActivity)
                  }
              }, 3000)
    }

    fun setLanguagecode(){
        basicFunctions .languageCode = sharedPref.getString(Constants.LANGUAGE_CODE)
        var countryCode= sharedPref.getString(Constants.COUNTRY_CODE)
        basicFunctions.setLocale(this.requireActivity(),basicFunctions.languageCode, countryCode)
    }
}