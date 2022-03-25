package com.patrol.guard.guardpatrol.view.fragment.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentSettingBinding
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment

class SettingFragment :BaseFragment() {
    var binding: FragmentSettingBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate<FragmentSettingBinding>(inflater,R.layout.fragment_setting,container,false)
        return  binding!!.root
    }
}