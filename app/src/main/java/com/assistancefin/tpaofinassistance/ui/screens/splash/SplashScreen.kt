package com.assistancefin.tpaofinassistance.ui.screens.splash

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.data.models.StartupResult
import com.assistancefin.tpaofinassistance.data.preferences.PreferManager
import com.assistancefin.tpaofinassistance.nav.LocalNavController
import com.assistancefin.tpaofinassistance.nav.NavSplashScreen
import com.assistancefin.tpaofinassistance.nav.NavStartScreen
import com.assistancefin.tpaofinassistance.nav.NavWelcomeScreen
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import com.assistancefin.tpaofinassistance.utils.Gist
import com.assistancefin.tpaofinassistance.utils.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume

@Composable
fun SplashScreen() {

    val navHostController = LocalNavController.current

    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val context = LocalContext.current

    // Стейт для початку анімації
    val startAnimation = remember { mutableStateOf(false) }

    // Анімація зміщення (починаємо, наприклад, з 400.dp нижче поточного місця)
    val offsetY by animateDpAsState(
        targetValue = if (startAnimation.value) 0.dp else 400.dp,
        animationSpec = tween(durationMillis = 1500)
    )

    // Анімація прозорості
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    // Анімація масштабу
    val scale by animateFloatAsState(
        targetValue = if (startAnimation.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1500)
    )

    BackHandler() {

    }

    // TODO uncomment for web part
    LaunchedEffect(Unit) {
        if (isInternetAvailable(context)) {
            when (val result = determineStartupDestination(context)) {

                is StartupResult.Web -> navHostController.navigate(NavWelcomeScreen(result.url)) {
                    popUpTo(NavSplashScreen) { inclusive = true }
                }

                StartupResult.App ->
                    navHostController.navigate(NavStartScreen) {
                        popUpTo(NavSplashScreen) { inclusive = true }
                    }
            }
        } else {
            navHostController.navigate(NavStartScreen) {
                popUpTo(NavSplashScreen) { inclusive = true }
            }
        }
    }
    //---------------------------------------------

    LaunchedEffect(Unit) {
        startAnimation.value = true
        // TODO comment for web part
//        delay(2000)
//        navHostController.navigate(NavStartScreen) {
//            navHostController.popBackStack()
//        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RedBg),
    ) {
//        Image(
//            modifier = Modifier.fillMaxSize(),
//            painter = painterResource(R.drawable.b_bit_bg),
//            contentDescription = null,
//            contentScale = ContentScale.Crop
//        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.35f))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    // Зміщення по осі Y
                    .offset(y = offsetY)
                    // Масштаб та прозорість через graphicsLayer
                    .graphicsLayer(
                        alpha = alpha,
                        scaleX = scale,
                        scaleY = scale
                    ),
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

private suspend fun determineStartupDestination(context: Context): StartupResult =
    suspendCancellableCoroutine { cont ->
        Log.d("TAG_WEB", "start determineStartupDestination()")

        val openedLink = PreferManager(context).getLink()
        Log.d("TAG_WEB", "openedLink = $openedLink")

        if (openedLink.isNotBlank()) {
            cont.resume(StartupResult.Web(openedLink))
            return@suspendCancellableCoroutine
        }

        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            val gistJson = Gist.getDataJson()
            Log.d("TAG_WEB", "gistJson = $gistJson")
            if (gistJson == null) {
                cont.resume(StartupResult.App)
            } else {
                val listener = getAppsFlyerConversionListener(
                    domen = gistJson.link,
                    context = context,
                    openWeb = { url -> cont.resume(StartupResult.Web(url)) },
                    openApp = { cont.resume(StartupResult.App) }
                )
                AppsFlyerLib.getInstance().init(
                    gistJson.afDevKey,
                    listener,
                    context
                )
                AppsFlyerLib.getInstance().start(
                    context,
                    gistJson.afDevKey,
                    getAppsFlyerRequestListener { cont.resume(StartupResult.App) }
                )
            }
        }
        cont.invokeOnCancellation { }
    }

private fun getAppsFlyerConversionListener(
    domen: String,
    context: Context,
    openWeb: (String) -> Unit,
    openApp: () -> Unit,
) = object : AppsFlyerConversionListener {

    private val isAppsflyerGetData = AtomicBoolean(false)

    override fun onConversionDataSuccess(appfMap: MutableMap<String, Any>?) {
        if (isAppsflyerGetData.getAndSet(true)) return

        if (appfMap != null) {
            val campaign = appfMap["campaign"] as? String
            val afAd = appfMap["af_ad"] as? String
            val media = appfMap["media_source"] as? String

            val afId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)

            Log.d(
                "TAG_WEB",
                "Result: campaign = $campaign | afAd = $afAd | media_source = $media | appfMap = $appfMap"
            )

            val link = "$domen?campaign=$campaign&afAd=$afAd&media=$media&afId=$afId"
            Log.d("TAG_WEB", "link = $link")

            PreferManager(context).saveLink(link)
            Log.d("TAG_WEB", "saved to pref = $link")

            openWeb(link)

        } else openApp()
    }

    override fun onConversionDataFail(p0: String?) {
        if (isAppsflyerGetData.getAndSet(true)) return
        openApp()
    }

    override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
    override fun onAttributionFailure(p0: String?) {}
}

private fun getAppsFlyerRequestListener(openApp: () -> Unit) =
    object : AppsFlyerRequestListener {
        override fun onSuccess() {
            Log.d("TAG_WEB", "AppsFlyer: onSuccess")
        }

        override fun onError(p0: Int, p1: String) {
            Log.d("TAG_WEB", "AppsFlyer: onError")
            openApp()
        }
    }

