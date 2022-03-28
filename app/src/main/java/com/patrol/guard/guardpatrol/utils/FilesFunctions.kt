package com.example.possibility.hr.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.patrol.guard.guardpatrol.utils.Constants
import java.io.*
import java.util.*


object FilesFunctions {
    lateinit var file: File

    var storageDir:File?=null
    fun createImageFile(): File {
        val imageFileName = Constants.APP_NAME + "_" + System.currentTimeMillis()
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        var image: File
        image = File.createTempFile(imageFileName, ".jpg", storageDir)
        return image
    }


    fun getResizedBitmap(image: Bitmap, bitmapWidth: Int, bitmapHeight: Int): Bitmap {
        var resizedBitMap = Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true)
        return resizedBitMap;
    }
    fun createFileFromBitMap(bitmap: Bitmap):File {
        val videoFileName = Constants.APP_NAME+"-"+ System.currentTimeMillis() + ".jpg"
       // val myDirectory = File(Environment.getExternalStorageDirectory(), "GuardPatrol")

        val myDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/GuardPatrol/"
        )

        if (!myDirectory.exists()) {
            myDirectory.mkdir()
        }
        file = File(myDirectory, videoFileName)
        file.createNewFile()

        //Convert bitmap to byte array
        var bos = ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        var bitmapData = bos.toByteArray()

        //write the bytes in file
        var fos = FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close()
        return file;
    }


    fun getPathFromData(context: Context, data: Intent): String {
        val selectedImageUri = data.data
        val filePath = arrayOf(MediaStore.Images.Media.DATA)
        val c = context.contentResolver.query(selectedImageUri!!, filePath, null, null, null)
        c!!.moveToFirst()
        val columnIndex = c.getColumnIndex(filePath[0])
        return c.getString(columnIndex)
    }




    fun changeImageOrientation(photoPath: String, bitmap: Bitmap): Bitmap {
        var ei = ExifInterface(photoPath);
        var orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        var rotatedBitmap: Bitmap? = null;
        when (orientation) {

            ExifInterface.ORIENTATION_ROTATE_90 ->
                rotatedBitmap = rotateImage(bitmap, 90.toFloat());


            ExifInterface.ORIENTATION_ROTATE_180 ->
                rotatedBitmap = rotateImage(bitmap, 180.toFloat());


            ExifInterface.ORIENTATION_ROTATE_270 ->
                rotatedBitmap = rotateImage(bitmap, 270.toFloat());


            ExifInterface.ORIENTATION_NORMAL ->
                rotatedBitmap = bitmap;
            else ->
                rotatedBitmap = bitmap
        }
        return rotatedBitmap!!;
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }


    fun createAudioFile(): File {
        var imageFileName = Constants.APP_NAME + "_" + System.currentTimeMillis()+".3gp"
       // storageDir = File(Environment.getExternalStorageDirectory(), "GuardPatrolAudio")

        storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/GuardPatrolAudio/"
        )
        if (!storageDir!!.exists()) {
            storageDir!!.mkdir()
        }
        val file = File(storageDir, imageFileName)
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /*File image = null;
        try {
            image = File.createTempFile(imageFileName, ".3gp", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return file
    }

    fun deleteAudoDirectory(){
        if(storageDir!=null){
            storageDir!!.delete()
        }
    }


    fun mergeMediaFiles(isAudio: Boolean, sourceFiles: MutableList<String>, targetFile: String): Boolean {
        try {
            val mediaKey = if (isAudio) "soun" else "vide"
            val listMovies = ArrayList<Movie>()
            for (filename in sourceFiles) {
                listMovies.add(MovieCreator.build(filename))
            }
            val listTracks = LinkedList<Track>()
            for (movie in listMovies) {
                for (track in movie.getTracks()) {
                    if (track.getHandler().equals(mediaKey)) {
                        listTracks.add(track)
                    }
                }
            }
            val outputMovie = Movie()
            if (!listTracks.isEmpty()) {
                outputMovie.addTrack(AppendTrack(*listTracks.toTypedArray<Track>()))
            }
            val container = DefaultMp4Builder().build(outputMovie)
            val fileChannel = RandomAccessFile(String.format(targetFile), "rw").channel
            container.writeContainer(fileChannel)
            fileChannel.close()
            return true
        } catch (e: IOException) {
            Log.e("Testing Purpose", "Error merging media files. exception: " + e.message)
            return false
        }

    }


}