package com.assistancefin.tpaofinassistance.ui.screens.welcome

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.nav.NavStartScreen
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WelcomeScreen(
    navHostController: NavHostController,
    welcome: String
) {
    val scope = rememberCoroutineScope()
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    var isOnPageFinished by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = CustomWebViewClient(
                            callBackFinished = { boolean ->
                                scope.launch {
                                    delay(500)
                                    isOnPageFinished = boolean
                                }
                            },
                            openApp = {
                                navHostController.navigate(NavStartScreen) {
                                    navHostController.popBackStack()
                                }
                            }
                        )
                        settings.domStorageEnabled = true
                        settings.javaScriptEnabled = true
                    }
                },
                update = { webView ->
                    Log.d("TAG_WEB", "WelcomeScreen: $welcome")
                    webView.loadUrl(welcome)
                }
            )
        }
        if (!isOnPageFinished) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RedBg),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(0.35f))
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(),
                        painter = painterResource(id = R.drawable.tpao_fin_ass_logo_img),
                        contentDescription = null,
                    )
                    Spacer(Modifier.weight(0.55f))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(bottom = systemBottomPadding),
                        color = Color.White
                    )
                    Spacer(Modifier.weight(0.1f))
                }
            }

        }
    }
}


