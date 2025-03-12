package com.wynndie.sendytestapp.auth.domain

import com.wynndie.sendytestapp.core.domain.Constants

class FormatPhone {
    operator fun invoke(value: String): String {
        var newValue = value
            .dropWhile { it != Constants.MOBILE_CODE }
            .filter { it.isDigit() }

        if (newValue.length > Constants.PHONE_LENGTH) {
            newValue = newValue.dropLast(newValue.length - Constants.PHONE_LENGTH)
        }

        return newValue
    }
}