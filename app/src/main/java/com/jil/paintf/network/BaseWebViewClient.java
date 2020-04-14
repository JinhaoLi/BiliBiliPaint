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
import com.jil.paintf.service.DataRoomService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseWebViewClient extends WebViewClient {
    private Context mContext;
    public int uid=-1;

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
        Log.i("webView页面开始加载->",url);

        if(AppPaintF.instance.getCookie()!=null){
            CookieManager cookieManager =CookieManager.getInstance();
            cookieManager.setCookie(url, AppPaintF.instance.getCookie().toString());
        }

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.i("webView页面开始加载->",url);
        if(url.equals("https://m.bilibili.com/index.html")){
            /** 登录完成跳转至此**/
            CookieManager cookieManager =CookieManager.getInstance();
            String cookieStr =cookieManager.getCookie(url);
            NetCookie netCookie= new NetCookie(cookieStr);
            DataRoomService.getDatabase().getCookieDao().insert(netCookie);

            view.loadUrl("https://m.bilibili.com/space/");
            return;
        }

        if(url.equals("https://passport.bilibili.com/login?act=exit")){
            /**退出登录**/
            DataRoomService.getDatabase().getCookieDao().deleteAll();
            view.loadUrl("https://passport.bilibili.com/login");
        }
    }
}
