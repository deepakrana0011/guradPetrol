package com.patrol.guard.guardpatrol.view.fragment.history.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.guardTour.CheckPoint
import com.patrol.guard.guardpatrol.utils.DateFunctions
import com.patrol.guard.guardpatrol.view.activity.timeline.TimeLineViewAdapter

class HistoryFragamentAdapter (
    activity: Activity,
    checkPoints: MutableList<CheckPoint>,
    dateFunctions: DateFunctions
) :
    RecyclerView.Adapter<HistoryFragamentAdapter.ViewHolder>() {
    var activity: Activity;
    var checkPoints: MutableList<CheckPoint>
    var dateFunctions: DateFunctions
    lateinit var handler: ItemClickListener

    var currentCheckPoint=0;

    init {
        this.activity = activity
        this.checkPoints = checkPoints
        this.dateFunctions = dateFunctions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.timeline_adapter, null)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return checkPoints.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkPointName.setText(checkPoints.get(position).name)

        // if check point has completed
        if (checkPoints.get(position).status!! == 1) {
            holder.checkPointName.setTextColor(ContextCompat.getColor(activity, R.color.colorGray))
            holder.imageViewCircleDot.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_black_circle))
            if (checkPoints.get(position).checkType!! == 0) {
                holder.linearLayoutNfc.visibility = View.GONE
                holder.linearLayoutScan.visibility = View.GONE
                holder.linearLayoutIncident.visibility = View.GONE
                holder.linearLayoutMessage.visibility = View.GONE
                if (checkPoints.get(position).date != null) {
                    holder.textViewStartEndTime.visibility = View.VISIBLE
                    holder.textViewStartEndTime.setText(dateFunctions.getStartEndTime(checkPoints.get(position).date!!))
                } else {
                    holder.textViewStartEndTime.visibility = View.GONE
                }
            } else if (checkPoints.get(position).checkType!! == 1) {
                holder.textViewStartEndTime.visibility = View.GONE
                if (checkPoints.get(position).isnfc!!) {
                    holder.textViewNFCTime.setText(
                        activity.getString(R.string.nfc) + " | " + dateFunctions.getStartEndTime(
                            checkPoints.get(position).nfcScanDate!!
                        ) + " | " + activity.getString(R.string.check_point_successful)
                    )
                    holder.textViewNFCTime.setTextColor(ContextCompat.getColor(activity, R.color.colorGray))
                    holder.imageViewNFC.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_nfc_gray))
                    holder.nfcVisible()
                } else {
                    holder.linearLayoutNfc.visibility = View.GONE
                }

                if (checkPoints.get(position).isqr!!) {
                    holder.textViewScanTime.setText(
                        activity.getString(R.string.qr_code) + " | " + dateFunctions.getStartEndTime(
                            checkPoints.get(position).qrScanDate!!
                        ) + " | " + activity.getString(R.string.check_point_successful)
                    )
                    holder.textViewScanTime.setTextColor(ContextCompat.getColor(activity, R.color.colorGray))
                    holder.imageViewQR.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_qr_gray))
                    holder.scanVisible()
                } else {
                    holder.linearLayoutScan.visibility = View.GONE
                }
            }
            else if (checkPoints.get(position).checkType == 2){
                holder.textViewStartEndTime.visibility = View.GONE
                if (!checkPoints.get(position).audio.isNullOrEmpty() || (!checkPoints.get(position).message.isNullOrEmpty()||
                            checkPoints.get(position).name.toString().contains("Message"))){
                    holder.imageViewCircleDot.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_green_circle))

                    holder.textViewMessagetTime.setText(dateFunctions.getStartEndTime(
                        checkPoints.get(position).date!!)
                    )
                    holder.textViewMessagetTime.setTextColor(ContextCompat.getColor(activity, R.color.colorGray))
                    holder.messageVisible()
                }
                else if (!checkPoints.get(position).incidents.isNullOrEmpty()){
                    holder.textViewStartEndTime.visibility = View.GONE

                    holder.imageViewCircleDot.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_blue_circle))

                    holder.textViewIncidentTime.setText(dateFunctions.getStartEndTime(
                        checkPoints.get(position).date!!)
                    )
                    holder.textViewIncidentTime.setTextColor(ContextCompat.getColor(activity, R.color.colorGray))
                    holder.incidentVisible()

                }

            }


        }

        if (position == checkPoints.size - 1) {
            holder.view.visibility = View.GONE
        } else {
            holder.view.visibility = View.VISIBLE
        }
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkPointName = itemView.findViewById<TextView>(R.id.checkPointName)
        var view = itemView.findViewById<View>(R.id.view)
        var linearLayoutNfc = itemView.findViewById<LinearLayout>(R.id.linearLayoutNfc)
        var linearLayoutScan = itemView.findViewById<LinearLayout>(R.id.linearLayoutScan)
        var linearLayoutStartEndTime = itemView.findViewById<LinearLayout>(R.id.linearLayoutStartEndTime)
        var linearLayoutGeoFence = itemView.findViewById<LinearLayout>(R.id.linearLayoutGeoFence)
        var imageViewCircleDot = itemView.findViewById<ImageView>(R.id.imageViewCircleDot)
        var imageViewGEO = itemView.findViewById<ImageView>(R.id.imageViewGEO)
        var imageViewNFC = itemView.findViewById<ImageView>(R.id.imageViewNFC)
        var imageViewQR = itemView.findViewById<ImageView>(R.id.imageViewQR)
        var textViewStartEndTime = itemView.findViewById<TextView>(R.id.textViewStartEndTime)
        var textViewNFCTime = itemView.findViewById<TextView>(R.id.textViewNFCTime)
        var textViewScanTime = itemView.findViewById<TextView>(R.id.textViewScanTime)
        var textViewGeoFenceTime = itemView.findViewById<TextView>(R.id.textViewGeoFenceTime)

        var linearLayoutIncident = itemView.findViewById<LinearLayout>(R.id.linearLayoutIncident)
        var linearLayoutMessage = itemView.findViewById<LinearLayout>(R.id.linearLayoutMessage)

        var textViewIncidentTime = itemView.findViewById<TextView>(R.id.textIncidentTime)
        var textViewMessagetTime = itemView.findViewById<TextView>(R.id.textMessageTime)

        var textActionIncident= itemView.findViewById<TextView>(R.id.textIncidentAction)
        var textActionMessage= itemView.findViewById<TextView>(R.id.textMessageAction)

        fun messageVisible(){
            linearLayoutMessage.visibility = View.VISIBLE
            linearLayoutIncident.visibility = View.GONE
            linearLayoutNfc.visibility = View.GONE
            linearLayoutScan.visibility = View.GONE

        }
        fun incidentVisible(){
            linearLayoutMessage.visibility = View.GONE
            linearLayoutIncident.visibility = View.VISIBLE
            linearLayoutNfc.visibility = View.GONE
            linearLayoutScan.visibility = View.GONE

        }
        fun scanVisible(){
            linearLayoutMessage.visibility = View.GONE
            linearLayoutIncident.visibility = View.GONE
            linearLayoutNfc.visibility = View.GONE
            linearLayoutScan.visibility = View.VISIBLE

        }
        fun nfcVisible(){
            linearLayoutMessage.visibility = View.VISIBLE
            linearLayoutIncident.visibility = View.GONE
            linearLayoutNfc.visibility = View.GONE
            linearLayoutScan.visibility = View.VISIBLE

        }

    }


    interface ItemClickListener {
        fun viewClick(position: Int)
        /* fun startClick()
         fun checkPointClick(position: Int)
         fun endClick(position: Int)*/
    }

    fun itemClickHandler() {
        this.handler = handler
    }

}