package com.jil.paintf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jil.paintf.R;

import java.util.ArrayList;
import java.util.List;

public class AddTagAdapter extends RecyclerView.Adapter<AddTagAdapter.TagItemVH> {
    List<TagItem> list =new ArrayList<>();

    @NonNull
    @Override
    public TagItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_tag_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull final TagItemVH holder, final int position) {
        if(position!=list.size()){
            holder.editText.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.imageView2.setVisibility(View.VISIBLE);
            holder.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(list.get(position).name);
        }else {
            holder.textView.setVisibility(View.GONE);
            holder.imageView2.setVisibility(View.GONE);
            holder.editText.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str =holder.editText.getText().toString();
                    if(!str.equals("")){
                        holder.editText.setText("");
                        list.add(new TagItem(0,str));
                        notifyDataSetChanged();
                        return;
                    }
                    holder.editText.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    public static class TagItemVH extends RecyclerView.ViewHolder{
        EditText editText =itemView.findViewById(R.id.editText);
        TextView textView =itemView.findViewById(R.id.textView);
        ImageView imageView =itemView.findViewById(R.id.imageView);
        ImageView imageView2 =itemView.findViewById(R.id.imageView2);
        public TagItemVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class TagItem{

        public TagItem(int state, String name) {
            this.state = state;
            this.name = name;
        }

        int state=0;
        String name ="";
    }
}
