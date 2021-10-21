package com.example.bact.ui.history

import androidx.lifecycle.*
import com.example.bact.model.database.ImageInfo
import com.example.bact.model.database.ImageInfoDao
import kotlinx.coroutines.launch

class HistoryFragmentViewModel(private val imageInfoDao: ImageInfoDao) : ViewModel() {

    val allItems: LiveData<List<ImageInfo>> = imageInfoDao.getItems().asLiveData()

    fun insertItem(imageInfo: ImageInfo) {
        viewModelScope.launch {
            imageInfoDao.insert(imageInfo)
        }
    }

    fun deleteItem(imageInfo: ImageInfo) {
        viewModelScope.launch {
            imageInfoDao.delete(imageInfo)
        }
    }

    fun newItem(
        imageByteArray: ByteArray,
        scale: Int,
        noiseGrade: Int,
        date: String,
        imageName: String
    ): ImageInfo {
        return ImageInfo(
            imageByteArray = imageByteArray,
            scale = scale,
            noiseGrade = noiseGrade,
            date = date,
            imageName = imageName
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class HistoryFragmentViewModelFactory(private val itemDao: ImageInfoDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryFragmentViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}