package com.patrol.guard.guardpatrol.view.activity.messages

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.databinding.ActivityMessagesBinding
import com.patrol.guard.guardpatrol.model.getIncidentsList.GetIncidentsList
import com.patrol.guard.guardpatrol.model.sendIncidentResponse.SendIncidentResponse
import com.patrol.guard.guardpatrol.model.uploadAudio.UploadAudioResponse
import com.patrol.guard.guardpatrol.model.uploadImage.UploadImageResponse
import com.patrol.guard.guardpatrol.repositry.handler.AllLocalHandler
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.FragmentFunctions
import com.patrol.guard.guardpatrol.view.activity.BaseActivity
import com.patrol.guard.guardpatrol.view.activity.home.HomeActivity
import com.patrol.guard.guardpatrol.view.activity.incidents.IncidentsActivity
import com.patrol.guard.guardpatrol.view.activity.messages.adapter.ImageAudioFragmentAdapter
import com.patrol.guard.guardpatrol.view.fragment.audio.AudioFragment
import com.patrol.guard.guardpatrol.view.fragment.image.ImageFragment
import com.patrol.guard.guardpatrol.viewModel.MessageActivityViewModel

import kotlinx.android.synthetic.main.activity_messages.*
import org.koin.android.ext.android.inject

class MessagesActivity : BaseActivity() {
    lateinit var binding: ActivityMessagesBinding
    val messageActivityViewModel: MessageActivityViewModel by inject()
    val basicFunctions: BasicFunctions by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMessagesBinding>(this, R.layout.activity_messages)
        binding.messageViewModel = messageActivityViewModel
        customToolBarWithBack(binding.toolBar, getString(R.string.messagesCaps))
        setAdapter()
        radioButtonsClick()
        initObserver()
        setData()
    }

    fun setData() {
        radioButtonImage.isChecked = true
        switchButtonNearestCheckPoint.isChecked = true
        if (AllLocalHandler.singleTripDetail.tripId.equals("") || AllLocalHandler.singleTripDetail.tripId == null) {
            linearLayoutNearestCheckPoint.visibility = View.GONE
        } else {
            linearLayoutNearestCheckPoint.visibility = View.VISIBLE
        }
    }

    fun initObserver() {
        messageActivityViewModel.feedBackMessage.observe(this, object : Observer<String> {
            override fun onChanged(message: String?) {
                basicFunctions.showFeedbackMessage(this@MessagesActivity!!, binding!!.root, message!!)
            }
        })

        messageActivityViewModel.progressBar.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    basicFunctions.showProgressBar(this@MessagesActivity!!, getString(R.string.loading))
                } else {
                    basicFunctions.hideProgressBar()
                }
            }
        })

        messageActivityViewModel.showNearestCheckPointAlert.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                if (t!!) {
                    alertDialogNearestCheckPoint()
                }
            }
        })

        messageActivityViewModel.sendIncidentClick.observe(this, object : Observer<Boolean> {
            override fun onChanged(t: Boolean?) {
                startActivity(Intent(this@MessagesActivity, IncidentsActivity::class.java))
                finish()
            }

        })


        messageActivityViewModel.onSuccessfullySendIncident.observe(this, object : Observer<SendIncidentResponse> {
            override fun onChanged(t: SendIncidentResponse?) {
                startActivity(
                    Intent(
                        this@MessagesActivity,
                        HomeActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
        })
    }

    fun setAdapter() {
        var adapter = ImageAudioFragmentAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }

    fun radioButtonsClick() {
        radioButtonImage.setOnClickListener {
            viewPager.setCurrentItem(0)
        }

        radioButtonAudio.setOnClickListener {
            viewPager.setCurrentItem(1)
        }

    }


    fun alertDialogNearestCheckPoint() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setCancelable(false)
        val layoutInflater = this!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.view_alert_dialog_nearest_check_point, null)

        var textViewAlert = view.findViewById<TextView>(R.id.textViewAlert)
        var textViewMessage = view.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.setText(getString(R.string.do_you_want_to_assign_to_nearest_check_point))
        var buttonYes = view.findViewById<Button>(R.id.buttonYes)
        var buttonNo = view.findViewById<Button>(R.id.buttonNo)

        textViewAlert.setText(
            frequentFunctions.customizeString(
                this!!,
                getString(R.string.alert),
                2,
                3
            ), TextView.BufferType.SPANNABLE
        )

        buttonYes.setOnClickListener {
            mDialog.dismiss()
            switchButtonNearestCheckPoint.isChecked = true
            messageActivityViewModel.isPopUpShowing = true
            messageActivityViewModel.sendMessageClick(rootLayout)
        }


        buttonNo.setOnClickListener {
            mDialog.dismiss()
            switchButtonNearestCheckPoint.isChecked = false
            messageActivityViewModel.isPopUpShowing = true
            messageActivityViewModel.sendMessageClick(rootLayout)

        }

        alertDialogBuilder.setView(view)
        mDialog = alertDialogBuilder.create()
        mDialog.setCancelable(false)
        mDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()
        val lp = WindowManager.LayoutParams()
        val window = mDialog.getWindow()
        window.setGravity(Gravity.CENTER)
        lp.copyFrom(window.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.setAttributes(lp)
    }
}
