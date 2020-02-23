package com.jil.paintf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class SuperRecyclerAdapter<T> extends RecyclerView.Adapter<SuperRecyclerAdapter.SuperVHolder> {
    ArrayList<T> data;

    public SuperRecyclerAdapter(ArrayList<T> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public SuperVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SuperVHolder(LayoutInflater.from(parent.getContext()).inflate(setLayout(),parent,false));
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

        private View getView(int id){
            return itemView.findViewById(id);
        }

        public void setText(String str,int id){
            TextView textView = (TextView) getView(id);
            textView.setText(str);
        }
    }

    abstract void bindData(@NonNull SuperVHolder holder, int position);

    abstract int setLayout();
}
