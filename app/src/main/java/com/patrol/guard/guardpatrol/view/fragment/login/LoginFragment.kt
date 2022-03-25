package com.patrol.guard.guardpatrol.view.fragment.login

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentLoginBinding
import com.patrol.guard.guardpatrol.model.login.LoginResponse
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel


class LoginFragment : BaseFragment() {
    val loginViewModel: LoginViewModel by viewModel()
    val basicFunctions: BasicFunctions by inject()
    val frequentFunctions: FrequentFunctions by inject()
    val sharedPref: SharedPref by inject()
    var binding: FragmentLoginBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false)
        binding!!.loginViewModel = loginViewModel
        return binding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        customizeText()
        setUpObserver()
    }


    fun setUpObserver() {

        loginViewModel.onError.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(t: String?) {
                basicFunctions.showFeedbackMessage(activity!!, binding!!.root, t!!)
            }

        })

        loginViewModel.showProgress.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                frequentFunctions.hideKeyBoard(activity!!,binding!!.root)
                if (t!!) {
                    basicFunctions.showProgressBar(activity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })

    }

    //<editor-fold desc="text customize here">
    fun customizeText() {
        textViewLoginAccessTo.setText(
            frequentFunctions.customizeString(
                activity!!,
                getString(R.string.login_access_to),
                1,
                2
            ), TextView.BufferType.SPANNABLE
        )
        textViewGuradPatrol.setText(
            frequentFunctions.customizeString(
                activity!!,
                getString(R.string.guard_patrol),
                10,
                11
            ), TextView.BufferType.SPANNABLE
        )
    }
    //</editor-fold>


}