package com.wynndie.sendytestapp.presentation.phone

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wynndie.sendytestapp.domain.usecase.MakePhoneApiCall
import com.wynndie.sendytestapp.domain.usecase.ValidatePhone
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.util.asUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import land.sendy.pfe_sdk.api.API

private const val PHONE_LENGTH = 10
private const val COUNTRY_CODE = "+7"

class PhoneViewModel(
    private val validatePhone: ValidatePhone,
    private val makePhoneApiCall: MakePhoneApiCall
) : ViewModel() {

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
        phoneInput.value.length == PHONE_LENGTH && areTermsAccepted && !isLoading
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )


    fun onPhoneValueChange(value: String) {
        var newValue = value

        if (newValue.startsWith(COUNTRY_CODE)) {
            newValue = newValue.drop(COUNTRY_CODE.length)
        }

        newValue = newValue.filter { it.isDigit() }
        if (newValue.length > PHONE_LENGTH) {
            newValue = newValue.dropLast(newValue.length - PHONE_LENGTH)
        }

        _phoneInputField.update { it.copy(value = newValue) }
    }

    fun onTermsAcceptanceChange(value: Boolean) {
        _areTermsAccepted.update { value }
    }

    fun verifyPhone(context: Context, api: API, navigateNext: () -> Unit) {
        if (isLoading.value) {
            return
        }

        _isLoading.update { true }

        val phoneNumber = COUNTRY_CODE + phoneInputField.value.value

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
            apiCall = { callback ->
                api.loginAtAuth(context, phoneNumber, callback)
            },
            onCallbackError = { error ->
                _isLoading.update { false }
                _phoneInputField.update {
                    it.copy(
                        supportingText = error.asUiText(),
                        isError = true
                    )
                }
            },
            onCallbackSuccess = {
                _isLoading.update { false }
                _phoneInputField.update {
                    it.copy(
                        supportingText = null,
                        isError = false
                    )
                }
                navigateNext()
            }
        )
    }
}