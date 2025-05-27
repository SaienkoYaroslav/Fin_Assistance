package com.assistancefin.tpaofinassistance.ui.screens.start

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.data.models.OnboardingPage
import com.assistancefin.tpaofinassistance.nav.LocalNavController
import com.assistancefin.tpaofinassistance.nav.NavMainScreen
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import kotlinx.coroutines.launch

@Composable
fun StartScreen() {
    val navHostController = LocalNavController.current
    val context = LocalContext.current
    val systemTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    val scope = rememberCoroutineScope()

    BackHandler {
        (context as Activity).moveTaskToBack(false)
    }

    val pages = listOf(
        OnboardingPage(
            R.drawable.tpao_fin_ass_logo_img,
            "Her harcama kontrol altında!",
            "Kahveden faturaya kadar her şeyi takip et, gizli fazlalıkları keşfet"
        ),
        OnboardingPage(
            R.drawable.tpao_fin_ass_logo_img,
            "Finansal farkındalık burada başlar!",
            "Paranı bilinçli yönetmenin ilk adımını at"
        ),
        OnboardingPage(
            R.drawable.tpao_fin_ass_logo_img,
            "Senin kişisel finans asistanın!",
            "Bütçeni dengede tutan akıllı hesaplayıcı her zaman seninle"
        )
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RedBg ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(systemTopPadding))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val p = pages[page]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(p.imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = p.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = p.subtitle,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }
            CustomPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        navHostController.navigate(NavMainScreen)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = if (pagerState.currentPage == pages.size - 1) "Başlangıç" else "Daha ileri",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(systemBottomPadding))
        }
    }

}

@Composable
fun CustomPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.White.copy(alpha = 0.2f),
    activeSize: Dp = 12.dp,
    inactiveSize: Dp = 8.dp,
    spacing: Dp = 8.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagerState.pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .size(if (isSelected) activeSize else inactiveSize)
                    .clip(CircleShape)
                    .background(if (isSelected) activeColor else inactiveColor)
            )
            if (index < pagerState.pageCount - 1) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}



