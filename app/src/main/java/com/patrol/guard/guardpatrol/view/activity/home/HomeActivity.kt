package com.patrol.guard.guardpatrol.view.activity.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FragmentFunctions
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.utils.LocationInternetFunctions
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.home.adapter.NavigationAdapter
import com.patrol.guard.guardpatrol.view.activity.incidents.IncidentsActivity
import com.patrol.guard.guardpatrol.view.activity.messages.MessagesActivity
import com.patrol.guard.guardpatrol.view.fragment.about.AboutFragment
import com.patrol.guard.guardpatrol.view.fragment.history.HistoryFragment
import com.patrol.guard.guardpatrol.view.fragment.homFragment.HomeFragment
import com.patrol.guard.guardpatrol.view.fragment.setting.SettingFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import org.koin.android.ext.android.inject

class HomeActivity : BaseActivity(), NavigationAdapter.ClickListnerHandler, View.OnClickListener {
    val fragmentFunctions: FragmentFunctions by inject()
    lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        activity = this;
        if (!LocationInternetFunctions.mInstance.isGpsOn(activity!!)) {
            LocationInternetFunctions.mInstance.deafultGoogleLocationDialog(activity!!)
        } else {
            setGoogleClient()
        }
        checkLatLong()
        setUpClickListeners()
        setUpData()
        setUpToolBar()
    }

    private fun setUpClickListeners() {
        relativeLayoutIncidents.setOnClickListener(this)
        relativeLayoutMessages.setOnClickListener(this)
        imageViewSos.setOnClickListener(this)
        imageViewTorch.setOnClickListener(this)
    }

    private fun setUpToolBar() {
        customToolBarWithMenu(toolBar, drawer_layout)
    }

    fun setUpData() {
        textViewGuardPatrol.setText(
            frequentFunctions.customizeString(
                this, getString(R.string.guard_patrol), 10, 11
            ), TextView.BufferType.SPANNABLE
        )
        val adapter = NavigationAdapter(this, frequentFunctions)
        adapter.clickListener(this)
        recyclerViewNavigation.layoutManager = LinearLayoutManager(this)
        recyclerViewNavigation.adapter = adapter
        imageViewCross.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onItemClick(view: View, position: Int) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutContainer)
        when (position) {
            0 -> {
                if (currentFragment !is HomeFragment) {
                    fragmentFunctions.replaceFragment(
                        this,
                        HomeFragment(),
                        getString(R.string.guard_patrol),
                        R.id.frameLayoutContainer
                    )
                    textViewTimeLine!!.visibility = View.VISIBLE
                    textViewTitleName!!.setText(
                        frequentFunctions.customizeString(
                            this, getString(R.string.guard_patrol), 10, 11
                        ), TextView.BufferType.SPANNABLE
                    )
                }
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            1 -> {
                if (currentFragment !is HistoryFragment) {
                    fragmentFunctions.replaceFragment(
                        this,
                        HistoryFragment(),
                        getString(R.string.history),
                        R.id.frameLayoutContainer
                    )
                    textViewTimeLine!!.visibility = View.GONE
                    textViewTitleName!!.setText(getString(R.string.history))
                }
                drawer_layout.closeDrawer(GravityCompat.START)
            }

            2 -> {
                if (currentFragment !is AboutFragment) {
                    fragmentFunctions.replaceFragment(
                        this,
                        AboutFragment(),
                        getString(R.string.about),
                        R.id.frameLayoutContainer
                    )
                    relativeLayoutBottom.visibility = View.GONE
                    textViewTimeLine!!.visibility = View.GONE
                    textViewTitleName!!.setText(getString(R.string.about))
                }
                drawer_layout.closeDrawer(GravityCompat.START)
            }

            3 -> {
                if (currentFragment !is SettingFragment) {
                    fragmentFunctions.replaceFragment(
                        this,
                        SettingFragment(),
                        getString(R.string.settings),
                        R.id.frameLayoutContainer
                    )
                    relativeLayoutBottom.visibility = View.GONE
                    textViewTimeLine!!.visibility = View.GONE
                    textViewTitleName!!.setText(getString(R.string.settings))
                }
                drawer_layout.closeDrawer(GravityCompat.START)
            }
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.relativeLayoutIncidents -> {
                startActivity(Intent(this, IncidentsActivity::class.java))
            }

            R.id.relativeLayoutMessages -> {
                startActivity(Intent(this, MessagesActivity::class.java))
            }

            R.id.imageViewSos -> {
                showSOSEventDialog()
            }

            R.id.imageViewTorch -> {
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
        }
    }


    fun turnOnOffFlashLight() {
        if (!frequentFunctions.isFlashLightOn) {
            frequentFunctions.turnOnTheLight(this@HomeActivity)
        } else {
            frequentFunctions.turnOfFlashLight()
        }

    }

    fun checkLatLong() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (latitude.equals("")) {
                    handler.postDelayed(this, 10)
                } else {
                    fragmentFunctions.replaceFragment(
                        this@HomeActivity,
                        HomeFragment(),
                        getString(R.string.guard_patrol),
                        R.id.frameLayoutContainer
                    )
                    handler.removeCallbacks(this)
                }
            }
        }
        handler.postDelayed(runnable, 10)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_CHECK_LOCATION_STATUS ->
                when (resultCode) {
                    Activity.RESULT_OK -> setGoogleClient()
                    Activity.RESULT_CANCELED -> LocationInternetFunctions.mInstance.deafultGoogleLocationDialog(
                        activity!!
                    )
                }
            Constants.REQUEST_PERMISSION_SETTING -> setGoogleClient()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            111 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                        setGoogleClient()
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        val showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                        val showRationaleCamer = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                        if (!showRationale&&permissions[0]==Manifest.permission.ACCESS_FINE_LOCATION) {
                            alertDialogWithOKButton(getString(R.string.permission), getString(R.string.locationPermission)
                            )
                        } else {
                            setGoogleClient()
                        }

                        if (!showRationaleCamer&&permissions[0]==Manifest.permission.CAMERA) {
                            alertDialogWithOKButton(
                                getString(R.string.permission),
                                getString(R.string.camera_permission)
                            )
                        }
                    }
                }
            }
        }
    }

}
