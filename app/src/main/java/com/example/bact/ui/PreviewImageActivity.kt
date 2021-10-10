package com.example.bact.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.example.bact.databinding.ActivityPreviewSelectedImageBinding
import com.example.bact.view.PinchImageView

class PreviewImageActivity : BaseActivity() {

    companion object {
        private const val TAG = "PreviewImageActivity"
    }

    private lateinit var binding: ActivityPreviewSelectedImageBinding
    private lateinit var pinchImageView: PinchImageView
    private lateinit var imageButton: ImageButton

    private var preViewImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewSelectedImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        pinchImageView = binding.previewImageView
        imageButton = binding.homeArrowBtn
        preViewImageUri = intent.getParcelableExtra("key")
        Log.d(TAG,"preViewImageUri:$preViewImageUri")
        pinchImageView.setImageURI(preViewImageUri)
        imageButton.setOnClickListener {
            finish()
        }
    }
}