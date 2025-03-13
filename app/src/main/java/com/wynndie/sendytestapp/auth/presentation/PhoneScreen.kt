package com.wynndie.sendytestapp.auth.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wynndie.sendytestapp.core.domain.Constants
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.auth.presentation.components.AcceptTermsField
import com.wynndie.sendytestapp.auth.presentation.components.InfoSection
import com.wynndie.sendytestapp.auth.presentation.components.LoadingButton
import com.wynndie.sendytestapp.auth.presentation.components.TextInputField
import com.wynndie.sendytestapp.auth.presentation.model.InputField
import com.wynndie.sendytestapp.auth.presentation.util.PhoneVisualTransformation
import com.wynndie.sendytestapp.theme.SendyDemoTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun PhoneScreenRoot(
    api: API,
    navigateNext: () -> Unit,
    navigateToTerms: () -> Unit,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val terms = viewModel.terms.collectAsStateWithLifecycle().value
    LaunchedEffect(Unit) {
        if (terms == "") {
            viewModel.loadTerms(context = context, api = api)
        }
    }

    PhoneScreen(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        phoneInputField = viewModel.phoneInputField.collectAsStateWithLifecycle().value,
        areTermsAccepted = viewModel.areTermsAccepted.collectAsStateWithLifecycle().value,
        canVerifyPhone = viewModel.canVerifyPhone.collectAsStateWithLifecycle().value,
        navigateToTerms = navigateToTerms,
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
    onPhoneValueChange: (TextFieldValue) -> Unit,
    areTermsAccepted: Boolean,
    onTermsAcceptanceChange: (Boolean) -> Unit,
    canVerifyPhone: Boolean,
    onVerifyPhone: () -> Unit,
    navigateToTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .imePadding()
    ) {

        Spacer(Modifier.weight(1f))

        InfoSection(
            icon = Icons.Outlined.Autorenew,
            title = stringResource(R.string.recover_access),
            info = stringResource(R.string.recover_access_info),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(64.dp))

        TextInputField(
            value = phoneInputField.value,
            onValueChange = onPhoneValueChange,
            enabled = !isLoading,
            label = stringResource(R.string.phone_label),
            placeholder = stringResource(R.string.phone_placeholder),
            supportingText = phoneInputField.supportingText.asString(),
            isError = phoneInputField.isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onVerifyPhone() }
            ),
            prefix = Constants.COUNTRY_CODE,
            visualTransformation = PhoneVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(4.dp))

        AcceptTermsField(
            enabled = !isLoading,
            areTermsAccepted = areTermsAccepted,
            onTermsAcceptanceChange = onTermsAcceptanceChange,
            navigateToTerms = navigateToTerms,
            modifier = Modifier
        )

        Spacer(Modifier.weight(1f))
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
    SendyDemoTheme {
        PhoneScreen(
            isLoading = false,
            phoneInputField = InputField(),
            navigateToTerms = {},
            onPhoneValueChange = {},
            areTermsAccepted = false,
            onTermsAcceptanceChange = {},
            canVerifyPhone = false,
            onVerifyPhone = {},
            modifier = Modifier
        )
    }
}