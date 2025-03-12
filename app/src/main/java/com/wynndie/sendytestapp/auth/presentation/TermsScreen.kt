package com.wynndie.sendytestapp.auth.presentation

import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wynndie.sendytestapp.R
import com.wynndie.sendytestapp.auth.presentation.components.LoadingButton
import com.wynndie.sendytestapp.theme.SendyDemoTheme
import land.sendy.pfe_sdk.api.API

@Composable
fun TermsScreenRoot(
    api: API,
    navigateBack: () -> Unit,
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {

    val terms = viewModel.terms.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (terms.isBlank()) {
            viewModel.loadTerms(context = context, api = api)
        }
    }

    TermsScreen(
        isLoading = viewModel.isTermsLoading.collectAsStateWithLifecycle().value,
        terms = viewModel.terms.collectAsStateWithLifecycle().value,
        onNavigateBack = navigateBack,
        onAcceptTerms = {
            viewModel.onTermsAcceptanceChange(true)
            navigateBack()
        },
        onLoadTermsClick = {
            viewModel.loadTerms(context = context, api = api)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TermsScreen(
    isLoading: Boolean,
    terms: String,
    onNavigateBack: () -> Unit,
    onAcceptTerms: () -> Unit,
    onLoadTermsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.agreement)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator()
                }
            }

            terms.isBlank() -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(innerPadding)
                ) {
                    Text(
                        text = stringResource(R.string.failed_load_info),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(16.dp))

                    TextButton(
                        onClick = onLoadTermsClick
                    ) {
                        Text(text = stringResource(R.string.refresh))
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    AndroidView(
                        factory = { context ->
                            TextView(context).apply {
                                text = HtmlCompat.fromHtml(terms, HtmlCompat.FROM_HTML_MODE_LEGACY)
                            }
                        },
                        update = { textView ->
                            textView.text =
                                HtmlCompat.fromHtml(terms, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        }
                    )

                    Spacer(Modifier.weight(1f))

                    LoadingButton(
                        onClick = onAcceptTerms,
                        isLoading = false,
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(R.string.accept))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsScreenPreview() {
    SendyDemoTheme {
        TermsScreen(
            isLoading = false,
            terms = "Terms",
            onNavigateBack = {},
            onAcceptTerms = {},
            onLoadTermsClick = {},
            modifier = Modifier
        )
    }
}