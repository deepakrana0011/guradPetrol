package com.patrol.guard.guardpatrol.view.activity.timeline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.ActivityTimeLineBinding
import com.patrol.guard.guardpatrol.model.endTrip.EndTripResponse
import com.patrol.guard.guardpatrol.model.guardTour.CheckPoint
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.DateFunctions
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.fragment.homFragment.adapter.HomeFragmentAdapter
import com.patrol.guard.guardpatrol.viewModel.TimelineViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject

class TimeLineActivity : BaseActivity(), TimeLineViewAdapter.ItemClickListener {
    lateinit var binding: ActivityTimeLineBinding
    val timelineViewModel: TimelineViewModel by inject()
    val basicFunctions: BasicFunctions by inject()
    val dateFunctions: DateFunctions by inject()
    var adapter :TimeLineViewAdapter?=null
    var tripId=""
    var checkPoints: MutableList<CheckPoint> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityTimeLineBinding>(this, R.layout.activity_time_line)
        binding.timelineViewModel = timelineViewModel
        intent?.let {
            tripId = it.getStringExtra("trip_id").toString()
        }

        intitView()
    }

    private fun intitView() {
        customToolBarWithBack(binding.toolBar, getString(R.string.timeline))
        callApi()
        initiateObserver()
    }

    private fun callApi() {
        timelineViewModel.getTimeLineResponse(tripId, true)
    }

    fun initiateObserver() {
        timelineViewModel.feedBackMessage.observe(this, object : Observer<String> {
            override fun onChanged(message: String?) {
                if (refreshLayoutView.isRefreshing) {
                    refreshLayoutView.isRefreshing = false
                }
                binding.message = message
                //basicFunctions.showFeedbackMessage(activity!!, binding!!.root, message!!)
            }
        })

        homeViewModel.progressBar.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(this@TimeLineActivity, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })

        homeViewModel.onSuccessfullyGetTourResponse.observe(this, object :
            Observer<GuardTourResponse> {
            override fun onChanged(response: GuardTourResponse?) {
                binding.dateFunctions = dateFunctions

                binding.message = ""
                binding.tourDetail = response!!.tour
                checkPoints = response!!.tour!!.checkPoints!!


              /*  var currentCheckPointPosition=response.tour!!.checkPointPosition!!


                if(response!!.tour!!.status==1){
                    AllLocalHandler.singleTripDetail.tripId=response!!.tour!!.id!!
                    AllLocalHandler.singleTripDetail.currentCheckPoint=response.tour!!.checkPoints!!.get(currentCheckPointPosition).id
                }else{
                    AllLocalHandler.singleTripDetail.tripId=""
                    AllLocalHandler.singleTripDetail.currentCheckPoint=""
                }
                setAdapter()*/

                setAdapter()

            }


        }) }
    fun setAdapter() {
        adapter = TimeLineViewAdapter(this, checkPoints, dateFunctions)
        adapter?.itemClickHandler(this)
        binding.recyclerViewDutyProgress.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDutyProgress.adapter = adapter
    }
}