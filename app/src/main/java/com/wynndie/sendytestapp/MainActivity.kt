package com.wynndie.sendytestapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.wynndie.sendytestapp.presentation.phone.PhoneScreenRoot
import com.wynndie.sendytestapp.presentation.theme.SendyTestAppTheme
import com.wynndie.sendytestapp.presentation.token.TokenScreenRoot
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
                PhoneScreenRoot(
                    navigateToTokenScreen = {
                        navController.navigate(Route.TokenScreen) {
                            popUpTo<Route.PhoneScreen> { inclusive = true }
                        }
                    },
                    api = api,
                    modifier = modifier
                )
            }
            composable<Route.TokenScreen> {
                TokenScreenRoot(
                    navigateBack = {
                        navController.navigate(Route.PhoneScreen) {
                            popUpTo<Route.TokenScreen> { inclusive = true }
                        }
                    },
                    api = api,
                    modifier = modifier
                )
            }
        }
    }
}