package com.wynndie.sendytestapp.auth.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.theme.SendyDemoTheme

@Composable
fun AcceptTermsField(
    enabled: Boolean,
    areTermsAccepted: Boolean,
    onTermsAcceptanceChange: (Boolean) -> Unit,
    navigateToTerms: () -> Unit,
    modifier: Modifier = Modifier
) {

    val linkInteractionListener = remember {
        object : LinkInteractionListener {
            override fun onClick(link: LinkAnnotation) {
                navigateToTerms()
            }
        }
    }

    val termsText = stringResource(R.string.terms_of_use)
    val fullText = stringResource(R.string.accept_terms_of_use, termsText)
    val annotatedString = buildAnnotatedString {
        append(fullText)
        val start = fullText.indexOf(termsText)
        val end = start + termsText.length

        val style = if (enabled) {
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        } else {
            SpanStyle(
                color = MaterialTheme.colorScheme.surfaceVariant,
                textDecoration = TextDecoration.Underline
            )
        }

        addStyle(
            style = style,
            start = start,
            end = end
        )
        if (enabled) {
            addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = "terms",
                    linkInteractionListener = linkInteractionListener
                ),
                start = start,
                end = end
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(enabled = enabled) {
                onTermsAcceptanceChange(!areTermsAccepted)
            }
            .padding(end = 8.dp)
            .then(modifier)
    ) {
        Checkbox(
            checked = areTermsAccepted,
            enabled = enabled,
            onCheckedChange = { onTermsAcceptanceChange(it) },
        )
        Text(text = annotatedString)
    }
}

@Preview(showBackground = true)
@Composable
private fun AcceptTermsFieldPreview() {
    SendyDemoTheme {
        AcceptTermsField(
            enabled = false,
            areTermsAccepted = true,
            onTermsAcceptanceChange = {},
            navigateToTerms = { },
            modifier = Modifier.padding(16.dp)
        )
    }
}