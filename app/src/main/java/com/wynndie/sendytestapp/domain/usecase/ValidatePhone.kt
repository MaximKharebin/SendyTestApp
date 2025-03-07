package com.wynndie.sendytestapp.domain.usecase

import com.wynndie.sendytestapp.domain.util.Error

private const val PHONE_PATTERN =
    "(^8|7|\\+7)((\\d{10})|(\\s\\(\\d{3}\\)\\s\\d{3}\\s\\d{2}\\s\\d{2}))"

class ValidatePhone {
    operator fun invoke(phone: String): Pair<Boolean, Error?> {
        if (!phone.matches(PHONE_PATTERN.toRegex())) {
            return false to Error.INCORRECT_PHONE
        }
        return true to null
    }
}