package com.jil.paintf.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jil.paintf.R;
import com.jil.paintf.repository.DataXX;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpLoadImgAdapter extends RecyclerView.Adapter {
    List<DataXX> images =new ArrayList<>();
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
            return new ImageItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout_upload,null));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof AddItemViewHolder){
            AddItemViewHolder addItemViewHolder= (AddItemViewHolder) holder;
            addItemViewHolder.delete.setVisibility(View.GONE);
            addItemViewHolder.imageView.setBackgroundColor(Color.GRAY);
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

            ImageItemViewHolder imageItemViewHolder = (ImageItemViewHolder) holder;
            imageItemViewHolder.delete.setVisibility(View.VISIBLE);
            imageItemViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeImage(position);
                }
            });
            Glide.with(mContext).load(images.get(position).getImage_url()+"@512w_384h_1e.webp").into(imageItemViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return images.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position== images.size()){
            return 1;
        }else {
            return 0;
        }

    }

    static class AddItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView =itemView.findViewById(R.id.image);
        Button delete =itemView.findViewById(R.id.button4);

        public AddItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ImageItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView =itemView.findViewById(R.id.image);
        Button delete =itemView.findViewById(R.id.button4);
        public ImageItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * pictures[0][img_src]: http://i0.hdslb.com/bfs/album/6da689102c1fac93aed220b2f7355e9345180443.jpg
     * pictures[0][img_width]: 800
     * pictures[0][img_height]: 1174
     * pictures[0][img_size]: 271.1640625
     * pictures[0][id]: 135564716471984830
     * @param data
     * @return
     */
    public int addImage(DataXX data){
        images.add(data);
        notifyItemInserted(images.size());
        return 0;
    }

    public Map<String,String> buildMap(){
        HashMap<String,String> map=new HashMap<>();
        for(int position =0;position<images.size();position++){
            String key ="pictures["+position+"][img_src]";
            map.put(key,images.get(position).getImage_url());

            String key1 ="pictures["+position+"][img_width]";
            map.put(key1,images.get(position).getImage_width()+"");

            String key2 ="pictures["+position+"][img_height]";
            map.put(key2,images.get(position).getImage_height()+"");

            String key3 ="pictures["+position+"][img_size]";
            map.put(key3,271.24929528+"");

            String key4 ="pictures["+position+"][id]";
            map.put(key4,(long)(Math.random()*Long.MAX_VALUE)+"");
        }
        return map;
    }

    public int removeImage(int position){
        images.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,images.size());
        return 0;
    }
}
