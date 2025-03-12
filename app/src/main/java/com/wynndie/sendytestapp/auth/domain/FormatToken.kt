package com.wynndie.sendytestapp.auth.domain

import com.wynndie.sendytestapp.core.domain.Constants

class FormatToken {
    operator fun invoke(value: String): String {
        var newValue = value.filter { it.isDigit() }

        if (newValue.length > Constants.TOKEN_LENGTH) {
            newValue = newValue.dropLast(newValue.length - Constants.TOKEN_LENGTH)
        }

        return newValue
    }
}