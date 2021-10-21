package com.example.bact.ui.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragmentViewModel : ViewModel() {

    private val _scale = MutableLiveData<Int>()
    val scale: LiveData<Int> = _scale

    private val _noiseGrade = MutableLiveData<Int>()
    val noiseGrade: LiveData<Int> = _noiseGrade

    private val _processedBitmap = MutableLiveData<Bitmap>()
    val processedBitmap: LiveData<Bitmap> = _processedBitmap

    private var isHasOriginImage = false

    private var isHasProcessedImage = false

    private var isClickable = true

    private var originImageUri: Uri? = null

    private var processedImageUri: Uri? = null

    private lateinit var imageUrl: String

    private lateinit var processedImageId: String

    private lateinit var receipt: String

    init {
        _scale.value = 2
        _noiseGrade.value = 0
    }

    fun getReceipt() = receipt

    fun setReceipt(receipt: String) {
        this.receipt = receipt
    }

    fun getProcessedImageId() = processedImageId

    fun setProcessedImageId(processedImageId: String) {
        this.processedImageId = processedImageId
    }

    fun setProcessedBitmap(processedBitmap: Bitmap) {
        _processedBitmap.value = processedBitmap
    }

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getOriginImageUri() = originImageUri

    fun getProcessedImageUri() = processedImageUri

    fun setOriginImageUri(value: Uri?) {
        originImageUri = value
    }

    fun setProcessedImageUri(value: Uri?) {
        processedImageUri = value
    }

    fun getIsClickable() = isClickable

    fun setIsClickable(value: Boolean) {
        isClickable = value
    }

    fun getIsHasOriginImage() = isHasOriginImage

    fun setIsHasOriginImage(value: Boolean) {
        isHasOriginImage = value
    }

    fun getIsHasProcessedImage() = isHasProcessedImage

    fun setIsHasProcessedImage(value: Boolean) {
        isHasProcessedImage = value
    }

    fun setScale(scale: Int) {
        _scale.value = scale
    }

    fun setNoiseGrade(noiseGrade: Int) {
        _noiseGrade.value = noiseGrade
    }

}