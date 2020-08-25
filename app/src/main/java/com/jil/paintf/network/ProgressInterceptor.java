package com.jil.paintf.network;

import com.jil.paintf.custom.ProgressListener;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 2020/8/25 15:03
 *
 * @author JIL
 **/
public class ProgressInterceptor implements Interceptor {
    public static final Map<String, ProgressListener> LISTENER_MAP = new HashMap<>();

    public static void addListener(String url,ProgressListener listener){
        LISTENER_MAP.put(url,listener);
    }

    public static void removeListener(String url){
        LISTENER_MAP.remove(url);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request =chain.request();
        Response response =chain.proceed(request);
        String url =request.url().toString();
        return response.newBuilder().body(new ProgressResponseBody(url,response.body())).build();
    }
}
