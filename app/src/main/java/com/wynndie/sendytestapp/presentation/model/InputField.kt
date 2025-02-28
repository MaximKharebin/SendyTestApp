package com.wynndie.sendytestapp.presentation.model

import com.wynndie.sendytestapp.presentation.util.UiText

data class InputField(
    val value: String = "",
    val supportingText: UiText? = null,
    val isError: Boolean = false,
)
