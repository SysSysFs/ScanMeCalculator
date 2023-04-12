package com.example.scanmecalculator.presentation.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.scanmecalculator.presentation.camera.CameraViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _visiblePermissionDialogQueue = mutableStateListOf<String>()
    val visiblePermissionDialogQueue = MutableStateFlow(_visiblePermissionDialogQueue)

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    fun dismissDialog() {
        _visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !_visiblePermissionDialogQueue.contains(permission)) {
            _visiblePermissionDialogQueue.add(permission)
        }
    }

    fun onImageSelected(uri:Uri?)
    {

    }
}