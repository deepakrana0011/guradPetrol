package com.patrol.guard.guardpatrol.view.fragment.intoduction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.view.fragment.intoduction.adapter.CustomPagerAdapter
import kotlinx.android.synthetic.main.fragment_introduction.*
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import org.koin.android.ext.android.inject


class IntroductionFragment : BaseFragment() {
    val frequentFunctions:FrequentFunctions by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_introduction, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        customizeText()
        skipClick()
    }

    //<editor-fold desc="view pager adapter set here">
    fun setAdapter() {
        val introductionImages = resources.obtainTypedArray(R.array.introduction_screens)
        val adapter = CustomPagerAdapter(activity, introductionImages);
        viewPager.adapter = adapter
    }
    //</editor-fold>

    //<editor-fold desc="text customize here">
    fun customizeText() {
        textViewGuradPatrol.setText(
            frequentFunctions.customizeString(activity!!, getString(R.string.guard_patrol), 10, 11),
            TextView.BufferType.SPANNABLE
        )
    }
    //</editor-fold>

    //<editor-fold desc="skip click functionality implemented here">
    fun skipClick() {
        buttonSkip.setOnClickListener { v: View ->
            findNavController().navigate(R.id.action_introductionFragment_to_loginFragment)
        }
    }
    //</editor-fold>
}


