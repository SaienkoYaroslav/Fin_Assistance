package com.assistancefin.tpaofinassistance.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.nav.BottomNavScreens
import com.assistancefin.tpaofinassistance.nav.LocalNavController
import com.assistancefin.tpaofinassistance.nav.NavGraph
import com.assistancefin.tpaofinassistance.nav.routeClass
import com.assistancefin.tpaofinassistance.ui.components.BottomNavigationBar
import com.assistancefin.tpaofinassistance.ui.theme.BgGray
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import com.assistancefin.tpaofinassistance.ui.theme.TPAOFinAssistanceTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPAOFinAssistanceTheme {

                val systemUiController = rememberSystemUiController()
                val navHostController = rememberNavController()
                val navBackStackEntry by navHostController.currentBackStackEntryAsState()
                val destination = navBackStackEntry.routeClass()

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        isNavigationBarContrastEnforced = false,
                        darkIcons = false
                    )
                }

                Scaffold(
                    contentWindowInsets = WindowInsets.statusBars.only(
                        WindowInsetsSides.Horizontal
                    ),
                    topBar = {
                        if (BottomNavScreens.entries.any { it.route::class == destination }) {
                            CustomTopBar()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(BgGray)
                            .padding(innerPadding)
                    ) {
                        CompositionLocalProvider(
                            LocalNavController provides navHostController
                        ) {
                            BottomNavigationBar(
                                modifier = Modifier
                            ) {
                                NavGraph()
                            }
                        }

                    }
                }

            }
        }
    }
}

@Composable
fun CustomTopBar(modifier: Modifier = Modifier) {
    val systemTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(RedBg)
            .padding(top = systemTopPadding),
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            modifier = Modifier.padding(10.dp),
            painter = painterResource(R.drawable.tpao_fin_ass_logo_imgx3),
            contentDescription = null,
        )
    }
}
