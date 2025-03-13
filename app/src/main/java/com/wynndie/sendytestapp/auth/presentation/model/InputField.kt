package com.wynndie.sendytestapp.auth.presentation.model

import androidx.compose.ui.text.input.TextFieldValue
import com.wynndie.sendytestapp.core.presentation.UiText

data class InputField(
    val value: TextFieldValue = TextFieldValue(),
    val supportingText: UiText = UiText.DynamicString(""),
    val isError: Boolean = false
)
