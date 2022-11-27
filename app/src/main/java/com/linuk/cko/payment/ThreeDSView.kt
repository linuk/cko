package com.linuk.cko.payment

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.linuk.cko.ui.theme.CKOTheme

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ThreeDSView(viewModel: PaymentViewModel, url: String) {
    // Back to Payment Details view on back press
    BackHandler {
        viewModel.onViewChanged(ViewType.PaymentDetails)
    }
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context -> buildWebClient(context, url, viewModel) }
    )
}

@SuppressLint("SetJavaScriptEnabled")
private fun buildWebClient(context: Context, url: String, viewModel: PaymentViewModel) =
    WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val newUrl: String = request?.url.toString()
                return when {
                    newUrl.startsWith(SUCCESS_PAYMENT_REDIRECTION_URL) -> {
                        viewModel.onViewChanged(ViewType.PaymentResult(isSuccessful = true))
                        true
                    }
                    newUrl.startsWith(FAILURE_PAYMENT_REDIRECTION_URL) -> {
                        viewModel.onViewChanged(ViewType.PaymentResult(isSuccessful = false))
                        true
                    }
                    else -> false
                }
            }
        }
        loadUrl(url)

        // Enable url redirection
        settings.javaScriptEnabled = true
    }

@Preview(showBackground = true)
@Composable
fun ThreeDSPreview() {
    CKOTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
        ) {
            ThreeDSView(PaymentViewModel(), "https://tinyurl.com/hey33")
        }
    }
}