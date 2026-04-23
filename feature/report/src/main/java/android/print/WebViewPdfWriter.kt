package android.print

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File

/**
 * This file intentionally lives in the `android.print` package to access
 * package-private callback constructors on older Android stubs.
 */
internal object WebViewPdfWriter {
    fun write(
        webView: WebView,
        html: String,
        outFile: File,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        webView.webViewClient =
            object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    try {
                        val adapter = webView.createPrintDocumentAdapter("battery-thermal-report")
                        val attrs = PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                            .build()

                        adapter.onLayout(
                            null,
                            attrs,
                            CancellationSignal(),
                            object : PrintDocumentAdapter.LayoutResultCallback() {
                                override fun onLayoutFinished(info: PrintDocumentInfo?, changed: Boolean) {
                                    try {
                                        val pfd = ParcelFileDescriptor.open(
                                            outFile,
                                            ParcelFileDescriptor.MODE_CREATE or ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE,
                                        )
                                        adapter.onWrite(
                                            arrayOf(PageRange.ALL_PAGES),
                                            pfd,
                                            CancellationSignal(),
                                            object : PrintDocumentAdapter.WriteResultCallback() {
                                                override fun onWriteFinished(pages: Array<PageRange>) {
                                                    runCatching { pfd.close() }
                                                    onSuccess()
                                                }

                                                override fun onWriteFailed(error: CharSequence?) {
                                                    runCatching { pfd.close() }
                                                    onError(IllegalStateException(error?.toString() ?: "PDF write failed"))
                                                }
                                            },
                                        )
                                    } catch (t: Throwable) {
                                        onError(t)
                                    }
                                }

                                override fun onLayoutFailed(error: CharSequence?) {
                                    onError(IllegalStateException(error?.toString() ?: "PDF layout failed"))
                                }
                            },
                            null,
                        )
                    } catch (t: Throwable) {
                        onError(t)
                    }
                }
            }

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }
}

