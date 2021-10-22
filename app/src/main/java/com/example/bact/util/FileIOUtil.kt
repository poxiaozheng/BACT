package com.example.bact.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.bact.BACTApplication
import com.example.bact.model.entity.BitmapWithMime
import com.example.bact.service.network.BACTNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object FileIOUtil {

    private const val TAG = "AlbumUtil"

    fun getShareImageUri(activity: Activity): Uri? {
        val intent = activity.intent
        val extras = intent.extras
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            extras?.let {
                if (it.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        val uri = it.getParcelable(Intent.EXTRA_STREAM) as Uri?
                        Log.d(TAG, "uri:$uri")
                        return uri
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                    }
                }
            }
        }
        return null
    }

    private fun getContentValues(displayName: String, mimeType: String): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            values.put(MediaStore.MediaColumns.IS_PENDING, false)
        } else {
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
            values.put(MediaStore.MediaColumns.IS_PENDING, true)
        }
        return values
    }

    fun addBitmapToAlbum(
        context: Context,
        displayName: String,
        bitmap: Bitmap,
        mimeType: String = "image/png",
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): Uri? {
        val values = getContentValues(displayName, mimeType)
        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
                outputStream.close()
            }
        }
        return uri
    }

    fun bitmapToByteArray(
        bitmap: Bitmap,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(compressFormat, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun writeInputStreamToAlbum(
        context: Context,
        displayName: String,
        inputStream: InputStream,
        mimeType: String = "image/png"
    ) {
        val values = getContentValues(displayName, mimeType)
        val bis = BufferedInputStream(inputStream)
        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val bos = BufferedOutputStream(outputStream)
                val buffer = ByteArray(1024)
                var bytes = bis.read(buffer)
                while (bytes >= 0) {
                    bos.write(buffer, 0, bytes)
                    bos.flush()
                    bytes = bis.read(buffer)
                }
                bos.close()
            }
        }
        bis.close()
    }

    fun byteArrayToBitmap(byteArray: ByteArray, opts: BitmapFactory.Options?): Bitmap {
        return if (opts != null) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, opts)
        } else {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }

//    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
//        val fd = context.contentResolver.openFileDescriptor(uri, "r")
//        val bitmap = BitmapFactory.decodeFileDescriptor(fd?.fileDescriptor)
//        fd?.close()
//        return bitmap
//    }

    fun uriToBitmapWithMime(context: Context, uri: Uri): BitmapWithMime {
        val fd = context.contentResolver.openFileDescriptor(uri, "r")
        val opts = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFileDescriptor(fd?.fileDescriptor, Rect(), opts)
        fd?.close()
        val mimeType = opts.outMimeType
        return BitmapWithMime(bitmap, mimeType)
    }

    fun getUrlImageByteArray(imageUrl: String): ByteArray? {
        val imageRequest = Request.Builder().url(imageUrl).get().build()
        val response = BACTNetwork.okHttpClient?.newCall(imageRequest)?.execute()
        return response?.body()?.bytes()
    }

}