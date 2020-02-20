package com.jil.paintf.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jil.paintf.R
import com.jil.paintf.activity.DocDetailActivity.Companion.startDocDetailActivity
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.repository.Item
import java.util.*

class ItemAdapter(
    var data: ArrayList<Item>,
    private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemVHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.doc_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemVHolder) {
            Glide.with(mContext).load(data[position].item.pictures[0].img_src + "@512w_384h_1e.webp")
                .into(holder.image)
            Glide.with(mContext).load(data[position].user.head_url+"@32w_32h.webp")
                .transform(GlideCircleWithBorder(2, 0xFFD81B60.toInt()))
                .into(holder.ico)
            holder.title.text = data[position].item.title
            if(data[position].item.pictures.size==1){
                holder.count.visibility=View.GONE
            }else{
                holder.count.visibility=View.VISIBLE
                holder.count.text = data[position].item.pictures.size.toString()
            }

            holder.itemView.setOnClickListener { v ->
                val intArray =IntArray(data.size)
                for (index in 0 until data.size){
                    intArray[index] =data[index].item.doc_id
                }
                startDocDetailActivity(v.context,intArray,data[position].item.doc_id)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private class ItemVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ico :ImageView = itemView.findViewById(R.id.imageView2)
        var image: ImageView = itemView.findViewById(R.id.imageView)
        var title: TextView = itemView.findViewById(R.id.textView)
        var count: TextView = itemView.findViewById(R.id.textView2)

    }

}