package com.wynndie.sendytestapp.domain.util

enum class Error {
    INCORRECT_PHONE,
    NO_INTERNET,
    INCORRECT_TOKEN,
    REQUEST_WAS_NOT_SENT,
    UNKNOWN,
    EMPTY_RESPONSE;

    companion object {
        fun of(number: Int): Error {
            return when (number) {
                -40 -> NO_INTERNET
                806 -> INCORRECT_TOKEN
                else -> UNKNOWN
            }
        }
    }
}