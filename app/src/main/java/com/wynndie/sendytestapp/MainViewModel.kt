package com.wynndie.sendytestapp

import androidx.lifecycle.ViewModel
import com.wynndie.sendytestapp.core.presentation.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible = _isDialogVisible.asStateFlow()

    private val _notificationText = MutableStateFlow<UiText>(UiText.DynamicString(""))
    val notificationText = _notificationText.asStateFlow()

    fun toggleDialogVisibility(visible: Boolean) {
        _isDialogVisible.update { visible }
        if (!visible) {
            _notificationText.update { UiText.DynamicString("") }
        }
    }

    fun changeNotificationText(text: UiText) {
        _notificationText.update { text }
    }
}