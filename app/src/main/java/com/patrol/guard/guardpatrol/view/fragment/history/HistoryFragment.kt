package com.patrol.guard.guardpatrol.view.fragment.history

import android.app.DatePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.FragmentHistoryBinding
import com.patrol.guard.guardpatrol.databinding.FragmentHomeBinding
import com.patrol.guard.guardpatrol.databinding.FragmentSettingBinding
import com.patrol.guard.guardpatrol.model.endTrip.EndTripResponse
import com.patrol.guard.guardpatrol.model.guardTour.CheckPoint
import com.patrol.guard.guardpatrol.model.guardTour.GuardTourResponse
import com.patrol.guard.guardpatrol.model.history.TripHistoryResponse
import com.patrol.guard.guardpatrol.model.scanCheckPoint.ScanCheckPointResponse
import com.patrol.guard.guardpatrol.model.startTrip.StartTripResponse
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.DateFunctions
import com.patrol.guard.guardpatrol.utils.SharedPref
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.activity.timeline.TimeLineViewAdapter
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.view.fragment.history.adapter.HistoryFragamentAdapter
import com.patrol.guard.guardpatrol.viewModel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import java.util.*

class HistoryFragment : BaseFragment() {
    val  c = Calendar.getInstance()
    var month = c.get(Calendar.MONTH)
    var year = c.get(Calendar.YEAR)
    var day = c.get(Calendar.DAY_OF_MONTH)
    var checkPoints: MutableList<CheckPoint> = ArrayList()
    val dateFunctions: DateFunctions by inject()
    val sharedPref: SharedPref by inject()
    val homeViewModel: HomeViewModel by inject()
    val basicFunctions: BasicFunctions by inject()
    lateinit var historyFragmentAdapter: HistoryFragamentAdapter
    lateinit var binding: FragmentHistoryBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentHistoryBinding>(inflater, R.layout.fragment_history, container, false)
        intiView()
        return binding.root

    }

    private fun intiView() {
        initiateObserver()
        setData()
        binding.linerLayoutDate.setOnClickListener {
            openDatePicker()
        }
    }

    private fun setData() {
       month = c.get(Calendar.MONTH)
       day =  c.get(Calendar.DAY_OF_MONTH)
       year =  c.get(Calendar.YEAR)
       onDateSet(month, day,year)

    }


    fun onDateSet(month:Int, day: Int, year : Int ){
        binding.month= dateFunctions.formatMonth((month+1).toString())
        binding.day = day.toString()
        binding.year = year.toString()
        val date = year.toString()+"-"+(month+1)+"-"+ binding.day
        homeViewModel.fetchTourHistory(date, true)
    }

    fun openDatePicker(){
        val dpd = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            Log.e("date=>",dayOfMonth.toString()+ "=>"+ monthOfYear.toString()+"=>"+ year )
            c.set(year,monthOfYear, dayOfMonth)
            setData() }, year, month, day)

        dpd.show()
    }

    fun initiateObserver() {
        homeViewModel.feedBackMessage.observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String?) {

                binding.message = message
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

        homeViewModel.onSuccessfullyHistory.observe(viewLifecycleOwner, object :
            Observer<TripHistoryResponse> {
            override fun onChanged(response: TripHistoryResponse?) {
                binding.dateFunctions = dateFunctions
                binding.message = ""
                if (response?.tripList?.isEmpty() == true){
                    binding.message = "There is no record for the selectd date"
                }
                else {
                    binding.tourDetail = response?.tripList?.get(0)
                    binding.guardId = sharedPref.getString(Constants.GUARD_ID)
                    checkPoints = response?.tripList?.get(0)?.checkPoints!!
                    setAdapter()
                }
            }
        })
    }

    fun setAdapter() {
        historyFragmentAdapter = HistoryFragamentAdapter(this.requireActivity(), checkPoints, dateFunctions)
      //  historyFragmentAdapter.itemClickHandler()
        binding.recyclerViewDutyProgress.layoutManager = LinearLayoutManager(this.requireContext())
        binding.recyclerViewDutyProgress.adapter = historyFragmentAdapter
    }

}