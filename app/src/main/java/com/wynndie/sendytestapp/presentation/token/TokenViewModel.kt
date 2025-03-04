package com.wynndie.sendytestapp.presentation.token

import android.content.Context
import androidx.lifecycle.ViewModel
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.model.TokenType
import com.wynndie.sendytestapp.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.pfe.response.AuthActivateRs
import land.sendy.pfe_sdk.model.pfe.response.BResponse
import land.sendy.pfe_sdk.model.types.ApiCallback
import land.sendy.pfe_sdk.model.types.LoaderError

private const val TOKEN_LENGTH = 6
private const val TOKEN_PATTERN = "^[0-9]{6}"

class TokenViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _tokenInputField = MutableStateFlow(InputField())
    val tokenInputField = _tokenInputField.asStateFlow()

    private val _tokenType = MutableStateFlow<TokenType>(TokenType.SMS)
    val tokenType = _tokenType.asStateFlow()

    private val _canVerifyToken = MutableStateFlow(false)
    val canVerifyToken = _canVerifyToken.asStateFlow()


    fun onTokenValueChange(value: String) {
        var newValue = value

        if (newValue.length > TOKEN_LENGTH) {
            newValue = newValue.dropLast(newValue.length - TOKEN_LENGTH)
        }

        _tokenInputField.update { it.copy(value = newValue) }
        _canVerifyToken.update { newValue.length == TOKEN_LENGTH }
    }

    fun verifyToken(context: Context, api: API) {
        _isLoading.update { true }

        val tokenType = tokenType.value.name.lowercase()
        val token = tokenInputField.value.value
        if (!token.matches(TOKEN_PATTERN.toRegex())) {
            updateSupportingText(
                flow = _tokenInputField,
                text = UiText.StringResource(R.string.incorrect_token_pattern),
                isError = true
            )
            _isLoading.update { false }
            return
        }

        val callback = object : ApiCallback() {
            override fun <T : BResponse?> onSuccess(data: T?) {
                if (data == null) {
                    updateSupportingText(
                        flow = _tokenInputField,
                        text = UiText.StringResource(R.string.server_returned_null),
                        isError = true
                    )
                    _isLoading.update { false }
                    return
                }

                if (this.errNo != 0) {
                    updateSupportingText(
                        flow = _tokenInputField,
                        text = UiText.DynamicString(this.toString()),
                        isError = true
                    )
                    _isLoading.update { false }
                    return
                }

                val response = oResponse as AuthActivateRs
                val isTwoFactorRequired = response.TwoFactor != null && response.TwoFactor
                if (isTwoFactorRequired && API.checkString(response.Email)) {
                    _tokenType.update { TokenType.EMAIL }
                    _tokenInputField.update { it.copy(value = "") }
                } else if (response.Active != null && response.Active) {
                    api.acivateDevice(context)
                }
                updateSupportingText(
                    flow = _tokenInputField,
                    text = UiText.StringResource(R.string.auth_succed),
                    isError = false
                )
                _isLoading.update { false }
            }

            override fun onFail(error: LoaderError?) {
                updateSupportingText(
                    flow = _tokenInputField,
                    text = UiText.StringResource(R.string.fatal_error),
                    isError = true
                )
                _isLoading.update { false }
            }
        }

        val runResult = api.activateWllet(context, token, tokenType, callback)
        if (runResult != null && runResult.hasError()) {
            updateSupportingText(
                flow = _tokenInputField,
                text = UiText.StringResource(R.string.request_wasnt_sent, runResult.toString()),
                isError = true
            )
            _isLoading.update { false }
        }
    }

    private fun updateSupportingText(
        flow: MutableStateFlow<InputField>,
        text: UiText?,
        isError: Boolean
    ) {
        flow.update { it.copy(supportingText = text, isError = isError) }
    }
}