package com.wynndie.sendytestapp.auth.presentation.model

import com.wynndie.sendytestapp.core.presentation.UiText

data class InputField(
    val value: String = "",
    val supportingText: UiText = UiText.DynamicString(""),
    val isError: Boolean = false
)
