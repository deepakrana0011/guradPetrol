package com.patrol.guard.guardpatrol.view.activity.messages.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.patrol.guard.guardpatrol.view.fragment.audio.AudioFragment
import com.patrol.guard.guardpatrol.view.fragment.history.HistoryFragment
import com.patrol.guard.guardpatrol.view.fragment.image.ImageFragment

class ImageAudioFragmentAdapter(fm:FragmentManager) :FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return ImageFragment()
            1 -> return AudioFragment()
            else->return HistoryFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}