package com.jil.paintf.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jil.paintf.R
import com.jil.paintf.activity.PreViewActivity
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.Result

class SearchItemAdapter(private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data=arrayListOf<Result>()
    var status ="输入关键词搜索"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_TYPE.ITEM_TYPE_DATA.ordinal->ItemVHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.item_doc_list_no_header_ico,
                    parent,
                    false
                )
            )
            else->BottomViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_recycleview_loading,
                parent,
                false
            ))
        }
    }

    //底部 ViewHolder
    //底部 ViewHolder
    class BottomViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val textView:TextView=itemView.findViewById(R.id.textView23)
        val progressBar: ProgressBar =itemView.findViewById(R.id.progressBar)
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
                PreViewActivity.startDocDetailActivity(v.context,intArray,data[position].id)
            }
        }

        if(holder is BottomViewHolder){
            holder.textView.text=status
            if(status == "正在加载..."){
                holder.progressBar.visibility=View.VISIBLE
            }else{
                holder.progressBar.visibility=View.GONE
            }
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
            image.visibility=View.INVISIBLE
            Glide.with(itemView.context).asBitmap().load(imageUrl)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        image.setImageBitmap(resource)
                        image.visibility=View.VISIBLE
                        alpView(image)
                        val builder = Palette.from(resource);
                        builder.generate {
                            //亮、柔和
                            val lightMutedColor: Int = it!!.getLightMutedColor(ThemeUtil.getColorAccent(itemView.context))
                            //暗、鲜艳
                            val darkVibrantColor: Int = it.getDarkVibrantColor(Color.BLACK)
                            title.setBackgroundColor(lightMutedColor)
                            title.setTextColor(darkVibrantColor)

                        }
                    }

                })
        }

        @SuppressLint("ObjectAnimatorBinding")
        fun alpView(v:View){
            val animator = ObjectAnimator.ofFloat(v,"alpha",0f,0.25f,0.75f,1f)
            animator.duration=700
            animator.start()
        }
    }

}
