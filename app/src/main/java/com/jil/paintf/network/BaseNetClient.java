package com.jil.paintf.network;

import android.os.Build;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jil.paintf.service.AppPaintF;
import com.orhanobut.logger.Logger;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseNetClient {

    private static OkHttpClient okHttpClient;
    private Gson gson =new GsonBuilder().create();

    public BaseNetClient() {
        if(okHttpClient==null){

            HttpLoggingInterceptor.Logger logger=new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String s) {
                    Log.d("jil", s);
                }
            };
            HttpLoggingInterceptor loggingInterceptor =new HttpLoggingInterceptor(logger);

//            if(AppPaintF.isDebug()){
                loggingInterceptor.level(HttpLoggingInterceptor.Level.BASIC);
//            }else {
//                loggingInterceptor.level(HttpLoggingInterceptor.Level.NONE);
//            }

            OkHttpClient.Builder builder =new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    /**@changeRequest(Chain)**/
                    if(AppPaintF.instance.getCookie()==null)
                        return chain.proceed(chain.request());
                    else
                        return chain.proceed(chain.request().newBuilder()
                                .addHeader("Sec-Fetch-Site","same-site")
                        .addHeader("Cookie", AppPaintF.instance.getCookie().toString())
                                .build());
                }
            }).addInterceptor(loggingInterceptor);
            okHttpClient = builder.build();
        }

    }

    /**
     * 修改Request
     * @param chain
     * @return
     * @throws IOException
     */
    private Response changeRequest(Interceptor.Chain chain) throws IOException {
        Request request =chain.request();
        int radom = (int) (10000000+(Math.random()*99999999));
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String code =dateFormat.format(new Date());
        String ra =encode(code);
        String cookie="";
        if(!ra.equals(""))
            cookie = AppPaintF.instance.getCookie().toString();
        Request newRequest =request.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36")
                .addHeader("Accept"," application/json, text/plain, */*")
                .addHeader("Accept-Encoding"," gzip, deflate, br")
                .addHeader("Referer"," https://h.bilibili.com/")
                .addHeader("Origin"," https://h.bilibili.com")
                .addHeader("Cookie",cookie)
                .addHeader("Host","api.vc.bilibili.com")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site"," same-site").build();

        return chain.proceed(newRequest);
    }


    private String encode(String text){
        try {
            MessageDigest instance= MessageDigest.getInstance("MD5");
            byte[] digest= instance.digest(text.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i= b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0"+hexString;
                }
                sb.append(hexString);
            }
            return sb.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *
     * https://account.bilibili.com
     * @return
     */
    public Retrofit getAccountBiliClient(){
        return new Retrofit.Builder()
                .baseUrl("https://account.bilibili.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * https://api.vc.bilibili.com
     * @return
     */
    public Retrofit getApiVcBiliClient(){
        return new Retrofit.Builder()
                .baseUrl("https://api.vc.bilibili.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * https://api.bilibili.com
     */
    public Retrofit getApiBiliClient(){
        return new Retrofit.Builder()
                .baseUrl("https://api.bilibili.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
