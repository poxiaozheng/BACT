package com.example.bact.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val _isHasOriginImage = MutableLiveData<Boolean>()
    val isHasOriginImage: LiveData<Boolean> = _isHasOriginImage

    private val _isHasProcessedImage = MutableLiveData<Boolean>()
    val isHasProcessedImage: LiveData<Boolean> = _isHasProcessedImage

    private val _scale = MutableLiveData<Int>()
    val scale: LiveData<Int> = _scale

    private val _noiseGrade = MutableLiveData<Int>()
    val noiseGrade: LiveData<Int> = _noiseGrade

    @Volatile
    private var _isProcessedFinish = MutableLiveData<Boolean>()
    val isProcessedFinish: LiveData<Boolean> = _isProcessedFinish

    @Volatile
    private var _isClickable = MutableLiveData<Boolean>()
    val isClickable: LiveData<Boolean> = _isClickable

    init {
        _isHasOriginImage.value = false
        _isHasProcessedImage.value = false
        _scale.value = 2
        _noiseGrade.value = 0
        _isProcessedFinish.value = false
        _isClickable.value = true
    }

    fun setIsClickable(value: Boolean) {
        _isClickable.value = value
    }

    fun setIsProcessedFinish(value: Boolean) {
        _isProcessedFinish.value = value
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