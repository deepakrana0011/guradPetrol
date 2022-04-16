package com.patrol.guard.guardpatrol.view.fragment.about

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentAboutBinding
import com.patrol.guard.guardpatrol.model.about.AboutResponse
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject


class AboutFragment : BaseFragment() {
    val homeViewModel: HomeViewModel by inject()
    val basicFunctions: BasicFunctions by inject()
    var binding: FragmentAboutBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentAboutBinding>(inflater, R.layout.fragment_about, container, false)
        initView()
        binding?.homeViewModel= homeViewModel

        return binding!!.root
    }

    private fun initView() {
        homeViewModel.fetchAboutInfo(true)
        initObserver()
    }

    fun initObserver(){
        homeViewModel.feedBackMessage.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {

                Toast.makeText(this@AboutFragment.requireContext(),message?:"",Toast.LENGTH_SHORT).show()
                //basicFunctions.showFeedbackMessage(activity!!, binding!!.root, message!!)
            }
        })
        homeViewModel.onSuccessfullyAbout.observe(viewLifecycleOwner, object : Observer<AboutResponse> {
            override fun onChanged(aboutResponse: AboutResponse?) {
                var fullAddress=""
                aboutResponse?.let {
                  fullAddress= it.about.address1
                  fullAddress = fullAddress+"\n"+ it.about.address2
                  fullAddress = fullAddress+"\n"+ it.about.country+" "+it.about.pincode
                  binding?.email=  it.about.email
                  binding?.phone = it.about.phone
                  binding?.address= fullAddress

                }
            }
        })

        homeViewModel.progressBar.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(activity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })
    }
}