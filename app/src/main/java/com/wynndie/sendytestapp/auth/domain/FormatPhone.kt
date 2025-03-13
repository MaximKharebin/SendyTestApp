package com.wynndie.sendytestapp.auth.domain

import com.wynndie.sendytestapp.core.domain.Constants

class FormatPhone {
    operator fun invoke(value: String): String {
        var newValue = value

        if (newValue.length > Constants.PHONE_LENGTH) {
            if (newValue.startsWith(Constants.COUNTRY_CODE)) {
                newValue = newValue.drop(Constants.COUNTRY_CODE.length)
            } else if (newValue.startsWith(Constants.REGIONAL_CODE)) {
                newValue = newValue.drop(Constants.REGIONAL_CODE.length)
            }
            newValue = newValue.dropLast(newValue.length - Constants.PHONE_LENGTH)
        }

        newValue = newValue.filter { it.isDigit() }

        return newValue.filter { it.isDigit() }
    }
}