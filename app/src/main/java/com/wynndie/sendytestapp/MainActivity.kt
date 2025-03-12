package com.wynndie.sendytestapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.wynndie.sendytestapp.auth.presentation.AuthViewModel
import com.wynndie.sendytestapp.auth.presentation.PhoneScreenRoot
import com.wynndie.sendytestapp.auth.presentation.TermsScreenRoot
import com.wynndie.sendytestapp.auth.presentation.TokenScreenRoot
import com.wynndie.sendytestapp.core.presentation.UiNotificationController
import com.wynndie.sendytestapp.core.presentation.viewModelFactory
import com.wynndie.sendytestapp.MainViewModel
import com.wynndie.sendytestapp.Route
import com.wynndie.sendytestapp.theme.SendyDemoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import land.sendy.pfe_sdk.activies.MasterActivity
import land.sendy.pfe_sdk.api.API

class MainActivity : MasterActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val api = API.getInsatce(SERVER_URL)

        setContent {
            SendyDemoTheme {
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(lifecycleOwner.lifecycle, UiNotificationController.notification) {
                    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        withContext(Dispatchers.Main.immediate) {
                            UiNotificationController.notification.collect {
                                viewModel.changeNotificationText(it)
                                viewModel.toggleDialogVisibility(visible = true)
                            }
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier
                ) { innerPadding ->

                    if (viewModel.isDialogVisible.collectAsStateWithLifecycle().value) {
                        AlertDialog(
                            onDismissRequest = {
                                viewModel.toggleDialogVisibility(visible = false)
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = { viewModel.toggleDialogVisibility(visible = false) }
                                ) {
                                    Text(text = stringResource(R.string.ok))
                                }
                            },
                            title = {
                                Text(
                                    text = stringResource(R.string.error)
                                )
                            },
                            text = {
                                Text(
                                    text = viewModel.notificationText.collectAsStateWithLifecycle().value.asString()
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                        )
                    }

                    MainActivityScreen(
                        api = api,
                        navController = rememberNavController(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    companion object {
        private const val SERVER_URL = "https://testwallet.sendy.land/"
    }
}

@Composable
fun MainActivityScreen(
    api: API,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.AuthGraph
    ) {
        navigation<Route.AuthGraph>(
            startDestination = Route.PhoneScreen
        ) {
            composable<Route.PhoneScreen> {
                val viewModel = it.sharedViewModel<AuthViewModel>(
                    navController = navController,
                    factory = viewModelFactory {
                        AuthViewModel(
                            makeTermsApiCall = App.Companion.appModule.makeTermsApiCall,
                            makePhoneApiCall = App.Companion.appModule.makePhoneApiCall,
                            makeTokenApiCall = App.Companion.appModule.makeTokenApiCall,
                            validatePhone = App.Companion.appModule.validatePhone,
                            validateToken = App.Companion.appModule.validateToken,
                            formatPhone = App.Companion.appModule.formatPhone,
                            formatToken = App.Companion.appModule.formatToken
                        )
                    }
                )
                PhoneScreenRoot(
                    api = api,
                    navigateNext = { navController.navigate(Route.TokenScreen) },
                    navigateToTerms = { navController.navigate(Route.TermsScreen) },
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            composable<Route.TermsScreen> {
                val viewModel = it.sharedViewModel<AuthViewModel>(
                    navController = navController,
                    factory = viewModelFactory {
                        AuthViewModel(
                            makeTermsApiCall = App.Companion.appModule.makeTermsApiCall,
                            makePhoneApiCall = App.Companion.appModule.makePhoneApiCall,
                            makeTokenApiCall = App.Companion.appModule.makeTokenApiCall,
                            validatePhone = App.Companion.appModule.validatePhone,
                            validateToken = App.Companion.appModule.validateToken,
                            formatPhone = App.Companion.appModule.formatPhone,
                            formatToken = App.Companion.appModule.formatToken
                        )
                    }
                )
                TermsScreenRoot(
                    api = api,
                    navigateBack = { navController.navigateUp() },
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            composable<Route.TokenScreen> {
                val viewModel = it.sharedViewModel<AuthViewModel>(
                    navController = navController,
                    factory = viewModelFactory {
                        AuthViewModel(
                            makeTermsApiCall = App.Companion.appModule.makeTermsApiCall,
                            makePhoneApiCall = App.Companion.appModule.makePhoneApiCall,
                            makeTokenApiCall = App.Companion.appModule.makeTokenApiCall,
                            validatePhone = App.Companion.appModule.validatePhone,
                            validateToken = App.Companion.appModule.validateToken,
                            formatPhone = App.Companion.appModule.formatPhone,
                            formatToken = App.Companion.appModule.formatToken
                        )
                    }
                )
                TokenScreenRoot(
                    api = api,
                    navigateBack = { navController.navigateUp() },
                    navigateNext = {
                        navController.navigate(Route.FinalScreen) {
                            popUpTo<Route.AuthGraph> { inclusive = true }
                        }
                    },
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
        }
        composable<Route.FinalScreen> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxSize()
            ) {
                Text(text = stringResource(R.string.auth_succeed))

                Spacer(Modifier.height(32.dp))

                TextButton(
                    onClick = {
                        navController.navigate(Route.AuthGraph) {
                            popUpTo<Route.FinalScreen> { inclusive = true }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.start_again))
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
    factory: ViewModelProvider.Factory
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel(factory = factory)
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry, factory = factory)
}