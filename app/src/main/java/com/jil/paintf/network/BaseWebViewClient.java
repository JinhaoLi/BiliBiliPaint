package com.jil.paintf.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
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
    private StateChange stateChange;

    public void setStateChange(StateChange stateChange) {
        this.stateChange = stateChange;
    }

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
        Log.i("onPageStarted->",url);

        if(AppPaintF.instance.getCsrf() != null){
            CookieManager cookieManager =CookieManager.getInstance();
            cookieManager.setCookie(url, AppPaintF.instance.getCookie().toString());
        }

    }

    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
        Log.i("onPageCommitVisible->",url);
        if(url.equals("https://m.bilibili.com/index.html")||url.equals("https://m.bilibili.com/")){
            /** 登录完成**/
            view.setVisibility(View.INVISIBLE);
            CookieManager cookieManager =CookieManager.getInstance();
            String cookieStr =cookieManager.getCookie(url);
            NetCookie netCookie= new NetCookie(cookieStr);
            AppPaintF.instance.setCsrf(netCookie.bili_jct);
            AppPaintF.instance.setLoginId(netCookie.DedeUserID);
            DataRoomService.getDatabase().getCookieDao().insert(netCookie);
            view.loadUrl("https://m.bilibili.com/space/");
            if(stateChange!=null)
                stateChange.isLogin();
            return;
        }
        if(url.startsWith("https://m.bilibili.com/space/")){
            Toast.makeText(view.getContext(), "可以点击->编辑资料->退出登录", Toast.LENGTH_SHORT).show();
        }
        if(url.equals("https://passport.bilibili.com/login?act=exit")){
            /**退出登录**/
            DataRoomService.getDatabase().getCookieDao().deleteAll();
            AppPaintF.instance.setCsrf(null);
            AppPaintF.instance.setLoginId(0);
            view.loadUrl("https://passport.bilibili.com/login");
            if(stateChange!=null)
                stateChange.loginExit();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        Log.i("onPageFinished->",url);
    }

    public interface StateChange{
        void loginExit();
        void isLogin();
    }
}
