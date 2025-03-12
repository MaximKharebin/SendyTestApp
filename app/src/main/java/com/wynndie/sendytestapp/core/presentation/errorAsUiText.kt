package com.wynndie.sendytestapp.core.presentation

import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.core.domain.Error
import com.wynndie.sendytestapp.core.presentation.UiText.StringResource

fun Error.asUiText(): UiText {
    return when (this) {
        Error.INCORRECT_PHONE -> StringResource(R.string.incorrect_phone)
        Error.INCORRECT_TOKEN -> StringResource(R.string.incorrect_token)
        Error.REQUEST_WAS_NOT_SENT -> StringResource(R.string.request_wasnt_sent)
        Error.EMPTY_RESPONSE -> StringResource(R.string.server_returned_null)
        Error.NO_INTERNET -> StringResource(R.string.no_internet)
        Error.UNKNOWN -> StringResource(R.string.unknown_error)
    }
}