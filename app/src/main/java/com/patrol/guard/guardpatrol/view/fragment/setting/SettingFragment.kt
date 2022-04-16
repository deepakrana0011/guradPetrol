package com.patrol.guard.guardpatrol.view.fragment.setting
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentSettingBinding
import com.patrol.guard.guardpatrol.model.setting.UpdatePinResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.StartUpActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.viewModel.LoginViewModel
import com.patrol.guard.guardpatrol.viewModel.SettingViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class SettingFragment :BaseFragment(), OnLocaleChangedListener {
    val settingViewModel: SettingViewModel by viewModel()
    val frequentFunctions: FrequentFunctions by inject()
    val sharedPref: SharedPref by inject()
    var firstTime= true
    val basicFunctions: BasicFunctions by inject()
    var binding: FragmentSettingBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate<FragmentSettingBinding>(inflater,R.layout.fragment_setting,container,false)
        intiView()
        binding!!.settingViewModel = settingViewModel
        return  binding!!.root
    }

    private fun intiView() {
        setSpinner()
        setUpObserver()
    }

    private fun setSpinner() {
        var statuts = arrayOf("English", "Spanish")
        var selectedPos=0
        statuts.forEach {
            val selectedLanguage= sharedPref.getString(Constants.LANGUAGE_NAME)
            if (selectedLanguage.equals(it)){
                selectedPos = statuts.indexOf(it)
            }
        }


       val adapter= ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_list_item_1, statuts)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.spinnerLaguage?.adapter = adapter
        binding?.spinnerLaguage?.setSelection(selectedPos)

        binding?.spinnerLaguage?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View,
                position: Int, id: Long) {
                if (!firstTime) {
                    var language = statuts[position]
                    sharedPref.setString(Constants.LANGUAGE_NAME, language)
                    when (language) {
                        "English" -> {
                            sharedPref.setString(Constants.LANGUAGE_CODE, "en")
                            basicFunctions.languageCode = "en"
                            sharedPref.setString(Constants.COUNTRY_CODE,"")
                            basicFunctions.setLocale(
                                this@SettingFragment.requireActivity(),
                                basicFunctions.languageCode,""
                            )
                            restartSplashActivity()


                        }
                        "Spanish" -> {
                            sharedPref.setString(Constants.LANGUAGE_CODE, "es")
                            sharedPref.setString(Constants.COUNTRY_CODE,"ES")
                            basicFunctions.languageCode = "es"
                            basicFunctions.setLocale(
                                this@SettingFragment.requireActivity(),
                                basicFunctions.languageCode, "ES"
                            )
                            restartSplashActivity()
                        }
                    }
                }
                else{
                    firstTime=false
                }

            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
    }

    private fun restartSplashActivity() {
        this.requireContext().startActivity(Intent(this.requireContext(), StartUpActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        this@SettingFragment.activity?.finish()
    }

    fun setUpObserver() {

        settingViewModel.onError.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(t: String?) {
                basicFunctions.showFeedbackMessage(activity!!, binding!!.root, t!!)
            }

        })

        settingViewModel.showProgress.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                frequentFunctions.hideKeyBoard(activity!!,binding!!.root)
                if (t!!) {
                    basicFunctions.showProgressBar(activity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })


        settingViewModel.changePin.observe(viewLifecycleOwner, object : Observer<UpdatePinResponse> {
            override fun onChanged(response: UpdatePinResponse?) {
                sharedPref.clear()
                sharedPref.setString(Constants.LANGUAGE_CODE, basicFunctions.languageCode)
                this@SettingFragment.requireContext().startActivity(Intent(this@SettingFragment.requireContext(), StartUpActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))

            }
        })

    }

    override fun onAfterLocaleChanged() {

    }

    override fun onBeforeLocaleChanged() {

    }
}