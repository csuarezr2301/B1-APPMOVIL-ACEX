package com.example.acexproyecto.utils

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedImages = MutableLiveData<List<Uri>>(emptyList())
    val isCameraVisible = MutableLiveData(false)

    fun showCamera() {
        isCameraVisible.value = true
    }

    fun hideCamera() {
        isCameraVisible.value = false
    }
}