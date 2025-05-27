package com.assistancefin.tpaofinassistance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.assistancefin.tpaofinassistance.nav.BottomNavScreens
import com.assistancefin.tpaofinassistance.nav.LocalNavController
import com.assistancefin.tpaofinassistance.nav.NavMainScreen
import com.assistancefin.tpaofinassistance.nav.routeClass
import com.assistancefin.tpaofinassistance.ui.theme.GrayIcon

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {

    val navHostController = LocalNavController.current
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val config = LocalConfiguration.current
//    val screenWidthDp = config.screenWidthDp.dp
    val destination = navBackStackEntry.routeClass()

    Box(modifier = modifier.fillMaxSize()) {
        content()
        if (BottomNavScreens.entries.any { it.route::class == destination }) {

            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .align(Alignment.BottomCenter)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 5.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Top
                ) {
                    BottomNavScreens.entries.forEach { item ->
                        val isSelectedItem = destination == item.route::class

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    enabled = !isSelectedItem,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        navHostController.navigate(item.route) {
                                            popUpTo(NavMainScreen) {
                                                inclusive = false
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                modifier = Modifier.weight(1f),
                                painter = painterResource(item.iconId),
                                contentDescription = null,
                                tint = if (isSelectedItem) Color.White else GrayIcon
                            )
                            Text(
                                style = MaterialTheme.typography.labelSmall,
                                text = item.title,
                                maxLines = 1,
                                color = if (isSelectedItem) Color.White else GrayIcon
                            )

                        }

                    }
                }
                Box(Modifier.height(systemBottomPadding).fillMaxWidth())
            }

        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewBottomBar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BottomNavigationBar() {

        }
    }

}


