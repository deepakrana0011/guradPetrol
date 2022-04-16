package com.patrol.guard.guardpatrol.view.activity.home.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.utils.FrequentFunctions

class NavigationAdapter(activity: Activity, frequentFunctions: FrequentFunctions) :
    RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    var items: Array<String>
    var frequentFunctions: FrequentFunctions
    var activity: Activity
    lateinit var listener: ClickListnerHandler

    init {
        items = activity.getResources().getStringArray(R.array.nav_item)
        this.frequentFunctions = frequentFunctions
        this.activity = activity;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_navigation, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 1) {
            holder.textViewItemName.setText(frequentFunctions.customizeString(activity, items.get(position), 16, 17))
        } else {
            holder.textViewItemName.setText(items.get(position))
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(it,position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewItemName: TextView

        init {
            textViewItemName = itemView.findViewById(R.id.textViewItemName)
        }
    }

    fun clickListener(listener: ClickListnerHandler) {

        this.listener = listener
    }

    interface ClickListnerHandler {
        fun onItemClick(view:View,position: Int)
    }
}