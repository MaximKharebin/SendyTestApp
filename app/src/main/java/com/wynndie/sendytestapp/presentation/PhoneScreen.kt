package com.wynndie.sendytestapp.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.theme.SendyTestAppTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun PhoneScreenRoot(
    navigateToCodeScreen: () -> Unit,
    api: API,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = AuthViewModel()
) {
    val context = LocalContext.current
    PhoneScreen(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,

        phoneInputField = viewModel.phoneInputField.collectAsStateWithLifecycle().value,
        onPhoneValueChange = { viewModel.onPhoneValueChange(it) },

        areTermsAccepted = viewModel.areTermsAccepted.collectAsStateWithLifecycle().value,
        onTermsAcceptanceChange = { viewModel.onTermsAcceptanceChange(it) },

        canVerifyPhone = viewModel.canVerifyPhone.collectAsStateWithLifecycle().value,
        onVerifyPhone = {
            viewModel.verifyPhone(
                context = context,
                api = api,
                navigateToTokenScreen = navigateToCodeScreen
            )
        },

        modifier = modifier
    )
}

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
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = areTermsAccepted,
                onCheckedChange = { onTermsAcceptanceChange(it) }
            )
            Text(text = stringResource(R.string.accept_terms_of_use))
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus(true)
                onVerifyPhone()
            },
            enabled = canVerifyPhone && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(R.string.next))
            }
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