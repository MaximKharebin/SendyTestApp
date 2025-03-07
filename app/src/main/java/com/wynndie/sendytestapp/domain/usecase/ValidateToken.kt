package com.wynndie.sendytestapp.domain.usecase

import com.wynndie.sendytestapp.domain.util.Error

private const val TOKEN_PATTERN = "^[0-9]{6}"

class ValidateToken {
    operator fun invoke(token: String): Pair<Boolean, Error?> {
        if (!token.matches(TOKEN_PATTERN.toRegex())) {
            return false to Error.INCORRECT_TOKEN
        }
        return true to null
    }
}