package com.jil.paintf.custom;

import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.jil.paintf.network.ProgressInterceptor;
import okhttp3.OkHttpClient;

import java.io.InputStream;

/**
 * 2020/8/25 15:01
 *
 * @author JIL
 **/
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        OkHttpClient.Builder builder =new OkHttpClient.Builder();
        builder.addInterceptor(new ProgressInterceptor());
        OkHttpClient okHttpClient = builder.build();

        //原来的是  new OkHttpUrlLoader.Factory()；
        registry.replace(GlideUrl.class, InputStream.class,new OkHttpUrlLoader.Factory(okHttpClient));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
