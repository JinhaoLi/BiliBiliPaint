package com.jil.paintf.adapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.activity.PreViewActivity
import com.jil.paintf.custom.GlideApp
import com.jil.paintf.repository.RankItem
import com.jil.paintf.service.AppPaintF

/**
 * 2020/8/29 23:40
 * @author JIL
 **/
class RankListAdapter(val data:List<RankItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE.BEFORE.ordinal->{
                BeforeItem(LayoutInflater.from(parent.context).inflate(R.layout.item_rank_header,parent,false))
            }
            else->{
                NormalItem(LayoutInflater.from(parent.context).inflate(R.layout.item_rank_normal,parent,false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val rank_item =data[position]
        if(holder is BeforeItem){
            GlideApp.with(holder.itemView.context).load(rank_item.item.pictures[0].img_src+"@512w_512h.webp").into(holder.previewImage)
            holder.rankText.text=(position+1).toString()
            holder.titleText.text =rank_item.item.title
            holder.artText.text =rank_item.user.name
            holder.seeText.text=rank_item.item.view_count.toString()
            holder.collectionText.text =rank_item.item.collect_count.toString()
        }
        if( holder is NormalItem){
            GlideApp.with(holder.itemView.context).load(rank_item.item.pictures[0].img_src+"@256w_256h.webp").into(holder.previewImage)
            holder.rankText.text=(position+1).toString()
            holder.titleText.text =rank_item.item.title
            holder.artText.text =rank_item.user.name
            holder.seeText.text=rank_item.item.view_count.toString()
            holder.collectionText.text =rank_item.item.collect_count.toString()
        }
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(it.context, PreViewActivity::class.java)
            val intArray =IntArray(data.size)
            for (index in 0 until data.size){
                intArray[index] =data[index].item.doc_id
            }
            bundle.putInt("doc_id",data[position].item.doc_id)
            bundle.putIntArray("intArray",intArray)
            intent.putExtra("param1",bundle)
            if (AppPaintF.instance.enableAnimator){
                val options1 = ActivityOptionsCompat.makeSceneTransitionAnimation(it.context as Activity,it,"mainimage")
                ActivityCompat.startActivity(it.context, intent, options1.toBundle())
            }else{
                PreViewActivity.startDocDetailActivity(it.context,intArray,data[position].item.doc_id)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0,1,2->{
                VIEW_TYPE.BEFORE.ordinal
            }
            else->{
                VIEW_TYPE.NORMAL.ordinal
            }
        }
    }

    enum class VIEW_TYPE{
        BEFORE,NORMAL
    }

    class BeforeItem(itemView: View) : RecyclerView.ViewHolder(itemView){
        val previewImage =itemView.findViewById<ImageView>(R.id.imageView14)
        val rankText =itemView.findViewById<TextView>(R.id.textView13)
        val titleText =itemView.findViewById<TextView>(R.id.textView33)
        val artText =itemView.findViewById<TextView>(R.id.textView34)
        val seeText =itemView.findViewById<TextView>(R.id.textView35)
        val collectionText =itemView.findViewById<TextView>(R.id.textView36)
    }

    class NormalItem(itemView: View) : RecyclerView.ViewHolder(itemView){
        val previewImage =itemView.findViewById<ImageView>(R.id.imageView14)
        val rankText =itemView.findViewById<TextView>(R.id.textView13)
        val titleText =itemView.findViewById<TextView>(R.id.textView33)
        val artText =itemView.findViewById<TextView>(R.id.textView34)
        val seeText =itemView.findViewById<TextView>(R.id.textView35)
        val collectionText =itemView.findViewById<TextView>(R.id.textView36)
    }
}