package com.patrol.guard.guardpatrol.view.activity.incidents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.getIncidentsList.IncidentList
import kotlinx.android.synthetic.main.adapter_incidents.view.*

class IncidentAdapter(incidentList: List<IncidentList>?) : RecyclerView.Adapter<IncidentAdapter.ViewHolder>() {

    var incidentList: List<IncidentList>
    lateinit var itemClickListener: ItemClickListener

    init {
        this.incidentList = incidentList!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_incidents, null);
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return incidentList.size;

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewIncidentName.setText(incidentList.get(position).name)
        if (incidentList.get(position).bitMap != null) {
            holder.imageViewCross.visibility = View.VISIBLE
            holder.imageViewIncident.scaleType=ImageView.ScaleType.FIT_XY
            holder.imageViewIncident.setImageBitmap(incidentList.get(position).bitMap)
        } else {
            holder.imageViewIncident.scaleType=ImageView.ScaleType.CENTER_INSIDE
            holder.imageViewIncident.setImageResource(R.drawable.ic_camera)
            holder.imageViewCross.visibility = View.GONE
        }
        holder.cardView.setOnClickListener {
            itemClickListener.onImageClick( incidentList.get(position))
        }

        holder.imageViewCross.setOnClickListener {
            itemClickListener.onImageCrossClick(incidentList.get(position))
        }




        holder.checkbox.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                itemClickListener.onCheckBoxClick(isChecked,incidentList.get(position))
            }
        })
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewIncidentName = itemView.textViewIncidentName
        var cardView = itemView.cardView
        var imageViewIncident = itemView.imageViewIncident
        var imageViewCross = itemView.imageViewCross
        var checkbox = itemView.checkbox
    }


    fun itemCLickHandler(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun customNotify(incidentList: List<IncidentList>) {
        this.incidentList = incidentList
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onImageClick(incedent: IncidentList)
        fun onImageCrossClick(incedent: IncidentList)
        fun onCheckBoxClick(isChecked:Boolean,incedent: IncidentList)
    }
}