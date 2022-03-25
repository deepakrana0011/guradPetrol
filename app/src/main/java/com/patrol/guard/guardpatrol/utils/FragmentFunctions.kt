package com.patrol.guard.guardpatrol.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.patrol.guard.guardpatrol.R

class FragmentFunctions {
    lateinit var transaction: FragmentTransaction
    fun replaceFragment(activity: AppCompatActivity, fragment: Fragment, name: String,containerId:Int) {
        transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(containerId, fragment, name).commit()
    }
}