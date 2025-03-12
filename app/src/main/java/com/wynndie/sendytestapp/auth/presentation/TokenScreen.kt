package com.wynndie.sendytestapp.auth.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.auth.presentation.components.InfoSection
import com.wynndie.sendytestapp.auth.presentation.components.LoadingButton
import com.wynndie.sendytestapp.auth.presentation.components.TextInputField
import com.wynndie.sendytestapp.auth.presentation.model.InputField
import com.wynndie.sendytestapp.theme.SendyDemoTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun TokenScreenRoot(
    api: API,
    navigateBack: () -> Unit,
    navigateNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    TokenScreen(
        isLoading = viewModel.isTokenLoading.collectAsStateWithLifecycle().value,
        tokenInputField = viewModel.tokenInputField.collectAsStateWithLifecycle().value,
        onTokenValueChange = { viewModel.onTokenValueChange(it) },
        canVerifyToken = viewModel.canVerifyToken.collectAsStateWithLifecycle().value,
        onVerifyToken = {
            viewModel.verifyToken(
                context = context,
                api = api,
                navigateNext = navigateNext
            )
        },
        navigateBack = navigateBack,
        modifier = modifier
    )
}

@Composable
private fun TokenScreen(
    isLoading: Boolean,
    tokenInputField: InputField,
    onTokenValueChange: (String) -> Unit,
    canVerifyToken: Boolean,
    onVerifyToken: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .imePadding()
    ) {

        Spacer(Modifier.weight(1f))

        InfoSection(
            icon = Icons.Outlined.Sms,
            title = stringResource(R.string.confirm_phone),
            info = stringResource(R.string.confirm_phone_info),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(64.dp))

        TextInputField(
            value = tokenInputField.value,
            onValueChange = onTokenValueChange,
            enabled = !isLoading,
            label = stringResource(R.string.token_label),
            placeholder = stringResource(R.string.token_placeholder),
            supportingText = tokenInputField.supportingText.asString(),
            isError = tokenInputField.isError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onVerifyToken() }
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                onClick = navigateBack,
                enabled = !isLoading
            ) {
                Text(text = stringResource(R.string.change_phone))
            }

            Spacer(Modifier.height(8.dp))

            LoadingButton(
                onClick = {
                    focusManager.clearFocus(true)
                    onVerifyToken()
                },
                enabled = canVerifyToken && !isLoading,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.next))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TokenScreenPreview() {
    SendyDemoTheme {
        TokenScreen(
            isLoading = false,
            tokenInputField = InputField(),
            onTokenValueChange = { },
            navigateBack = { },
            canVerifyToken = true,
            onVerifyToken = { },
            modifier = Modifier
        )
    }
}