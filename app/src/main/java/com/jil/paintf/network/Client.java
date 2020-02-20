package com.jil.paintf.network;

import android.util.Log;
import com.bumptech.glide.RequestBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.Random;

public class Client {

    private static OkHttpClient okHttpClient;
    private Gson gson =new GsonBuilder().create();

    public Client() {
        if(okHttpClient==null){

            HttpLoggingInterceptor.Logger logger=new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String s) {
                    Log.d("Client", s);
                }
            };
            HttpLoggingInterceptor loggingInterceptor =new HttpLoggingInterceptor(logger);
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder =new OkHttpClient.Builder().addNetworkInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request request =chain.request();
                    int radom = (int) (10000000+(Math.random()*99999999));
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String code =dateFormat.format(new Date());
                    String ra =encode(code);
                    String cookie="";
                    if(!ra.equals(""))
                    cookie ="l=v; _uuid="+ra.substring(0,8)+"-"+ra.substring(9,13)+"-"+ra.substring(14,18)+"-FCA6-"+ra.substring(19,28)+radom+"infoc;" +
                            "buvid3="+ra.substring(5,13)+"-"+ra.substring(20,24)+"-401B-B81B-"+ra.substring(11,19)+"infoc;LIVE_BUVID=AUTO85156745"+radom/2+";";
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
            }).addInterceptor(loggingInterceptor);
            okHttpClient = builder.build();
        }

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

    public Retrofit getRetrofitAppApi(){

        return new Retrofit.Builder()
                .baseUrl("https://api.vc.bilibili.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
