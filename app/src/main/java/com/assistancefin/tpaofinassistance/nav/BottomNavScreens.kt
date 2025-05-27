package com.assistancefin.tpaofinassistance.nav

import androidx.annotation.DrawableRes
import com.assistancefin.tpaofinassistance.R

enum class BottomNavScreens(val route: NavScreen, @DrawableRes val iconId: Int, val title: String) {
    BotNavMainScreen(NavMainScreen, R.drawable.tpao_fin_ass_main_ic, "Ana Sayfa"),
    BotNavAddingScreen(NavAddingScreen, R.drawable.tpao_fin_ass_add_ic, "Ekle"),
    BotNavHistoryScreen(NavHistoryScreen, R.drawable.tpao_fin_ass_history_ic, "Liste"),
}