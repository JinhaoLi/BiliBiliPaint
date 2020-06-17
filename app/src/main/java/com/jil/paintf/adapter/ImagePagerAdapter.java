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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jil.paintf.R;
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
        switch(AppPaintF.getLoadLevel()){
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
    public Object instantiateItem(final ViewGroup container, final int position) {
        if(position==ts.size()){
            final View v =LayoutInflater.from(container.getContext()).inflate(R.layout.item_viewpager_end,container,false);
            TextView up =v.findViewById(R.id.textView18);
            TextView next =v.findViewById(R.id.textView19);
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upAndNextPagerCallBack.up(v);
                    v.animate().translationY(v.getHeight()).setInterpolator(new AccelerateInterpolator()).start();
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upAndNextPagerCallBack.next(view);
                    v.animate().translationX(-v.getWidth()).setInterpolator(new AccelerateInterpolator()).start();
                }
            });
            container.addView(v);
            return v;
        }

        final View v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_doc_illusts_layout,container,false);
        final SubsamplingScaleImageView imageView =v.findViewById(R.id.imageView);
        RoundedCorners roundedCorners= new RoundedCorners(10);
        final RequestOptions requestOptions =RequestOptions.bitmapTransform(roundedCorners);

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
        if(!loadLevel.equals(""))
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("加载原图").setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Glide.with(v.getContext()).asFile().load(ts.get(position)).apply(requestOptions)
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
                        dialog.dismiss();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setMessage("临时加载此图片的原图！").create().show();
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
                if(loadLevel.equals("")){
                    try {
                        downLoadPic((String) ts.get(position),pic);
                        Toast.makeText(context,"已保存！",Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context,"保存失败！",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    downLoadPic((String) ts.get(position),context);
                }

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

    private void downLoadPic(final String urlStr, Context context){
        final ProgressDialog pd1 = new ProgressDialog(context);

        Observer<Bundle> observer =new Observer<Bundle>() {
            //单位 kb
            long picSize= 0L;
            @Override
            public void onSubscribe(Disposable d) {

                //依次设置标题,内容,是否用取消按钮关闭,是否显示进度
                pd1.setTitle("图片下载中");
                pd1.setMessage("图片正在下载中,请稍后...");
                pd1.setCancelable(true);
                //这里是设置进度条的风格,HORIZONTAL是水平进度条,SPINNER是圆形进度条
                pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                //调用show()方法将ProgressDialog显示出来
                pd1.show();
            }

            @Override
            public void onNext(Bundle bundle) {
                if(picSize==0)
                    picSize=bundle.getLong("pic_size");
                long isLoad =bundle.getLong("loaded");
                int percentage= (int) ((isLoad*100)/picSize);
                pd1.setProgress(percentage);
                pd1.setMessage(picSize+"/"+isLoad+"Kb");

            }

            @Override
            public void onError(Throwable e) {
//                Logger.d(e.getMessage());
                pd1.setTitle("错误");
                pd1.setProgress(100);
                pd1.setMessage(e.getMessage());
            }

            @Override
            public void onComplete() {
                pd1.setTitle("下载成功");
                pd1.setProgress(100);
                pd1.setMessage("原图已下载(共"+picSize+"Kb)");
                //pd1.dismiss();
            }
        };

        Observable<Bundle> download =Observable.create(new ObservableOnSubscribe<Bundle>() {
            @Override
            public void subscribe(final ObservableEmitter<Bundle> emitter) throws Exception {
                URL url=null;
                try {
                    url =new URL(urlStr);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if(url==null){
                    return;
                }
                File f = new File(AppPaintF.Companion.getSave_dir_path());
                if(!f.exists())
                    f.mkdir();
                String[] strs=urlStr.split("/");
                final File pic =new File(f,strs[strs.length-1]);
                if(pic.exists()){
                    emitter.onError(new Throwable("图片已存在！"));
                    return;
                }
                final OkHttpClient.Builder builder =new OkHttpClient.Builder();
                Request.Builder rq =new Request.Builder().url(urlStr);
                Call call =builder.build().newCall(rq.build());
                call.enqueue(new Callback() {
                    Bundle bundle=new Bundle();
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
                            bundle.putLong("pic_size",response.body().contentLength()/1024);
                            bundle.putLong("loaded",0);
                            emitter.onNext(bundle);
                            while ((len=inputStream.read(bytes))!=-1){
                                all_size+=len;
                                fileOutputStream.write(bytes,0,len);
                                fileOutputStream.flush();
                                bundle.putLong("loaded",all_size/1024);
                                emitter.onNext(bundle);
                            }
                            inputStream.close();
                            fileOutputStream.close();
                            emitter.onComplete();
                        }else {
//                            Logger.d(response);
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
