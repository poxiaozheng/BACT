package com.example.bact.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainActivityViewModelTAG"
    }

    private val _isHasOriginImage = MutableLiveData<Boolean>()
    val isHasOriginImage: LiveData<Boolean> = _isHasOriginImage

    @Volatile
    private var _isHasProcessedImage = MutableLiveData<Boolean>()
    val isHasProcessedImage: LiveData<Boolean> = _isHasProcessedImage

    private val _scale = MutableLiveData<Int>()
    val scale: LiveData<Int> = _scale

    private val _noiseGrade = MutableLiveData<Int>()
    val noiseGrade: LiveData<Int> = _noiseGrade

    private val _processedBitmap = MutableLiveData<Bitmap>()
    val processedBitmap: LiveData<Bitmap> = _processedBitmap

    @Volatile
    private var _isClickable = MutableLiveData<Boolean>()
    val isClickable: LiveData<Boolean> = _isClickable

    private var originImageUri: Uri? = null
    private var processedImageUri: Uri? = null

    private lateinit var imageUrl: String

    private lateinit var processedImageId: String

    private lateinit var receipt: String

    init {
        _isHasOriginImage.value = false
        _isHasProcessedImage.value = false
        _scale.value = 2
        _noiseGrade.value = 0
        _isClickable.value = true
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

    fun setIsClickable(value: Boolean) {
        _isClickable.value = value
    }

    fun setIsHasOriginImage(isHasOriginImage: Boolean) {
        _isHasOriginImage.value = isHasOriginImage
    }

    fun setIsHasProcessedImage(isHasProcessedImage: Boolean) {
        _isHasProcessedImage.value = isHasProcessedImage
    }

    fun setScale(scale: Int) {
        _scale.value = scale
    }

    fun setNoiseGrade(noiseGrade: Int) {
        _noiseGrade.value = noiseGrade
    }

}