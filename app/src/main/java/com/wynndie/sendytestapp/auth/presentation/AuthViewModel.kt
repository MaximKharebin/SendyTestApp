package com.wynndie.sendytestapp.auth.presentation

import android.content.Context
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wynndie.sendytestapp.auth.domain.FormatPhone
import com.wynndie.sendytestapp.auth.domain.FormatToken
import com.wynndie.sendytestapp.auth.domain.MakePhoneApiCall
import com.wynndie.sendytestapp.auth.domain.MakeTermsApiCall
import com.wynndie.sendytestapp.auth.domain.MakeTokenApiCall
import com.wynndie.sendytestapp.auth.domain.ValidatePhone
import com.wynndie.sendytestapp.auth.domain.ValidateToken
import com.wynndie.sendytestapp.auth.presentation.model.InputField
import com.wynndie.sendytestapp.core.domain.Constants
import com.wynndie.sendytestapp.core.presentation.UiNotificationController
import com.wynndie.sendytestapp.core.presentation.UiText
import com.wynndie.sendytestapp.core.presentation.asUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import land.sendy.pfe_sdk.api.API

class AuthViewModel(
    private val makeTermsApiCall: MakeTermsApiCall,
    private val makePhoneApiCall: MakePhoneApiCall,
    private val makeTokenApiCall: MakeTokenApiCall,
    private val validatePhone: ValidatePhone,
    private val validateToken: ValidateToken,
    private val formatPhone: FormatPhone,
    private val formatToken: FormatToken
) : ViewModel() {

    private val _isTermsLoading = MutableStateFlow(false)
    val isTermsLoading = _isTermsLoading.asStateFlow()

    private val _terms = MutableStateFlow("")
    val terms = _terms.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _phoneInputField = MutableStateFlow(InputField())
    val phoneInputField = _phoneInputField.asStateFlow()

    private val _areTermsAccepted = MutableStateFlow(false)
    val areTermsAccepted = _areTermsAccepted.asStateFlow()

    val canVerifyPhone = combine(
        isLoading,
        phoneInputField,
        areTermsAccepted
    ) { isLoading, phoneInput, areTermsAccepted ->
        phoneInput.value.text.length == Constants.PHONE_LENGTH && areTermsAccepted && !isLoading
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )


    private val _isTokenLoading = MutableStateFlow(false)
    val isTokenLoading = _isTokenLoading.asStateFlow()

    private val _tokenInputField = MutableStateFlow(InputField())
    val tokenInputField = _tokenInputField.asStateFlow()

    val canVerifyToken = combine(
        isLoading,
        tokenInputField,
    ) { isLoading, phoneInput ->
        phoneInput.value.text.length == Constants.TOKEN_LENGTH && !isLoading
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )


    fun onPhoneValueChange(value: TextFieldValue) {
        var oldText = phoneInputField.value.value.text
        var newText = formatPhone(value.text)

        if (value.text.length > Constants.PHONE_LENGTH) {
            val textDelta = newText.length - oldText.length
            if (textDelta > 1) {
                _phoneInputField.update { state ->
                    state.copy(
                        value = state.value.copy(
                            text = newText,
                            selection = value.selection
                        )
                    )
                }
            }
            return
        }

        _phoneInputField.update { state ->
            state.copy(
                value = state.value.copy(
                    text = newText,
                    selection = value.selection
                )
            )
        }
    }

    fun onTokenValueChange(value: TextFieldValue) {
        val oldText = tokenInputField.value.value.text
        var newText = formatToken(value.text)

        if (value.text.length > Constants.TOKEN_LENGTH) {
            val textDelta = newText.length - oldText.length
            if (textDelta > 1) {
                _tokenInputField.update { state ->
                    state.copy(
                        value = state.value.copy(
                            text = newText,
                            selection = value.selection
                        )
                    )
                }
            }
            return
        }

        _tokenInputField.update { state ->
            state.copy(
                value = state.value.copy(
                    text = newText,
                    selection = value.selection
                )
            )
        }
    }

    fun onTermsAcceptanceChange(value: Boolean) {
        _areTermsAccepted.update { value }
    }


    fun loadTerms(context: Context, api: API) {
        _isTermsLoading.update { true }
        makeTermsApiCall(
            apiCall = { api.getTermsOfUse(context, it) },
            onCallbackError = { error ->
                viewModelScope.launch {
                    _isTermsLoading.update { false }
                    UiNotificationController.notify(error.asUiText())
                }
            },
            onCallbackSuccess = { terms ->
                _terms.update { terms.replace("<title>user_agreement</title>", "") }
                _isTermsLoading.update { false }
            }
        )
    }


    fun verifyPhone(context: Context, api: API, navigateNext: () -> Unit) {
        if (isLoading.value) return
        _isLoading.update { true }

        val phoneNumber = Constants.COUNTRY_CODE + phoneInputField.value.value.text
        val (result, error) = validatePhone(phoneNumber)
        if (!result) {
            _isLoading.update { false }
            _phoneInputField.update {
                it.copy(
                    supportingText = error!!.asUiText(),
                    isError = true
                )
            }
            return
        }

        makePhoneApiCall(
            apiCall = { api.loginAtAuth(context, phoneNumber, it) },
            onCallbackError = { error ->
                viewModelScope.launch {
                    _isLoading.update { false }
                    UiNotificationController.notify(error.asUiText())
                }
            },
            onCallbackSuccess = {
                _isLoading.update { false }
                _phoneInputField.update {
                    it.copy(
                        supportingText = UiText.DynamicString(""),
                        isError = false
                    )
                }
                navigateNext()
            }
        )
    }


    fun verifyToken(context: Context, api: API, navigateNext: () -> Unit) {
        if (isTokenLoading.value) return
        _isTokenLoading.update { true }

        val token = tokenInputField.value.value.text
        val (result, error) = validateToken(token)
        if (!result) {
            _isTokenLoading.update { false }
            _tokenInputField.update {
                it.copy(
                    supportingText = error!!.asUiText(),
                    isError = true
                )
            }
            return
        }

        makeTokenApiCall(
            apiCall = { api.activateWllet(context, token, Constants.TOKEN_TYPE, it) },
            onCallbackError = { error ->
                viewModelScope.launch {
                    _isTokenLoading.update { false }
                    UiNotificationController.notify(error.asUiText())
                }
            },
            onCallbackSuccess = {
                _isTokenLoading.update { false }
                _tokenInputField.update {
                    it.copy(
                        supportingText = UiText.DynamicString(""),
                        isError = false
                    )
                }
                navigateNext()
            }
        )
    }
}