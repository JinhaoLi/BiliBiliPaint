package com.jil.paintf.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jil.paintf.R;
import com.jil.paintf.service.AppPaintf;
import com.jil.paintf.viewmodel.DocViewModel;
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
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class ImagePagerAdapter<T> extends PagerAdapter {
    public ArrayList<T> ts;
    private HideLayoutCallBack listener;
    private UpAndNextPagerCallBack upAndNextPagerCallBack;
    private String loadLevel;
    File pic;


    public void setUpAndNextPagerCallBack(UpAndNextPagerCallBack upAndNextPagerCallBack) {
        this.upAndNextPagerCallBack = upAndNextPagerCallBack;
    }

    public void setListener(HideLayoutCallBack listener) {
        this.listener = listener;
    }


    public ImagePagerAdapter(ArrayList<T> ts) {
        this.ts = ts;
        switch(AppPaintf.getLoadLevel()){
            case 1080:
                loadLevel="@1080w_1e.webp";
                break;
            case 5000:
                loadLevel="";
                break;
            default:
                loadLevel="@720w_1e.webp";
                break;
        }
    }

    @Override
    public int getCount() {
        return ts.size()+1;
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
        if(position==ts.size()){
            View v =LayoutInflater.from(container.getContext()).inflate(R.layout.item_viewpager_end,container,false);
            TextView up =v.findViewById(R.id.textView18);
            TextView next =v.findViewById(R.id.textView19);
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upAndNextPagerCallBack.up(v);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    upAndNextPagerCallBack.next(v);
                }
            });
            container.addView(v);
            return v;
        }

        final View v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_doc_illusts_layout,container,false);
        final SubsamplingScaleImageView imageView =v.findViewById(R.id.imageView);
        RoundedCorners roundedCorners= new RoundedCorners(10);
        RequestOptions requestOptions =RequestOptions.bitmapTransform(roundedCorners);

        Glide.with(container.getContext()).asFile().load(ts.get(position)+loadLevel).apply(requestOptions)
                .into(new CustomTarget<File>() {

                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        imageView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        pic = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

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
        new AlertDialog.Builder(context).setTitle("下载图片?").setNegativeButton("保存预览", new DialogInterface.OnClickListener() {
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
        }).setPositiveButton("保存原图", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadPic((String) ts.get(position),context);
            }
        }).create().show();

    }

    private void downLoadPic(final String urlStr,File into) throws IOException {
       File f = new File(Environment.getExternalStorageDirectory(),"Pictures");
        if(!f.exists())
            f.mkdir();
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

    private void downLoadPic(final String urlStr, Context context){
        final ProgressDialog pd1 = new ProgressDialog(context);

        Observer<Long> observer =new Observer<Long>() {
            long picSize;
            @Override
            public void onSubscribe(Disposable d) {

                //依次设置标题,内容,是否用取消按钮关闭,是否显示进度
                pd1.setTitle("图片下载中");
                pd1.setMessage("图片正在下载中,请稍后...");
                pd1.setCancelable(true);
                //这里是设置进度条的风格,HORIZONTAL是水平进度条,SPINNER是圆形进度条
                pd1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd1.setIndeterminate(true);
                //调用show()方法将ProgressDialog显示出来
                pd1.show();
            }

            @Override
            public void onNext(Long l) {
                pd1.setMessage("图片正在下载中,请稍后..."+l/1024+"Kb");
                picSize=l;
            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e.getMessage());
            }

            @Override
            public void onComplete() {
                pd1.setTitle("下载成功");
                pd1.setProgress(100);
                pd1.setMessage("图片原图已下载("+picSize/1024+"Kb)");
                //pd1.dismiss();
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

    public interface HideLayoutCallBack {
       void hideLayout(View view);
    }

    public interface UpAndNextPagerCallBack{
        void up(View view);
        void next(View view);
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
