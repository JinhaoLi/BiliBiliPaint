package com.jil.paintf.adapter;

import android.annotation.SuppressLint;
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
        Glide.with(container.getContext()).load(ts.get(position)).apply(requestOptions)
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
