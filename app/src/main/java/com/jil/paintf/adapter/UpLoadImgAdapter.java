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
    HashMap<String,String> map=new HashMap<>();

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
            Glide.with(mContext).load(images.get(position).getImage_url()+"@512w_384h_1e.webp").into((ImageView) holder.itemView);
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

    public Map<String,String> getMap(){
        return map;
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
        int position =images.size();
        images.add(data);
        notifyItemInserted(images.size());

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



        return 0;
    }
}
