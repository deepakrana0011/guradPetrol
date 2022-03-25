package com.patrol.guard.guardpatrol.view.fragment.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentAboutBinding
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment

class AboutFragment : BaseFragment() {

    var binding: FragmentAboutBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentAboutBinding>(inflater, R.layout.fragment_about, container, false)
        return binding!!.root
    }
}