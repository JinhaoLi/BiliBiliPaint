package com.jil.paintf.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.jil.paintf.service.AppPaintF;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseWebViewClient extends WebViewClient {
    private Context mContext;
    public int uid;

    public BaseWebViewClient(Context context) {
        this.mContext =context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.i("页面开始加载->",url);
        CookieManager cookieManager =CookieManager.getInstance();
        cookieManager.setCookie(url, AppPaintF.getCookieStr());
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.i("页面加载完成->",url);
        if(url.equals("https://m.bilibili.com/index.html")){
            /** 登录完成跳转至此**/
            CookieManager cookieManager =CookieManager.getInstance();
            String cookieStr =cookieManager.getCookie(url);
            AppPaintF.setCookieStr(cookieStr);
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("cookie",cookieStr).apply();
            Toast.makeText(mContext,"已登录",Toast.LENGTH_SHORT).show();
            view.loadUrl("https://m.bilibili.com/space/");
        }

        if(url.startsWith("https://m.bilibili.com/space/")){
            /**此处可以获取id**/
            Toast.makeText(mContext,"点击-->编辑资料\t 退出登录！",Toast.LENGTH_SHORT).show();
            String idStr = null;
            Pattern p = Pattern.compile("\\d{1,9}");
            Matcher m = p.matcher(url);
            if(m.find()){
                idStr=m.group();
                uid =Integer.parseInt(idStr);
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("uid", uid).apply();
            }
        }

        if(url.equals("https://passport.bilibili.com/login?act=exit")){
            /**退出登录**/
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putInt("uid",-1).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("cookie","null").apply();
            AppPaintF.setCookieStr("null");
            view.loadUrl("https://passport.bilibili.com/login");
        }


    }
}
