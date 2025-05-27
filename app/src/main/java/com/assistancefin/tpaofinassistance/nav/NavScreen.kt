package com.assistancefin.tpaofinassistance.nav

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed interface NavScreen


@Serializable
data object NavSplashScreen : NavScreen

@Serializable
data object NavStartScreen : NavScreen

@Serializable
data object NavMainScreen : NavScreen

@Serializable
data object NavAddingScreen : NavScreen

@Serializable
data object NavHistoryScreen : NavScreen


@Serializable
data class NavWelcomeScreen(
    val welcome: String = ""
) : NavScreen


fun NavBackStackEntry?.routeClass(): KClass<*>? {
    return this?.destination?.route
        ?.split("?")
        ?.first()
        ?.let { Class.forName(it)}
        ?.kotlin
}