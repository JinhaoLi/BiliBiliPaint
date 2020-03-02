package com.jil.paintf.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jil.paintf.R;
import com.jil.paintf.custom.OnScaleListener;
import com.jil.paintf.service.AppPaintf;
import com.orhanobut.logger.Logger;
import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ImagePagerAdapter<T> extends PagerAdapter {
    public ArrayList<T> ts;
    private String path;
    public int width=720;
    public int height=1280;
    private imageClickListener listener;

    public void setListener(imageClickListener listener) {
        this.listener = listener;
    }

    public ImagePagerAdapter(String path) {
        ts =new ArrayList<>();
    }

    public ImagePagerAdapter(ArrayList<T> ts) {
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
    public Object instantiateItem(ViewGroup container, final int position) {
        View v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_doc_illusts_layout,container,false);
        ImageView imageView =v.findViewById(R.id.imageView);
        RoundedCorners roundedCorners= new RoundedCorners(10);
        RequestOptions requestOptions =RequestOptions.bitmapTransform(roundedCorners)
                .override(width,height);
        Glide.with(container.getContext()).load(ts.get(position)+"@1080w_1e.webp").apply(requestOptions)
                .into(imageView);
        imageView.setOnTouchListener(new OnScaleListener(new OnScaleListener.OnScalceCallBack() {
            @Override
            public void onClick(View view) {
                if(listener!=null)
                listener.onClick(view);
            }

        }));
        container.addView(v);
        if(width!=720){
            width=720;
            height=1280;
        }
        return v;
    }

    public void download(final int position, final Context context){
        new AlertDialog.Builder(context).setTitle("下载图片?").setNegativeButton("下载这张", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadPic((String) ts.get(position),context);
            }
        }).setPositiveButton("下载全部", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();

    }

    private void downLoadPic(final String urlStr, Context context){
        final ProgressDialog pd1 = new ProgressDialog(context);
        Observer<Long> observer =new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

                //依次设置标题,内容,是否用取消按钮关闭,是否显示进度
                pd1.setTitle("图片下载中");
                pd1.setMessage("图片正在下载中,请稍后...");
                pd1.setCancelable(true);
                //这里是设置进度条的风格,HORIZONTAL是水平进度条,SPINNER是圆形进度条
                pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd1.setIndeterminate(true);
                //调用show()方法将ProgressDialog显示出来
                pd1.show();
            }

            @Override
            public void onNext(Long l) {
                pd1.setMessage("图片正在下载中,请稍后..."+l/1024);
            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e.getMessage());
            }

            @Override
            public void onComplete() {
                pd1.dismiss();
            }
        };

        Observable<Long> download =Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(final ObservableEmitter<Long> emitter) throws Exception {
                URL url=null;
                try {
                    url =new URL(urlStr);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if(url==null){
                    return;
                }
                File f = new File(Environment.getExternalStorageDirectory(),"Pictures");
                if(!f.exists())
                    f.mkdir();
                String[] strs=urlStr.split("/");
                final File pic =new File(f,strs[strs.length-1]);
                OkHttpClient.Builder builder =new OkHttpClient.Builder();
                Request.Builder rq =new Request.Builder().url(urlStr);
                Call call =builder.build().newCall(rq.build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.code()==200){
                            InputStream inputStream=response.body().byteStream();
                            FileOutputStream fileOutputStream =new FileOutputStream(pic);
                            byte[] bytes =new byte[1024];
                            int len=0;
                            long all_size=0;
                            while ((len=inputStream.read(bytes))!=-1){
                                all_size+=len;
                                fileOutputStream.write(bytes,0,len);
                                fileOutputStream.flush();
                                emitter.onNext(all_size);
                            }
                            inputStream.close();
                            fileOutputStream.close();
                            emitter.onComplete();
                        }else {
                            Logger.d(response);
                        }

                    }
                });


            }
        });

        download.subscribeOn( Schedulers.io()).observeOn( AndroidSchedulers.mainThread()).subscribe(observer);





    }

    public interface imageClickListener {
       void onClick(View view);
    }

    @Override
    public int getItemPosition(Object object) {
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
