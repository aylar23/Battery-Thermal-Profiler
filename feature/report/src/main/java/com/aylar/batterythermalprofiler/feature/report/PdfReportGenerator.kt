package com.aylar.batterythermalprofiler.feature.report

import android.content.Context
import android.net.Uri
import android.webkit.WebView
import androidx.core.content.FileProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal object PdfReportGenerator {
    suspend fun renderHtmlToPdfInCache(
        context: Context,
        html: String,
        fileName: String,
    ): Uri = withContext(kotlinx.coroutines.Dispatchers.Main) {
        val outFile = File(context.cacheDir, fileName)
        if (outFile.exists()) outFile.delete()

        val webView = WebView(context)
        webView.settings.javaScriptEnabled = false

        suspendCancellableCoroutine { cont ->
            android.print.WebViewPdfWriter.write(
                webView = webView,
                html = html,
                outFile = outFile,
                onSuccess = {
                    tryDestroy(webView)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        outFile,
                    )
                    cont.resume(uri)
                },
                onError = { t ->
                    tryDestroy(webView)
                    cont.resumeWithException(t)
                },
            )
            cont.invokeOnCancellation { tryDestroy(webView) }
        }
    }

    private fun tryDestroy(webView: WebView) {
        runCatching { webView.stopLoading() }
        runCatching { webView.destroy() }
    }
}

