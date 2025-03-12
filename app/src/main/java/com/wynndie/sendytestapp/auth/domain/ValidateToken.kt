package com.wynndie.sendytestapp.auth.domain

import com.wynndie.sendytestapp.core.domain.Error

class ValidateToken {
    operator fun invoke(token: String): Pair<Boolean, Error?> {
        if (!token.matches(TOKEN_PATTERN.toRegex())) {
            return false to Error.INCORRECT_TOKEN
        }
        return true to null
    }

    companion object {
        private const val TOKEN_PATTERN = "^[0-9]{6}"
    }
}