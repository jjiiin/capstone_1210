package com.capstone_design.a1209_app

import android.annotation.TargetApi
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.capstone_design.a1209_app.databinding.ActivityAddressSearchBinding
import com.capstone_design.a1209_app.databinding.ActivityWebSearchBinding

//도로명 주소api로 주소 찾기
class WebSearchActivity : AppCompatActivity() {
    private var webView: WebView? = null
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK fc81b72e05032dce7689cf5b13b5a9cc"  // REST API 키
    }

    private lateinit var binding: ActivityWebSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_search)

        webView = binding.webView
//        WebView.setWebContentsDebuggingEnabled(true)
//        webView!!.apply {
//            settings.javaScriptEnabled = true
//            settings.javaScriptCanOpenWindowsAutomatically = true
//            settings.setSupportMultipleWindows(true)
//            webViewClient = client
//
//           //webChromeClient = WebChromeClient()
//        }
//        webView!!.loadUrl("api 호출하는 웹 주소")
        //searchKeyword("은행")
    }

        var client: WebViewClient = object : WebViewClient() {
            @TargetApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }
        }
    }


