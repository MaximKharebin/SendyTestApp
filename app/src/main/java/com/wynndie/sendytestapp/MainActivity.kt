package com.wynndie.sendytestapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.wynndie.sendytestapp.presentation.AuthViewModel
import com.wynndie.sendytestapp.presentation.PhoneScreenRoot
import com.wynndie.sendytestapp.presentation.TokenScreenRoot
import com.wynndie.sendytestapp.presentation.theme.SendyTestAppTheme
import com.wynndie.sendytestapp.presentation.util.Route
import land.sendy.pfe_sdk.activies.MasterActivity
import land.sendy.pfe_sdk.api.API

private const val SERVER_URL = "https://testwallet.sendy.land/"

class MainActivity : MasterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val api = API.getInsatce(SERVER_URL)

        setContent {
            SendyTestAppTheme {

                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainActivityScreen(
                        navController = navController,
                        api = api,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainActivityScreen(
    navController: NavHostController,
    api: API,
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
                val viewModel = it.sharedViewModel<AuthViewModel>(navController)
                PhoneScreenRoot(
                    navigateToCodeScreen = {
                        navController.navigate(Route.TokenScreen) {
                            navController.popBackStack()
                        }
                    },
                    api = api,
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
            composable<Route.TokenScreen> {
                val viewModel = it.sharedViewModel<AuthViewModel>(navController)
                TokenScreenRoot(
                    navigateBack = {
                        navController.navigate(Route.PhoneScreen) {
                            navController.popBackStack()
                        }
                    },
                    api = api,
                    viewModel = viewModel,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(parentEntry)
}