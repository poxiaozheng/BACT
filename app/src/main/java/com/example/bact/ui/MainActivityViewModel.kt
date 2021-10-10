package com.example.bact.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val _isHasOriginImage = MutableLiveData<Boolean>()
    val isHasOriginImage: LiveData<Boolean> = _isHasOriginImage

    private val _isHasProcessedImage = MutableLiveData<Boolean>()
    val isHasProcessedImage: LiveData<Boolean> = _isHasProcessedImage

    private val _isProcessFinish = MutableLiveData<Boolean>()
    val isProcessFinish: LiveData<Boolean> = _isProcessFinish

    private val _multiple = MutableLiveData<Int>()
    val multiple: LiveData<Int> = _multiple

    private val _noiseGrade = MutableLiveData<Int>()
    val noiseGrade: LiveData<Int> = _noiseGrade

    init {
        _isHasOriginImage.value= false
        _isHasProcessedImage.value = false
        _isProcessFinish.value = false
        _multiple.value = 2
        _noiseGrade.value = 0
    }

    fun setIsHasOriginImage(isHasOriginImage : Boolean){
        _isHasOriginImage.value = isHasOriginImage
    }

    fun setIsHasProcessedImage(isHasProcessedImage:Boolean){
        _isHasProcessedImage.value = isHasProcessedImage
    }

    fun setIsProcessFinish(isProcessFinish:Boolean){
        _isProcessFinish.value = isProcessFinish
    }

    fun setMultiple(multiple:Int){
        _multiple.value = multiple
    }

    fun setNoiseGrade(noiseGrade:Int){
        _noiseGrade.value = noiseGrade
    }

}