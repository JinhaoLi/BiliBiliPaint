package com.jil.paintf.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.jil.paintf.R
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.network.BaseWebViewClient
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var client:BaseWebViewClient?=null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_login)
        client=BaseWebViewClient(this)
        web_view.webViewClient =client
        web_view.settings.javaScriptEnabled =true
        web_view.loadUrl("https://passport.bilibili.com/login")
    }

    override fun onBackPressed() {
        val data =Intent(this,MainActivity::class.java)
        if(client!=null){
            data.putExtra("uid",client?.uid)
        }

        setResult(Activity.RESULT_OK,data)
        finish()
    }
}
