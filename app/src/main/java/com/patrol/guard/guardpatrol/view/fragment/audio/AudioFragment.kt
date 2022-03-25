package com.patrol.guard.guardpatrol.view.fragment.audio

import android.Manifest
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.patrol.guard.guardpatrol.R
import android.media.MediaRecorder
import android.os.Environment.getExternalStorageDirectory
import android.R.attr.start
import android.content.Intent
import android.content.pm.PackageManager
import java.io.IOException
import android.media.MediaPlayer
import android.os.*
import kotlinx.android.synthetic.main.fragment_audio.*
import android.widget.Chronometer
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.possibility.hr.utils.FilesFunctions
import java.io.File
import java.util.*
import kotlin.concurrent.timer
import android.os.SystemClock.elapsedRealtime
import android.util.Log
import androidx.core.content.ContextCompat
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment


class AudioFragment : BaseFragment(), View.OnClickListener {

    var myAudioRecorder: MediaRecorder? = null

    var listOfFiles: MutableList<String> = ArrayList()
    lateinit var timer: CountDownTimer
    companion object{
        var mergedAudioFilePath: String = ""
    }

    var mediaPlayer: MediaPlayer? = null

    var isRecordingStarted = false
    var isRecordingPaused = false

    var timeWhenStopped = 0.toLong()
    var isViewCreated = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity!!).inflate(R.layout.fragment_audio, null)
        mergedAudioFilePath=""
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageViewRecordingCancel.setOnClickListener(this)
        imageViewRecordPause.setOnClickListener(this)
        imageViewPlay.setOnClickListener(this)
        isViewCreated = true
    }

    fun startCountDownTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start()
        chronometer.setFormat("%s");
        chronometer.setOnChronometerTickListener(object : Chronometer.OnChronometerTickListener {
            override fun onChronometerTick(chronometer: Chronometer?) {
                val elapsedMillis = SystemClock.elapsedRealtime() - chronometer!!.getBase()
                val progress = elapsedMillis / 1000
                if (circularProgress.progressMax == circularProgress.progress) {
                    chronometer.stop()
                    imageViewRecordPause.setImageResource(R.drawable.ic_recording_on_inactive)
                    imageViewRecordPause.isEnabled = false


                    imageViewPlay.setImageResource(R.drawable.ic_recording_play)
                    imageViewPlay.isEnabled = true
                } else {
                    circularProgress.progress = progress.toFloat()
                }
            }
        })
    }


    /* //<editor-fold desc="pause recording for veriosn greater than 24">
     @RequiresApi(Build.VERSION_CODES.N)
     fun pauseRecording() {
         myAudioRecorder!!.pause()
         isRecordingPaused = true

         imageViewRecordPause.setImageResource(R.drawable.ic_recording_on)
         imageViewPlay.setImageResource(R.drawable.ic_recording_play)
         imageViewPlay.isEnabled = true
     }
     //</editor-fold>

     //<editor-fold desc="start resume recording for version greater than 24">
     @RequiresApi(Build.VERSION_CODES.N)
     fun startResumeRecording() {
         if (myAudioRecorder == null) {
             myAudioRecorder = MediaRecorder()
             myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
             myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
             myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
             mergedAudioFilePath = FilesFunctions.createAudioFile().getPath()
             myAudioRecorder!!.setOutputFile(mergedAudioFilePath)

             try {
                 myAudioRecorder!!.prepare()
                 myAudioRecorder!!.start()
             } catch (ise: IllegalStateException) {
                 ise.printStackTrace()
             } catch (ioe: IOException) {
                 ioe.printStackTrace()
             }
         } else {
             myAudioRecorder!!.resume()
         }
         imageViewRecordPause.setImageResource(R.drawable.ic_recording_pause)
         imageViewPlay.setImageResource(R.drawable.ic_recording_play_inactive)
         imageViewPlay.isEnabled=false

     }
     //</editor-fold>*/


    //<editor-fold desc="recording start here for version less than 24">
    fun startRecording() {
        startCountDownTimer()
        myAudioRecorder = MediaRecorder()
        myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)


        var outputFile = FilesFunctions.createAudioFile().getPath()
        myAudioRecorder!!.setOutputFile(outputFile)

        try {
            myAudioRecorder!!.prepare()
            myAudioRecorder!!.start()
        } catch (ise: IllegalStateException) {
            ise.printStackTrace()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

        listOfFiles.add(outputFile)

        imageViewRecordPause.setImageResource(R.drawable.ic_recording_pause)
        imageViewPlay.setImageResource(R.drawable.ic_recording_play_inactive)
        imageViewPlay.isEnabled = false
        // stopPlayingRecording()
    }
    //</editor-fold>


    //<editor-fold desc="recording stop here version less than 24">
    fun stopRecording() {
        myAudioRecorder!!.stop()
        myAudioRecorder!!.release()
        myAudioRecorder = null
        mergedAudioFilePath = FilesFunctions.createAudioFile().getPath()
        FilesFunctions.mergeMediaFiles(true, listOfFiles, mergedAudioFilePath)
        listOfFiles.clear()
        listOfFiles.add(mergedAudioFilePath)
        mediaPlayer = null
    }
    //</editor-fold>


    //<editor-fold desc="play recored audio">
    fun playRecording() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer!!.setDataSource(mergedAudioFilePath)
                mediaPlayer!!.prepare()
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.pause()
                } else {
                    mediaPlayer!!.start()
                    imageViewPlay.setImageResource(R.drawable.ic_recording_pause)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                imageViewPlay.setImageResource(R.drawable.ic_recording_play)
            } else {
                mediaPlayer!!.start()
                imageViewPlay.setImageResource(R.drawable.ic_recording_pause)
            }
        }


        mediaPlayer!!.setOnCompletionListener(MediaPlayer.OnCompletionListener {
            imageViewPlay.setImageResource(R.drawable.ic_recording_play)
        })


    }
    //</editor-fold>


    fun stopPlayingRecording() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageViewRecordingCancel -> {
                FilesFunctions.deleteAudoDirectory()

                if (myAudioRecorder != null) {
                    myAudioRecorder!!.stop()
                    myAudioRecorder!!.release()
                    myAudioRecorder = null
                }

                isRecordingStarted = false

                timeWhenStopped = 0.toLong()
                chronometer.stop()
                chronometer.setBase(SystemClock.elapsedRealtime())
                circularProgress.progress = 0.toFloat()
                imageViewPlay.setImageResource(R.drawable.ic_recording_play_inactive)
                imageViewPlay.isEnabled = false


                if (mediaPlayer != null) {
                    mediaPlayer!!.stop()
                    mediaPlayer!!.release()
                    mediaPlayer == null
                }

                imageViewRecordPause.isEnabled = true
                imageViewRecordPause.setImageResource(R.drawable.ic_recording_on)

                listOfFiles.clear()
            }
            R.id.imageViewRecordPause -> {
                if (isRecordingStarted) {
                    timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                    chronometer.stop()
                    imageViewRecordPause.setImageResource(R.drawable.ic_recording_on)
                    imageViewPlay.setImageResource(R.drawable.ic_recording_play)
                    imageViewPlay.isEnabled = true
                    stopRecording()
                    isRecordingStarted = false
                } else {

                    val permission = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    )
                    if (checkPermission(permission) > 0) {
                        requestPermissions(arrayOf(permission[0], permission[1],permission[2]), Constants.REQUEST_PERMISSION)
                    }else{
                        startRecording()
                    }

                    isRecordingStarted = true
                }
/*                if (Build.VERSION.SDK_INT >= 24) {
                    if (isRecordingStarted) {
                        pauseRecording()
                        isRecordingStarted = false
                    } else {
                        startResumeRecording()
                        isRecordingStarted = true
                    }
                } else {
                    if (isRecordingStarted) {
                        stopRecording()
                        isRecordingStarted = false
                    } else {
                        startRecording()
                        isRecordingStarted = true
                    }
                }*/
            }

            R.id.imageViewPlay -> {
                playRecording()
            }
        }
    }

    /* override fun onPause() {
         super.onPause()
         imageViewRecordPause.setImageResource(R.drawable.ic_recording_on)
         imageViewPlay.setImageResource(R.drawable.ic_recording_play)
         imageViewPlay.isEnabled = true

         timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
         chronometer.stop()

         if(isRecordingStarted){
             stopRecording()
         }

         if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
             mediaPlayer!!.stop()
             mediaPlayer!!.release()
             mediaPlayer == null
         }

     }*/

    override fun onDestroy() {
        super.onDestroy()
        FilesFunctions.deleteAudoDirectory()
        try {
            stopRecording()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            stopPlayingRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (!menuVisible && isViewCreated) {
            imageViewRecordPause.setImageResource(R.drawable.ic_recording_on)
            imageViewPlay.setImageResource(R.drawable.ic_recording_play)
            imageViewPlay.isEnabled = true

            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop()

            if (isRecordingStarted) {
                stopRecording()
                isRecordingStarted = false
            }

            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer == null
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                       // dialogForCameraGallery()
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)

                            val showRationale3 =
                                shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
                            if (!showRationale) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storagePermission)
                                )
                            } else if (!showRationale2) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.storagePermission)
                                )
                            } else if (!showRationale3) {
                                alertDialogWithOKButton(
                                    getString(R.string.permission),
                                    getString(R.string.mic_phone_permission)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}