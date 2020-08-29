package com.jil.paintf.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jil.paintf.R;
import com.jil.paintf.custom.GlideApp;
import com.jil.paintf.custom.ProgressListener;
import com.jil.paintf.custom.ShowProgressImageView;
import com.jil.paintf.network.ProgressInterceptor;
import com.jil.paintf.service.AppPaintF;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter<T> extends PagerAdapter {
    public List<T> ts;
    private HideLayoutCallBack listener;
    File pic;

    public void setListener(HideLayoutCallBack listener) {
        this.listener = listener;
    }

    public ImagePagerAdapter(List<T> ts) {
        this.ts = ts;
    }

    @Override
    public int getCount() {
        return ts.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        final View v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_doc_illusts_layout,container,false);
        final ShowProgressImageView imageView =v.findViewById(R.id.imageView);
        RoundedCorners roundedCorners= new RoundedCorners(10);
        final RequestOptions requestOptions =RequestOptions.bitmapTransform(roundedCorners);
        final String path =ts.get(position).toString();

        GlideApp.with(container.getContext()).asFile().load(ts.get(position)).apply(requestOptions)
                .into(new CustomTarget<File>() {

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        imageView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        imageView.setProgress(100);
                        ProgressInterceptor.removeListener(path);
                        pic = resource;
                    }

                    @Override
                    public void onStart() {
                        ProgressInterceptor.addListener(path, new ProgressListener() {
                            @Override
                            public void onProgress(int progress) {
                                imageView.setProgress(progress);
                            }
                        });
                        super.onStart();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        ProgressInterceptor.removeListener(path);
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                        ProgressInterceptor.removeListener(path);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        ProgressInterceptor.removeListener(path);
                    }
                });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                download(position,v.getContext());
                return false;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.hideLayout(v);
            }
        });
        container.addView(v);
        return v;
    }

    public void download(final int position, final Context context){
        new AlertDialog.Builder(context).setTitle("下载图片?").setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    downLoadPic((String) ts.get(position),pic);
                    Toast.makeText(context,"已保存！",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context,"保存失败！",Toast.LENGTH_SHORT).show();
                }
            }
        }).setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).create().show();

    }

    private void downLoadPic(final String urlStr,File into) throws IOException {
       File f = new File(AppPaintF.Companion.getSave_dir_path());
        if(!f.exists())
            if(!f.mkdir()){
               throw new FileNotFoundException("创建文件夹失败");
            }
        String[] strs=urlStr.split("/");
        final File pic =new File(f,strs[strs.length-1]);
        FileInputStream fi =new FileInputStream(into);
        FileOutputStream fo =new FileOutputStream(pic);
        FileChannel fic =fi.getChannel();
        FileChannel foc =fo.getChannel();
        foc.transferFrom(fic,0,fic.size());
        fo.close();
        fi.close();
        fic.close();
        foc.close();

    }

    public interface HideLayoutCallBack {
       void hideLayout(View view);
    }

    public interface UpAndNextPagerCallBack{
        void up(View view);
        void next(View view);
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return POSITION_NONE;
    }

    public int findPositionByName(T t){
        for(int i = 0; i< ts.size(); i++){
            if(ts.get(i)==t){
                return i;
            }
        }
        return 1;
    }

    public void remove(int position){
        ts.remove(position);
        notifyDataSetChanged();
    }
}
