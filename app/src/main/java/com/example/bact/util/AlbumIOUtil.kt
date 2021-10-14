package com.example.bact.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.*
import java.net.URL

object AlbumIOUtil {

    private const val TAG = "AlbumUtil"

     fun getRealPathFromURI(activity: Activity, uri: Uri?): String? {
        val arr = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.managedQuery(uri, arr, null, null, null)
            ?: return uri?.path
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

    fun getShareImageUri(activity: Activity): Uri? {
        val intent = activity.intent
        val extras = intent.extras
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            extras?.let {
                if (it.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        val uri = it.getParcelable(Intent.EXTRA_STREAM) as Uri?
                        uri?.path?.let { it1 -> Log.d(TAG, "getPath():$it1") }
                        val path = getRealPathFromURI(activity, uri)
                        Log.d(TAG, "path:$path")
                        return uri
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                    }
                }
            }
        }
        return null
    }

    private fun getContentValues(mimeType: String): ContentValues {
        val values = ContentValues()
        val displayName = System.currentTimeMillis().toString()
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
        bitmap: Bitmap,
        mimeType: String = "image/jpeg",
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ) :Uri ?{
        val values = getContentValues(mimeType)
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
        inputStream: InputStream,
        mimeType: String = "image/jpeg"
    ) {
        val values = getContentValues(mimeType)
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

    fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
        val buffer = ByteArray(1024)
        var len = -1
        val byteArrayOutputStream = ByteArrayOutputStream()
        len = inputStream.read(buffer)
        ExceptionUtil.wrapException {
            while (len != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
                len = inputStream.read(buffer)
            }
        }
        val data = byteArrayOutputStream.toByteArray()
        byteArrayOutputStream.close()
        inputStream.close()
        return data
    }

    fun byteArrayToBitmap(byteArray: ByteArray, opts: BitmapFactory.Options?): Bitmap? {
        return if (opts != null) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, opts)
        } else {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        val fd = context.contentResolver.openFileDescriptor(uri, "r")
        val bitmap = BitmapFactory.decodeFileDescriptor(fd?.fileDescriptor)
        fd?.close()
        return bitmap
    }

    fun getURLImage(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        ExceptionUtil.wrapException {
            val imageUrl = URL(url)
            val httpURLConnection = imageUrl.openConnection()
            httpURLConnection.apply {
                connectTimeout = 5000
                doInput = false
                useCaches = false
                connect()
                val inputStream = httpURLConnection.getInputStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
        }
        return bitmap
    }


}