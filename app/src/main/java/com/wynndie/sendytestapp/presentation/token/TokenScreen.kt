package com.wynndie.sendytestapp.presentation.token

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.presentation.components.LoadingButton
import com.wynndie.sendytestapp.presentation.model.InputField
import com.wynndie.sendytestapp.presentation.model.TokenType
import com.wynndie.sendytestapp.presentation.theme.SendyTestAppTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun TokenScreenRoot(
    navigateBack: () -> Unit,
    api: API,
    modifier: Modifier = Modifier,
    viewModel: TokenViewModel = viewModel()
) {
    val context = LocalContext.current
    TokenScreen(
        isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value,
        tokenInputField = viewModel.tokenInputField.collectAsStateWithLifecycle().value,
        tokenType = viewModel.tokenType.collectAsStateWithLifecycle().value,
        onTokenValueChange = { viewModel.onTokenValueChange(it) },
        canVerifyToken = viewModel.canVerifyToken.collectAsStateWithLifecycle().value,
        onVerifyToken = { viewModel.verifyToken(context = context, api = api) },
        navigateBack = navigateBack,
        modifier = modifier
    )
}

@Composable
private fun TokenScreen(
    isLoading: Boolean,
    tokenInputField: InputField,
    tokenType: TokenType,
    onTokenValueChange: (String) -> Unit,
    canVerifyToken: Boolean,
    onVerifyToken: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    if (tokenInputField.supportingText != null && tokenInputField.isError == false) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = tokenInputField.supportingText.asString())
        }
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

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
                    Text(
                        text = when (tokenType) {
                            TokenType.SMS -> stringResource(R.string.enter_token_from_sms)
                            TokenType.EMAIL -> stringResource(R.string.enter_token_from_email)
                        }
                    )
                },
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

            Spacer(Modifier.height(16.dp))

            LoadingButton(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                    onVerifyToken()
                },
                enabled = canVerifyToken && !isLoading,
                isLoading = isLoading,
                modifier = Modifier
            ) {
                Text(text = stringResource(R.string.next))
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = navigateBack
            ) {
                Text(text = stringResource(R.string.change_phone))
            }
        }
    }
}

@Preview
@Composable
private fun TokenScreenPreview() {
    SendyTestAppTheme {
        TokenScreen(
            isLoading = false,
            tokenInputField = InputField(),
            tokenType = TokenType.SMS,
            onTokenValueChange = {},
            navigateBack = {},
            modifier = Modifier,
            canVerifyToken = true,
            onVerifyToken = { }
        )
    }
}