package com.patrol.guard.guardpatrol.view.fragment.intoduction.adapter

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.patrol.guard.guardpatrol.R

internal class CustomPagerAdapter(var mContext: Activity?, introductionImages: TypedArray) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater
    var introductionImages: TypedArray

    init {
        mLayoutInflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.introductionImages = introductionImages
        this.mContext = mContext
    }

    override fun getCount(): Int {
        return introductionImages.length()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(introductionImages.getResourceId(position, -1))
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}