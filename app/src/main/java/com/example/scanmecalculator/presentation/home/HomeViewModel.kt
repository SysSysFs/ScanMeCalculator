package com.example.scanmecalculator.presentation.home

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanmecalculator.domain.model.TextParserInfo
import com.example.scanmecalculator.domain.repository.StorageType
import com.example.scanmecalculator.domain.use_case.GetDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase
) : ViewModel() {
    private val _visiblePermissionDialogQueue = mutableStateListOf<String>()
    val visiblePermissionDialogQueue = MutableStateFlow(_visiblePermissionDialogQueue)

    private var getTextParserInfoListJob: Job? = null

    private val _storageType: MutableStateFlow<StorageType> = MutableStateFlow(StorageType.FILE)
    val storageType = _storageType.asStateFlow()

    private val _textParserInfoList: SnapshotStateList<TextParserInfo> =
        mutableStateListOf<TextParserInfo>()
    val textParserInfoList = MutableStateFlow(_textParserInfoList)

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        refreshTextParserInfoList()
    }

    fun refreshTextParserInfoList() {
        _isRefreshing.value = true
        getTextParserInfoListJob?.cancel()
        getTextParserInfoListJob = getDataUseCase
            .invoke(
                type = _storageType.value
            )
            .onEach { result ->
                _textParserInfoList.clear()
                _textParserInfoList.addAll(result)
                _isRefreshing.value = false
            }
            .launchIn(viewModelScope)

    }

    fun dismissDialog() {
        _visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String, isGranted: Boolean
    ) {
        if (!isGranted && !_visiblePermissionDialogQueue.contains(permission)) {
            _visiblePermissionDialogQueue.add(permission)
        }
    }

    fun onStorageTypeSelected(label: String) {
        _storageType.value = StorageType.values().firstOrNull { it.label == label } ?: StorageType.FILE
        refreshTextParserInfoList()
    }
}