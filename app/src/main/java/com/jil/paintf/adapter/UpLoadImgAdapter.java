package com.jil.paintf.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jil.paintf.R;

import java.util.ArrayList;
import java.util.List;

public class UpLoadImgAdapter extends RecyclerView.Adapter {
    List<String> imageUrl =new ArrayList<>();
    Context mContext;
    public final static int UPLOAD_CODE = 1521;
    public UpLoadImgAdapter(Context context) {
        this.mContext=context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            return new AddItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout_upload,null));
        }else {
            return new ImageItemViewHolder(new ImageView(mContext));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AddItemViewHolder){
            Glide.with(mContext).load(R.drawable.ic_add_green_24dp).into(((AddItemViewHolder) holder).imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, UPLOAD_CODE,null);
                }
            });
        }else {
            Glide.with(mContext).load(imageUrl.get(position)).into((ImageView) holder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrl.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==imageUrl.size()){
            return 1;
        }else {
            return 0;
        }

    }

    static class AddItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView =itemView.findViewById(R.id.image);

        public AddItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ImageItemViewHolder extends RecyclerView.ViewHolder{

        public ImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public int addImage(String url){
        imageUrl.add(url);
        notifyItemInserted(imageUrl.size());
        return 0;
    }
}
