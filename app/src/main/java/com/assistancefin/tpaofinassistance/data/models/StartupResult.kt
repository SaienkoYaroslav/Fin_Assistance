package com.assistancefin.tpaofinassistance.data.models

sealed class StartupResult {
    data class Web(val url: String) : StartupResult()
    data object App : StartupResult()
}