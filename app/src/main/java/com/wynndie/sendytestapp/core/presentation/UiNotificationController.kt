package com.wynndie.sendytestapp.core.presentation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

object UiNotificationController {
    private val _notification = Channel<UiText>()
    val notification = _notification.receiveAsFlow()

    suspend fun notify(message: UiText) {
        _notification.send(message)
    }
}