package com.wynndie.sendytestapp.presentation.token

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wynndie.sendytestapp.App
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.components.LoadingButton
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.util.viewModelFactory
import com.wynndie.sendytestapp.theme.SendyTestAppTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun TokenScreenRoot(
    api: API,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TokenViewModel = viewModel(
        factory = viewModelFactory {
            TokenViewModel(
                validateToken = App.appModule.validateToken,
                makeTokenApiCall = App.appModule.makeTokenApiCall
            )
        }
    )
) {
    val context = LocalContext.current
    TokenScreen(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        tokenInputField = viewModel.tokenInputField.collectAsStateWithLifecycle().value,
        onTokenValueChange = { viewModel.onTokenValueChange(it) },
        canVerifyToken = viewModel.canVerifyToken.collectAsStateWithLifecycle().value,
        onVerifyToken = {
            viewModel.verifyToken(
                context = context,
                api = api
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        if (tokenInputField.supportingText != null && tokenInputField.isError == false) {
            Text(text = stringResource(R.string.auth_succeed))
        } else {
            OutlinedTextField(
                value = tokenInputField.value,
                onValueChange = { onTokenValueChange(it) },
                supportingText = {
                    tokenInputField.supportingText?.let {
                        Text(text = it.asString())
                    }
                },
                isError = tokenInputField.isError,
                label = {
                    Text(text = stringResource(R.string.enter_token_from_sms))
                },
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus(true) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

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

        Spacer(Modifier.height(32.dp))

        TextButton(
            onClick = navigateBack
        ) {
            Text(text = stringResource(R.string.change_phone))
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun TokenScreenPreview() {
    SendyTestAppTheme {
        TokenScreen(
            isLoading = false,
            tokenInputField = InputField(),
            onTokenValueChange = { },
            navigateBack = { },
            modifier = Modifier,
            canVerifyToken = true,
            onVerifyToken = { }
        )
    }
}