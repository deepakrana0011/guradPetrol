package com.patrol.guard.guardpatrol.view.activity.timelinedetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.ActivityTimeLineBinding
import com.patrol.guard.guardpatrol.databinding.ActivityTimeLineDetailBinding
import com.patrol.guard.guardpatrol.model.guardTour.CheckPoint
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.DateFunctions
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.incidents.IncidentsActivity
import com.patrol.guard.guardpatrol.view.activity.messages.MessagesActivity
import org.koin.android.ext.android.inject

class TimeLineDetailActivity : BaseActivity() {

    var checkPoints: MutableList<CheckPoint> = ArrayList()
    val dateFunctions: DateFunctions by inject()
    var position=0
    lateinit var binding: ActivityTimeLineDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityTimeLineDetailBinding>(this, R.layout.activity_time_line_detail)
        checkPoints= intent?.getParcelableArrayListExtra<CheckPoint>(Constants.checkPointList)!!
        checkPoints = BasicFunctions.checkPointListForTimeLine
        position = intent?.getIntExtra(Constants.Position,0)!!
        initView()

    }

    private fun initView() {
        customToolBarWithBack(binding.toolBar, getString(R.string.event_detail))
        Log.e("checkPoint=>", checkPoints.size.toString()+"")
        setData()
        clickEvent()

    }

    private fun clickEvent() {
        binding.imageViewTorch.setOnClickListener {
            val permission = arrayOf(Manifest.permission.CAMERA)
            if (checkPermission(permission) > 0) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission[0]),
                    Constants.REQUEST_PERMISSION
                )
            } else {
                turnOnOffFlashLight()
            }
        }

        binding.imageViewSos.setOnClickListener {
            showSOSEventDialog()
        }

        binding.relativeLayoutIncidents.setOnClickListener {
            startActivity(Intent(this, IncidentsActivity::class.java))
            finish()
        }

        binding.relativeLayoutMessages.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
            finish()
        }
    }

    private fun setData() {
        var checkPoint = checkPoints.get(position)
        checkPoint.let {
            binding.textViewEvntType.text= it.name
            if (!it.audio.isNullOrEmpty() ||
                (!it.message.isNullOrEmpty() || it.name.toString().contains("Message"))){
                    binding.layoutIncidents.visibility = View.GONE
                    binding.layoutMessage.visibility = View.VISIBLE
                    var imageCount =0
                    var audioCount=0
                    it.images?.forEach { it1 ->
                        if (it1.isNotEmpty()){
                            imageCount+=1
                        }
                    }
                if (it.audio.toString().isNotEmpty()){
                    audioCount+=1
                }
                var messageStr=""
                if (imageCount==1){
                   messageStr+=imageCount.toString()+" Image"
                }
                else if (imageCount>1){
                    messageStr+=imageCount.toString()+" Images"
                }
                if (audioCount==1){
                    if (messageStr.isEmpty()){
                        messageStr = audioCount.toString()+" Audio File"
                    }
                    else{
                        messageStr+=", "+audioCount.toString()+" Audio File"
                    }
                }

                if(it.message.toString().isNotEmpty()){
                    if (messageStr.isNotEmpty()){
                        messageStr+= "\n"+it.message
                    }
                    else{
                        messageStr = it.message?:""
                    }
                }

                binding.textViewMessage.text = messageStr

            }
            else{
                binding.layoutIncidents.visibility = View.VISIBLE
                binding.layoutMessage.visibility = View.GONE
                binding.textViewIncidentsMessage.text = it.name
            }

            binding.textViewDateTime.text = it.date?.let { it1 ->
                dateFunctions.getStartEndTime(
                    it1
                )
            }

            binding.textViewLatLong.text = it.gaurdlat.toString()+" ,"+ it.gaurdlong.toString()
            if (getPreviousPositionData().isEmpty()){
                binding.layoutNearestPoint.visibility= View.GONE
            }
            else{
                binding.layoutNearestPoint.visibility= View.VISIBLE
                binding.textViewCheckPointName.text = getPreviousPositionData()
            }

        }

    }

    fun  getPreviousPositionData(): String{
        val currentPosition  = checkPoints.get(position-1)
        var previousPoint =""

        for (i in checkPoints.lastIndexOf((currentPosition)) downTo 0) {
            val previousCheckPoint = checkPoints.get(i)
            if ((previousCheckPoint.audio.isNullOrEmpty() ||
                (previousCheckPoint.message.isNullOrEmpty() ||
                        previousCheckPoint.name.toString().contains("Message"))) && previousCheckPoint.incidents!!.isEmpty()){
                    previousPoint = previousCheckPoint.name?:""
                return previousPoint

            }
        }
        return previousPoint
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        turnOnOffFlashLight()
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale3 =
                                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                            if (!showRationale3) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.cameraPermission2)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}