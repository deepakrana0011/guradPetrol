package com.patrol.guard.guardpatrol.view.fragment.homFragment

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentHomeBinding
import com.patrol.guard.guardpatrol.model.LocationData
import com.patrol.guard.guardpatrol.model.endTrip.EndTripResponse
import com.patrol.guard.guardpatrol.model.guardTour.CheckPoint
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.*
import com.patrol.guard.guardpatrol.utils.geoFencing.GeofenceBroadcastReceiver
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.chooseScanOrNFC.ScanorNfcActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.view.fragment.homFragment.adapter.HomeFragmentAdapter
import com.patrol.guard.guardpatrol.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject

class HomeFragment : BaseFragment(), HomeFragmentAdapter.ItemClickListener, OnCompleteListener<Void> {


    val frequentFunctions: FrequentFunctions by inject()
    val homeViewModel: HomeViewModel by inject()
    val basicFunctions: BasicFunctions by inject()
    val dateFunctions: DateFunctions by inject()
    val sharedPref: SharedPref by inject()
    var binding: FragmentHomeBinding? = null
    private var mGeofenceList: MutableList<Geofence>? = ArrayList()
    private var mGeofencePendingIntent: PendingIntent? = null
    private var mGeofencingClient: GeofencingClient? = null
    lateinit var geoFenceReciever: BroadcastReceiver
    var adapter :HomeFragmentAdapter?=null


    var checkPoints: MutableList<CheckPoint> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false)
        binding!!.guardId = sharedPref.getString(Constants.GUARD_ID)
        homeViewModel.getGuardTour(BaseActivity.latitude, BaseActivity.longtiude, true)

        mGeofencingClient = LocationServices.getGeofencingClient(activity!!)
       // registerBroadCa
        // stReciever()


        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        initiateObserver()
        refreshLayout()

        var location= sharedPref.getLocationArray()

     //   BasicFunctions.locationArrayList = ArrayList()

    }


    fun refreshLayout() {
        refreshLayoutView.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                homeViewModel.getGuardTour(BaseActivity.latitude, BaseActivity.longtiude, false)
            }
        })
    }

    fun initiateObserver() {
        homeViewModel.feedBackMessage.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {
                if (refreshLayoutView.isRefreshing) {
                    refreshLayoutView.isRefreshing = false
                }
                binding!!.message = message
                //basicFunctions.showFeedbackMessage(activity!!, binding!!.root, message!!)
            }
        })

        homeViewModel.progressBar.observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(activity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })

        homeViewModel.onSuccessfullyGetTourResponse.observe(viewLifecycleOwner, object : Observer<GuardTourResponse> {
            override fun onChanged(response: GuardTourResponse?) {
                binding!!.dateFunctions = dateFunctions
                if (refreshLayoutView.isRefreshing) {
                    refreshLayoutView.isRefreshing = false
                }
                binding!!.message = ""
                binding!!.tourDetail = response!!.tour
                checkPoints = response!!.tour!!.checkPoints!!


                var currentCheckPointPosition=response.tour!!.checkPointPosition!!
                clearLocationData(response)
                if (currentCheckPointPosition>0){
                    (activity as HomeActivity?)?.getSingleUpdate()
                    (activity as HomeActivity?)?.startUpdates()
                    setServiceEnable(true)
                }
                if(response!!.tour!!.status==1){
                    AllLocalHandler.singleTripDetail.tripId=response!!.tour!!.id!!
                    AllLocalHandler.singleTripDetail.currentCheckPoint=response.tour!!.checkPoints!!.get(currentCheckPointPosition).id
                }else{
                    AllLocalHandler.singleTripDetail.tripId=""
                    AllLocalHandler.singleTripDetail.currentCheckPoint=""
                }
                setAdapter()

                //Comment this code for geo fence
               /* if(response.tour!!.checkPoints!!.get(currentCheckPointPosition).isgeoLoc!!){
                    createGeoFenceList(response.tour!!.checkPoints!!.get(currentCheckPointPosition).geolat!!.toDouble(),response.tour!!.checkPoints!!.get(currentCheckPointPosition).geolong!!.toDouble(),response.tour!!.checkPoints!!.get(currentCheckPointPosition).id!!)
                }*/
            }
        })

        homeViewModel.onSuccessfullyStartTrip.observe(viewLifecycleOwner, object : Observer<StartTripResponse> {
            override fun onChanged(response: StartTripResponse?) {
                setServiceEnable(true)
                (activity as HomeActivity?)?.getSingleUpdate()
                (activity as HomeActivity?)?.startUpdates()
                homeViewModel.getGuardTour(BaseActivity.latitude, BaseActivity.longtiude, true)
            }
        })

        homeViewModel.onSuccessfullyEndTrip.observe(viewLifecycleOwner, object : Observer<EndTripResponse> {
            override fun onChanged(response: EndTripResponse?) {
                sharedPref.clearLocationData()
                (activity as HomeActivity?)?.stopUpdates()
                setServiceEnable(false)
                homeViewModel.getGuardTour(BaseActivity.latitude, BaseActivity.longtiude, true)
            }
        })

        homeViewModel.onSuccessfullyScanGeoFence.observe(viewLifecycleOwner, object : Observer<ScanCheckPointResponse> {
            override fun onChanged(response: ScanCheckPointResponse?) {
                removeGeofences()
                homeViewModel.getGuardTour(BaseActivity.latitude, BaseActivity.longtiude,true)
            }
        })
    }

    private fun clearLocationData(response: GuardTourResponse) {
        val currentCheckPointPosition=response.tour!!.checkPointPosition!!
        if (currentCheckPointPosition>0){
            val currentCheckPoint = response.tour?.checkPoints?.get(currentCheckPointPosition-1)
            currentCheckPoint?.let {
                val status = it.status
                if (status==1){
                    sharedPref.clearLocationData();
                }

            }
        }

    }

    /*
    * setServiceEnable() => In this function we are allowing to start the background service for fetching
    * the Location data
    * */
    fun setServiceEnable(enable:Boolean){
        (activity as HomeActivity?)?.startServiceEnable= enable

    }

    fun setData() {
        BaseActivity.textViewTitleName!!.setText(
            frequentFunctions.customizeString(
                activity!!,
                getString(R.string.guard_patrol),
                10,
                11
            )
        )
        binding!!.textViewZone.setText(frequentFunctions.customizeString(activity!!, getString(R.string.zone), 1, 2))
        binding!!.textViewDutyHours.setText(
            frequentFunctions.customizeString(
                activity!!,
                getString(R.string.duty_hours),
                6,
                7
            )
        )
    }

    fun setAdapter() {
        adapter = HomeFragmentAdapter(activity!!, checkPoints, dateFunctions)
        adapter!!.itemClickHandler(this)
        binding!!.recyclerViewDutyProgress.layoutManager = LinearLayoutManager(activity!!)
        binding!!.recyclerViewDutyProgress.adapter = adapter
    }


    override fun startClick() {
        if (binding!!.tourDetail!!.status == 0) {
            homeViewModel.startTrip(binding!!.tourDetail!!.id.toString(), BaseActivity.latitude, BaseActivity.longtiude)
        }
    }

    override fun checkPointClick(position: Int) {
        if (checkPoints.get(position).status == 2) {
            if (checkPoints.get(position).isnfc!! || checkPoints.get(position).isqr!!) {
                activity!!.startActivity(
                    Intent(activity, ScanorNfcActivity::class.java)
                        .putExtra("checkPointId", checkPoints.get(position).id)
                        .putExtra("nextCheckPointId", checkPoints.get(position + 1).id)
                        .putExtra("isNfc", checkPoints.get(position).isnfc!!)
                        .putExtra("isQr", checkPoints.get(position).isqr)
                )
            }
        }
    }


    override fun endClick(position: Int) {
        if (checkPoints.get(position)!!.status == 2) {
            homeViewModel.endTrip(checkPoints.get(position).id!!, BaseActivity.latitude, BaseActivity.longtiude)
        }
    }

// Comment by Vijay
  /*  fun createGeoFenceList(latitude:Double,longitude:Double,id:String) {
        mGeofenceList!!.add(
            Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(
                    latitude,
                    longitude,
                    Constants.GEOFENCE_HUNDERED_METER_RADIUS
                )
                .setExpirationDuration(Constants.GEO_DURATION)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()
        )
        addGeofences()
    }
*/

    override fun onComplete(task: Task<Void>) {
        if (task.isSuccessful()) {
            //basicFunctions.showFeedbackMessage(activity!!, binding!!.root, "geo fence add successfully"!!)
        } else {
            basicFunctions.showFeedbackMessage(activity!!, binding!!.root, task!!.exception!!.message!!)
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofences(mGeofenceList)
        return builder.build()
    }
    //</editor-fold>


    //<editor-fold desc="pending intent created here for geo fence">
    private fun getGeofencePendingIntent(): PendingIntent {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent!!
        }
        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        mGeofencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return mGeofencePendingIntent!!
    }

    private fun addGeofences() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (checkPermission(permission) > 0) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(permission[0]), Constants.REQUEST_PERMISSION)
        } else {
            if (mGeofenceList!!.size > 0) {
                mGeofencingClient!!.addGeofences(getGeofencingRequest(), getGeofencePendingIntent()).addOnCompleteListener(this!!)
            }
        }
    }

   /* fun registerBroadCastReciever() {
        geoFenceReciever = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                var currentCheckPointPosition=binding!!.tourDetail!!.checkPointPosition!!
                homeViewModel.scanGeoFence(binding!!.tourDetail!!.checkPoints!!.get(currentCheckPointPosition).id!!,binding!!.tourDetail!!.checkPoints!!.get(currentCheckPointPosition+1).id!!)
            }
        }
        activity!!.registerReceiver(geoFenceReciever, IntentFilter(Constants.GEO_FENCE))
    }*/


    override fun onDestroy() {
        super.onDestroy()
        removeGeofences()
    }

    //<editor-fold desc="geofence listening remove here">
    private fun removeGeofences() {
        if (mGeofencePendingIntent != null && mGeofencingClient != null) {
            mGeofencingClient!!.removeGeofences(mGeofencePendingIntent).addOnCompleteListener(this)
        }
    }
    //</editor-fold>
}