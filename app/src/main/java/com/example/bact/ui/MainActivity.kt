package com.example.bact.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bact.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val originImage = binding.originImage
        originImage.setImageURI(getShareImageUri())
    }

    private fun getShareImageUri(): Uri? {
        val intent = intent
        val extras = intent.extras
        val action = intent.action
        if (Intent.ACTION_SEND == action) {
            extras?.let {
                if (it.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        return it.getParcelable(Intent.EXTRA_STREAM) as Uri?
                        //val path = getRealPathFromURI(this, uri)
                    } catch (e: Exception) {
                        Log.d(TAG, e.toString())
                    }
                }
            }
        }
        return null
    }

    private fun getRealPathFromURI(activity: Activity, uri: Uri?): String? {
        val arr = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.managedQuery(uri, arr, null, null, null)
            ?: return uri?.path
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

}