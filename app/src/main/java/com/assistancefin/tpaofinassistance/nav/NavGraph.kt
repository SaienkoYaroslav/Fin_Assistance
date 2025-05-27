package com.assistancefin.tpaofinassistance.nav

import androidx.compose.animation.EnterTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.assistancefin.tpaofinassistance.ui.screens.adding.AddingScreen
import com.assistancefin.tpaofinassistance.ui.screens.history.HistoryScreen
import com.assistancefin.tpaofinassistance.ui.screens.main.MainScreen
import com.assistancefin.tpaofinassistance.ui.screens.splash.SplashScreen
import com.assistancefin.tpaofinassistance.ui.screens.start.StartScreen
import com.assistancefin.tpaofinassistance.ui.screens.welcome.WelcomeScreen

@Composable
fun NavGraph(
) {
    val navHostController = LocalNavController.current
    NavHost(
        navController = navHostController,
        startDestination = NavSplashScreen
    ) {

        composable<NavSplashScreen> { SplashScreen() }

        composable<NavStartScreen> { StartScreen() }

        composable<NavMainScreen> { MainScreen() }

        composable<NavAddingScreen> { AddingScreen() }

        composable<NavHistoryScreen> { HistoryScreen() }

        composable<NavWelcomeScreen>(
            // Вимикаємо анімації для цього конкретного екрану
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
        ) {
            val args = it.toRoute<NavWelcomeScreen>()
            WelcomeScreen(
                navHostController = navHostController,
                welcome = args.welcome
            )
        }

    }
}