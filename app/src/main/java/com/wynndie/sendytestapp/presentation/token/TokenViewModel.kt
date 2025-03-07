package com.wynndie.sendytestapp.presentation.token

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.domain.usecase.MakeTokenApiCall
import com.wynndie.sendytestapp.domain.usecase.ValidateToken
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.util.UiText
import com.wynndie.sendytestapp.presentation.util.asUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import land.sendy.pfe_sdk.api.API

private const val TOKEN_LENGTH = 6
private const val TOKEN_TYPE = "sms"

class TokenViewModel(
    private val validateToken: ValidateToken,
    private val makeTokenApiCall: MakeTokenApiCall
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _tokenInputField = MutableStateFlow(InputField())
    val tokenInputField = _tokenInputField.asStateFlow()

    val canVerifyToken = combine(
        isLoading,
        tokenInputField,
    ) { isLoading, phoneInput ->
        phoneInput.value.length == TOKEN_LENGTH && !isLoading
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )


    fun onTokenValueChange(value: String) {
        var newValue = value

        newValue = newValue.filter { it.isDigit() }
        if (newValue.length > TOKEN_LENGTH) {
            newValue = newValue.dropLast(newValue.length - TOKEN_LENGTH)
        }

        _tokenInputField.update { it.copy(value = newValue) }
    }

    fun verifyToken(context: Context, api: API) {
        if (isLoading.value) {
            return
        }
        _isLoading.update { true }

        val token = tokenInputField.value.value

        val (result, error) = validateToken(token)
        if (!result) {
            _isLoading.update { false }
            _tokenInputField.update {
                it.copy(
                    supportingText = error!!.asUiText(),
                    isError = true
                )
            }
            return
        }

        makeTokenApiCall(
            apiCall = { callback ->
                api.activateWllet(context, token, TOKEN_TYPE, callback)
            },
            onCallbackError = { error ->
                _isLoading.update { false }
                _tokenInputField.update {
                    it.copy(
                        supportingText = error.asUiText(),
                        isError = true
                    )
                }
            },
            onCallbackSuccess = {
                _isLoading.update { false }
                _tokenInputField.update {
                    it.copy(
                        supportingText = UiText.StringResource(R.string.auth_succeed),
                        isError = false
                    )
                }
            }
        )
    }
}