package com.patrol.guard.guardpatrol.view.fragment.image

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.example.possibility.hr.utils.FilesFunctions
import com.patrol.guard.guardpatrol.R
import com.patrol.guard.guardpatrol.model.uploadImage.UploadImageResponse
import com.patrol.guard.guardpatrol.repositry.WebServices
import com.patrol.guard.guardpatrol.utils.BasicFunctions
import com.patrol.guard.guardpatrol.utils.Constants
import com.patrol.guard.guardpatrol.utils.FrequentFunctions
import com.patrol.guard.guardpatrol.view.fragment.BaseFragment
import com.patrol.guard.guardpatrol.view.fragment.image.adapter.ImageViewPagerAdapter
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.fragment_image.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ImageFragment : BaseFragment(), View.OnClickListener, ViewPager.OnPageChangeListener,ImageViewPagerAdapter.ClickHandler {


    var currentItemPosition = 0;
    var adapter: ImageViewPagerAdapter? = null
    var listOfImages: MutableList<Bitmap?> = ArrayList()
    companion object{
        var imagesListUploadedToServer: MutableList<String?> = ArrayList()
    }

    val basicFunctions: BasicFunctions by inject()
    val webServices: WebServices by inject()
    val frequentFunctions: FrequentFunctions by inject()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_image, null);
        imagesListUploadedToServer.clear()
        return view;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageViewAdd.setOnClickListener(this)
        rightImageView.setOnClickListener(this)
        leftImageView.setOnClickListener(this)

        for (i in 0..2) {
            var bitmap: Bitmap? = null
            listOfImages.add(bitmap)


            imagesListUploadedToServer.add("")
        }

        setAdapter(listOfImages)
    }


    //<editor-fold desc="view pager adapter set here">
    fun setAdapter(listOfImages: MutableList<Bitmap?>) {
        adapter = ImageViewPagerAdapter(activity, listOfImages);
        adapter!!.itemClickListener(this)
        viewPagerImages.addOnPageChangeListener(this)
        viewPagerImages.adapter = adapter
        viewPagerImages.currentItem = currentItemPosition
    }
    //</editor-fold>


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageViewAdd -> {
                val permission = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                if (checkPermission(permission) > 0) {
                    requestPermissions(arrayOf(permission[0], permission[1],permission[2]), Constants.REQUEST_PERMISSION)
                } else {
                    currentItemPosition = viewPagerImages.currentItem
                    dialogForCameraGallery()
                }
            }

            R.id.leftImageView -> {
                viewPagerImages.setCurrentItem(viewPagerImages.currentItem - 1)
            }


            R.id.rightImageView -> {
                viewPagerImages.setCurrentItem(viewPagerImages.currentItem + 1)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.CAMERA_INTENT -> {
                    val bitmap = BitmapFactory.decodeFile(mImageFile!!.getPath())
                    var newBitMap = FilesFunctions.changeImageOrientation(mImageFile!!.path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    listOfImages.set(viewPagerImages.currentItem, newBitMap)
                    uploadIncidentImage(mImageFile!!)
                    setAdapter(listOfImages)
                }

                Constants.GALLERY_INTENT -> {
                    var path = FilesFunctions.getPathFromData(activity!!, data!!)
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.getContentResolver(), data!!.data);
                    var newBitMap = FilesFunctions.changeImageOrientation(path, bitmap)
                    mImageFile = FilesFunctions.createFileFromBitMap(newBitMap)
                    listOfImages.set(viewPagerImages.currentItem, newBitMap)
                    uploadIncidentImage(mImageFile!!)
                    setAdapter(listOfImages)
                }
            }
        }
    }


    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (position == 0) {
            leftImageView.visibility = View.INVISIBLE
        } else {
            leftImageView.visibility = View.VISIBLE
        }

        if (position == 2) {
            rightImageView.visibility = View.INVISIBLE
        } else {
            rightImageView.visibility = View.VISIBLE
        }
    }


    fun uploadIncidentImage(file: File) {
        basicFunctions.showProgressBar(activity!!, getString(R.string.loading))
        webServices.uploadIncidentImage(file).enqueue(object : Callback<UploadImageResponse> {
            override fun onResponse(call: Call<UploadImageResponse>, response: Response<UploadImageResponse>) {
                basicFunctions.hideProgressBar()
                if (response.code() == 200) {
                    imagesListUploadedToServer.set(currentItemPosition, response.body()!!.image)
                } else {
                  /*  basicFunctions.showFeedbackMessage(
                        activity!!,
                        relativeLayout,
                        frequentFunctions.errorMessage(response.errorBody()!!.string())
                    )*/
                }
            }

            override fun onFailure(call: Call<UploadImageResponse>, t: Throwable) {
                basicFunctions.hideProgressBar()
               // basicFunctions.showFeedbackMessage(activity!!, relativeLayout, t!!.message!!)
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_PERMISSION -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        dialogForCameraGallery()
                    } else {
                        if (Build.VERSION.SDK_INT >= 23) {
                            val showRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            val showRationale2 =
                                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)

                            val showRationale3 =
                                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
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
                                    getString(R.string.cameraPermission2)
                                )
                            }


                        }
                    }
                }
            }
        }
    }

    override fun crossClick(position: Int) {
        currentItemPosition=position
        var bitmap: Bitmap? = null
        listOfImages.set(position,bitmap)
        imagesListUploadedToServer.set(position,"")
        setAdapter(listOfImages)

    }


}