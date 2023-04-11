package com.example.scanmecalculator.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _visiblePermissionDialogQueue = mutableStateListOf<String>()
    val visiblePermissionDialogQueue = MutableStateFlow(_visiblePermissionDialogQueue)

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
}