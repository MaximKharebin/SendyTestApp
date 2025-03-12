package com.wynndie.sendytestapp

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object AuthGraph : Route

    @Serializable
    data object PhoneScreen : Route

    @Serializable
    data object TermsScreen : Route

    @Serializable
    data object TokenScreen : Route


    @Serializable
    data object FinalScreen : Route
}