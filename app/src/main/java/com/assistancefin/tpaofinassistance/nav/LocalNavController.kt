package com.assistancefin.tpaofinassistance.nav

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController


val LocalNavController =
    staticCompositionLocalOf<NavHostController> { error("Can't access NavController") }
