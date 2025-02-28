package com.wynndie.sendytestapp.presentation.util

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object AuthGraph : Route

    @Serializable
    data object PhoneScreen : Route

    @Serializable
    data object TokenScreen : Route
}