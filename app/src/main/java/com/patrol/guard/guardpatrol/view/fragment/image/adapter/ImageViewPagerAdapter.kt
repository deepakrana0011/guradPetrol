package com.patrol.guard.guardpatrol.view.fragment.image.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.patrol.guard.guardpatrol.R

class ImageViewPagerAdapter(var mContext: Activity?, listOfImages: MutableList<Bitmap?>) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater
    var listOfImages: MutableList<Bitmap?>
    lateinit var handler:ClickHandler
    init {
        mLayoutInflater = mContext!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.listOfImages=listOfImages
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.adapter_image_view_pager, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val imageViewCross = itemView.findViewById<ImageView>(R.id.imageViewCross)
        if(listOfImages.get(position)!=null){
            imageView.setImageBitmap(listOfImages.get(position))
            imageViewCross.visibility=View.VISIBLE
        }else{
            imageViewCross.visibility=View.GONE
        }


        imageViewCross.setOnClickListener({
            handler.crossClick(position)
        })
        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return listOfImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }


    fun itemClickListener(handler:ClickHandler){
        this.handler=handler
    }

    interface ClickHandler{
        fun crossClick(position: Int)
    }
}