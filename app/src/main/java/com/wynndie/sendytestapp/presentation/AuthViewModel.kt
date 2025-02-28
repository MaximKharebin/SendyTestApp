package com.wynndie.sendytestapp.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.util.TokenType
import com.wynndie.sendytestapp.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.pfe.response.AuthActivateRs
import land.sendy.pfe_sdk.model.pfe.response.BResponse
import land.sendy.pfe_sdk.model.types.ApiCallback
import land.sendy.pfe_sdk.model.types.LoaderError

private const val TOKEN_LENGTH = 6
private const val TOKEN_PATTERN = "^[0-9]{6}"
private const val PHONE_LENGTH = 10
private const val PHONE_PATTERN = "(^8|7|\\+7)((\\d{10})|(\\s\\(\\d{3}\\)\\s\\d{3}\\s\\d{2}\\s\\d{2}))"

class AuthViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    private val _phoneInputField = MutableStateFlow(InputField())
    val phoneInputField = _phoneInputField.asStateFlow()

    private val _areTermsAccepted = MutableStateFlow(false)
    val areTermsAccepted = _areTermsAccepted.asStateFlow()

    val canVerifyPhone = phoneInputField.combine(areTermsAccepted) { phoneValue, areTermsAccepted ->
        phoneValue.value.length == PHONE_LENGTH && areTermsAccepted == true
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )


    private val _tokenInputField = MutableStateFlow(InputField())
    val tokenInputField = _tokenInputField.asStateFlow()

    private val _tokenType = MutableStateFlow<TokenType>(TokenType.SMS)
    val tokenType = _tokenType.asStateFlow()


    fun onPhoneValueChange(value: String) {
        if (value.length > PHONE_LENGTH) return
        _phoneInputField.update { it.copy(value = value) }
    }

    fun onTokenValueChange(value: String, context: Context, api: API) {
        if (value.length > TOKEN_LENGTH) return
        _tokenInputField.update { it.copy(value = value) }
        if (value.length == TOKEN_LENGTH) {
            verifyToken(context = context, api = api)
        }
    }

    fun onTermsAcceptanceChange(value: Boolean) {
        _areTermsAccepted.update { value }
    }

    fun verifyPhone(context: Context, api: API, navigateToTokenScreen: () -> Unit) {
        _isLoading.update { true }

        val phoneCountryCode = context.getString(R.string.phone_country_code)
        val phoneNumber = phoneCountryCode + phoneInputField.value.value
        if (!phoneNumber.matches(PHONE_PATTERN.toRegex())) {
            updateSupportingText(
                flow = _phoneInputField,
                text = UiText.StringResource(R.string.incorrect_phone_pattern),
                isError = true
            )
            _isLoading.update { false }
            return
        }

        val callback = object : ApiCallback() {
            override fun onCompleted(res: Boolean) {
                if (!res || errNo != 0) {
                    updateSupportingText(
                        flow = _phoneInputField,
                        text = UiText.DynamicString(this.toString()),
                        isError = true
                    )
                } else {
                    updateSupportingText(
                        flow = _phoneInputField,
                        text = null,
                        isError = false
                    )
                    _tokenType.update { TokenType.SMS }
                    navigateToTokenScreen()
                }
                _isLoading.update { false }
            }
        }

        runRequest(
            request = { api.loginAtAuth(context, phoneNumber, callback) },
            onError = { result ->
                updateSupportingText(
                    flow = _phoneInputField,
                    text = UiText.StringResource(R.string.request_wasnt_sent, result.toString()),
                    isError = true
                )
                _isLoading.update { false }
            }
        )
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

        runRequest(
            request = { api.activateWllet(context, token, tokenType, callback) },
            onError = { result ->
                updateSupportingText(
                    flow = _tokenInputField,
                    text = UiText.StringResource(R.string.request_wasnt_sent, result.toString()),
                    isError = true
                )
                _isLoading.update { false }
            }
        )
    }

    private fun updateSupportingText(
        flow: MutableStateFlow<InputField>,
        text: UiText?,
        isError: Boolean
    ) {
        flow.update { it.copy(supportingText = text, isError = isError) }
    }

    private fun runRequest(request: () -> LoaderError?, onError: (LoaderError) -> Unit) {
        val runResult = request()
        if (runResult != null && runResult.hasError()) { onError(runResult) }
    }
}