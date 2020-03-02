package com.jil.paintf.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.activity.DocDetailActivity.Companion.startDocDetailActivity
import com.jil.paintf.activity.UserActivity
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.DocX
import com.jil.paintf.repository.Item
import com.jil.paintf.repository.Result

class SearchItemAdapter(private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data=arrayListOf<Result>()
    var status ="输入关键词搜索"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_TYPE.ITEM_TYPE_DATA.ordinal->ItemVHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.item_user_doc_list,
                    parent,
                    false
                )
            )
            else->BottomViewHolder(LayoutInflater.from(mContext).inflate(
                android.R.layout.simple_list_item_1,
                parent,
                false
            ))
        }
    }

    //底部 ViewHolder
    class BottomViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var textView:TextView?=null
        init {
            textView=itemView.findViewById(android.R.id.text1)
            textView!!.gravity=TextView.TEXT_ALIGNMENT_CENTER
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemVHolder) {
            holder.imageUrl=data[position].cover + "@512w_384h_1e.webp"
            holder.displayImage()
            if(data[position].title =="")
                holder.title.text = "无题"
            else{
                var str1=data[position].title.replace("<em class=\"keyword\">","<font color=\""+ ThemeUtil.getColorAccent(mContext)+"\">" )
                var str2 =str1.replace("</em>","</font>" )

//                "<font color=\"#1586C6\">" + this.to + "</font>";
                holder.title.text = Html.fromHtml(str2)
            }

            if(data[position].count==1){
                holder.count.visibility=View.GONE
            }else{
                holder.count.visibility=View.VISIBLE
                holder.count.text = data[position].count.toString()
            }

            holder.image.setOnClickListener { v ->
                val intArray =IntArray(data.size)
                for (index in 0 until data.size){
                    intArray[index] =data[index].id
                }
                startDocDetailActivity(v.context,intArray,data[position].id)
            }
        }

        if(holder is BottomViewHolder){
            holder.textView!!.text=status
        }
    }

    override fun getItemCount(): Int {
        return data.size+1
    }

    //判断当前item是否是FooterView
    fun  isBottomView(position:Int):Boolean {
        return position >= data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            data.size->ITEM_TYPE.ITEM_TYPE_LOAD.ordinal
            else -> {
                ITEM_TYPE.ITEM_TYPE_DATA.ordinal
            }
        }
    }

    enum class ITEM_TYPE {
        ITEM_TYPE_DATA,
        ITEM_TYPE_LOAD,
    }

    private class ItemVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageUrl:String?=null
        var image: ImageView = itemView.findViewById(R.id.imageView)
        var title: TextView = itemView.findViewById(R.id.textView)
        var count: TextView = itemView.findViewById(R.id.textView2)


        fun displayImage(){
            imageUrl=imageUrl.let {
                Glide.with(itemView.context).load(it).placeholder(R.drawable.empty_hint).into(image)
                null
            }
        }
    }

}
