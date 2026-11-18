package com.example.moonday.ui.components

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AnimationWebView(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true,
    assetPath: String = "file:///android_asset/moon-phases/app_opening.html",
    height: Dp = 260.dp
) {
    val themeMode = if (isDarkTheme) "dark" else "light"

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(0x00000000)
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        val script = "if (typeof setTheme === 'function') { setTheme('$themeMode'); } else if ('$themeMode' === 'light') { document.body.classList.add('light-theme'); } else { document.body.classList.remove('light-theme'); }"
                        view?.evaluateJavascript(script, null)
                    }
                }
                settings.apply {
                    javaScriptEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    @Suppress("DEPRECATION")
                    allowFileAccessFromFileURLs = true
                    @Suppress("DEPRECATION")
                    allowUniversalAccessFromFileURLs = true
                    domStorageEnabled = true
                }
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
            }
        },
        update = { webView ->
            if (webView.url.isNullOrEmpty()) {
                webView.loadUrl(assetPath)
            } else {
                val script = "if ('$themeMode' === 'light') { document.body.classList.add('light-theme'); } else { document.body.classList.remove('light-theme'); }"
                webView.evaluateJavascript(script, null)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}
