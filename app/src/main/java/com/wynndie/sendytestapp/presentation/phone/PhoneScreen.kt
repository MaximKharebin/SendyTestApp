package com.wynndie.sendytestapp.presentation.phone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wynndie.sendytestapp.App
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.components.LoadingButton
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.util.PhoneVisualTransformation
import com.wynndie.sendytestapp.presentation.util.viewModelFactory
import com.wynndie.sendytestapp.theme.SendyTestAppTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun PhoneScreenRoot(
    api: API,
    navigateNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhoneViewModel = viewModel(
        factory = viewModelFactory {
            PhoneViewModel(
                validatePhone = App.appModule.validatePhone,
                makePhoneApiCall = App.appModule.makePhoneApiCall
            )
        }
    )
) {
    val context = LocalContext.current
    PhoneScreen(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        phoneInputField = viewModel.phoneInputField.collectAsStateWithLifecycle().value,
        areTermsAccepted = viewModel.areTermsAccepted.collectAsStateWithLifecycle().value,
        canVerifyPhone = viewModel.canVerifyPhone.collectAsStateWithLifecycle().value,
        onPhoneValueChange = { viewModel.onPhoneValueChange(it) },
        onTermsAcceptanceChange = { viewModel.onTermsAcceptanceChange(it) },
        onVerifyPhone = {
            viewModel.verifyPhone(
                context = context,
                api = api,
                navigateNext = navigateNext
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneScreen(
    isLoading: Boolean,
    phoneInputField: InputField,
    onPhoneValueChange: (String) -> Unit,
    areTermsAccepted: Boolean,
    onTermsAcceptanceChange: (Boolean) -> Unit,
    canVerifyPhone: Boolean,
    onVerifyPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {

        OutlinedTextField(
            value = phoneInputField.value,
            onValueChange = { onPhoneValueChange(it) },
            supportingText = {
                phoneInputField.supportingText?.let {
                    Text(text = it.asString())
                }
            },
            isError = phoneInputField.isError,
            label = { Text(text = stringResource(R.string.enter_phone)) },
            prefix = { Text(text = stringResource(R.string.phone_country_code)) },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus(true) }
            ),
            visualTransformation = PhoneVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .then(
                    if (!isLoading) {
                        Modifier.clickable { onTermsAcceptanceChange(!areTermsAccepted) }
                    } else {
                        Modifier
                    }
                )
                .padding(end = 8.dp)
        ) {
            Checkbox(
                checked = areTermsAccepted,
                enabled = !isLoading,
                onCheckedChange = { onTermsAcceptanceChange(it) }
            )
            Text(text = stringResource(R.string.accept_terms_of_use))
        }

        Spacer(Modifier.height(16.dp))

        LoadingButton(
            onClick = {
                focusManager.clearFocus(true)
                onVerifyPhone()
            },
            enabled = canVerifyPhone,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.next))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoneScreenPreview() {
    SendyTestAppTheme {
        PhoneScreen(
            isLoading = false,
            phoneInputField = InputField(),
            onPhoneValueChange = {},
            areTermsAccepted = false,
            onTermsAcceptanceChange = {},
            canVerifyPhone = false,
            onVerifyPhone = {},
            modifier = Modifier
        )
    }
}