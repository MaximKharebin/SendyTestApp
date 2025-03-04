package com.wynndie.sendytestapp.presentation.phone

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.util.UiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import land.sendy.pfe_sdk.api.API
import land.sendy.pfe_sdk.model.types.ApiCallback
import java.text.DecimalFormat

private const val PHONE_LENGTH = 10
private const val PHONE_PATTERN =
    "(^8|7|\\+7)((\\d{10})|(\\s\\(\\d{3}\\)\\s\\d{3}\\s\\d{2}\\s\\d{2}))"
private const val COUNTRY_CODE = "+7"

class PhoneViewModel : ViewModel() {

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
        SharingStarted.Companion.WhileSubscribed(5000),
        false
    )


    fun onPhoneValueChange(value: String) {
        var newValue = value

        if (newValue.startsWith(COUNTRY_CODE)) {
            newValue = newValue.drop(COUNTRY_CODE.length)
        }

        if (newValue.length > PHONE_LENGTH) {
            newValue = newValue.dropLast(newValue.length - PHONE_LENGTH)
        }

        _phoneInputField.update { it.copy(value = newValue) }
    }

    fun onTermsAcceptanceChange(value: Boolean) {
        _areTermsAccepted.update { value }
    }

    fun verifyPhone(context: Context, api: API, navigateToTokenScreen: () -> Unit) {
        _isLoading.update { true }

        val phoneNumber = COUNTRY_CODE + phoneInputField.value.value
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
                    navigateToTokenScreen()
                }
                _isLoading.update { false }
            }
        }

        val runResult = api.loginAtAuth(context, phoneNumber, callback)
        if (runResult != null && runResult.hasError()) {
            updateSupportingText(
                flow = _phoneInputField,
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