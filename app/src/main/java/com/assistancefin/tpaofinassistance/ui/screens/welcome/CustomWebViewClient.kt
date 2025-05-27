package com.assistancefin.tpaofinassistance.ui.screens.welcome

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient(
    val callBackFinished: (Boolean) -> Unit,
    val openApp: () -> Unit
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {

        Log.d("TAG_WEB","redirect: ${request?.url.toString()}")

        if (request?.url.toString().contains("https://ssdfdsfs54d6ds")) {
            Log.d("TAG_WEB","contains: (ssdfdsfs54d6ds) | відкриваємо Гру")
            openApp()
            return true
        }
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d("TAG_WEB", "onPageFinished: $url")

        if (!url.isNullOrBlank() && url != "about:blank") {
            callBackFinished(true)
        }
    }
}