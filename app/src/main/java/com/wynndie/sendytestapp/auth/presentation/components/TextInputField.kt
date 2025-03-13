package com.wynndie.sendytestapp.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wynndie.sendytestapp.core.domain.Constants
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.auth.presentation.util.PhoneVisualTransformation
import com.wynndie.sendytestapp.theme.SendyDemoTheme

@Composable
fun TextInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean,
    label: String,
    placeholder: String,
    supportingText: String,
    isError: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    prefix: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            enabled = enabled,
            placeholder = {
                Text(text = placeholder)
            },
            prefix = {
                if (prefix != null) {
                    Text(text = prefix)
                } else {
                    null
                }
            },
            supportingText = {
                Text(text = supportingText)
            },
            isError = isError,
            singleLine = true,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = TextFieldDefaults.colors(
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                errorContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.small,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TextInputFieldPreview() {
    SendyDemoTheme {
        TextInputField(
            value = TextFieldValue("234234"),
            onValueChange = {},
            enabled = true,
            label = stringResource(R.string.phone_label),
            placeholder = stringResource(R.string.phone_placeholder),
            supportingText = stringResource(R.string.no_internet),
            isError = false,
            keyboardOptions = KeyboardOptions(),
            keyboardActions = KeyboardActions(),
            prefix = Constants.COUNTRY_CODE,
            visualTransformation = PhoneVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}