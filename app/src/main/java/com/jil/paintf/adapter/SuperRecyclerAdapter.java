package com.jil.paintf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jil.paintf.custom.GlideCircleWithBorder;
import com.jil.paintf.custom.ThemeUtil;

import java.util.ArrayList;

public abstract class SuperRecyclerAdapter<T> extends RecyclerView.Adapter<SuperRecyclerAdapter.SuperVHolder> {
    ArrayList<T> data;

    public ArrayList<T> getData() {
        return data;
    }

    public SuperRecyclerAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public SuperVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SuperVHolder(LayoutInflater.from(parent.getContext()).inflate(setLayout(viewType),parent,false));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull SuperVHolder holder, int position) {
        bindData(holder,position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public  static class SuperVHolder extends RecyclerView.ViewHolder {
        public SuperVHolder(@NonNull View itemView) {
            super(itemView);
        }

        public View getView(int id){
            return itemView.findViewById(id);
        }

        public void setText(String str,int id){
            TextView textView = (TextView) getView(id);
            textView.setText(str);
        }

        public void setImageIco(String str, int id){
            ImageView imageView = (ImageView) getView(id);
            Glide.with(imageView.getContext()).load(str)
                    .transform(new GlideCircleWithBorder(2, ThemeUtil.getColorAccent(imageView.getContext())))
                    .into(imageView);
        }

        public void setImage(String str,int id){
            ImageView imageView = (ImageView) getView(id);
            Glide.with(imageView.getContext()).load(str)
                    .into(imageView);
        }
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    public abstract void bindData(@NonNull SuperVHolder holder, int position);

    public abstract int setLayout(int viewType);
}
