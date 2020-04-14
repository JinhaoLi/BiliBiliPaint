package com.jil.paintf.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jil.paintf.R
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.network.BaseWebViewClient
import com.jil.paintf.service.AppPaintF
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private var client:BaseWebViewClient?=null
    private var isLogin=false
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar4)
        client=BaseWebViewClient(this)

        web_view.webViewClient =client
        web_view.settings.javaScriptEnabled =true
        if(AppPaintF.instance.cookie==null){
            web_view.loadUrl("https://passport.bilibili.com/login")
        }else{
            isLogin=true
            web_view.loadUrl("https://m.bilibili.com/space/")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(AppPaintF.instance.cookie!= null)
            menu?.add(1,1,1,"退出登录")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            1->{
                /**此处可以获取id */
                Toast.makeText(this, "点击->编辑资料->退出登录", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val data =Intent(this,MainActivity::class.java)
        if(isLogin){
            finish()
            return
        }
        if(client!=null){
            data.putExtra("uid",client?.uid)
            setResult(Activity.RESULT_OK,data)

        }
        finish()


    }
}
