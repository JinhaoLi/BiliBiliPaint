package com.jil.paintf.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jil.paintf.R
import com.jil.paintf.activity.DocDetailActivity.Companion.startDocDetailActivity
import com.jil.paintf.activity.UserActivity
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.Item

class ItemAdapter(private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data=arrayListOf<Item>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_TYPE.ITEM_TYPE_DATA.ordinal->ItemVHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.item_doc_list,
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
            holder.imageUrl=data[position].item.pictures[0].img_src + "@512w_384h_1e.webp"
            holder.icoUrl=data[position].user.head_url+"@32w_32h.webp"
            holder.displayImage()
            holder.title.text = data[position].item.title
            if(data[position].item.pictures.size==1){
                holder.count.visibility=View.GONE
            }else{
                holder.count.visibility=View.VISIBLE
                holder.count.text = data[position].item.pictures.size.toString()
            }

            holder.image.setOnClickListener { v ->
                val intArray =IntArray(data.size)
                for (index in 0 until data.size){
                    intArray[index] =data[index].item.doc_id
                }
                startDocDetailActivity(v.context,intArray,data[position].item.doc_id)
            }

            holder.ico.setOnClickListener{
                UserActivity.startUserActivity(mContext,data[position].user.uid)
            }
        }

        if(holder is BottomViewHolder){
            holder.textView!!.text="正在加载..."
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
        var icoUrl:String? =null
        var imageUrl:String?=null
        var ico :ImageView = itemView.findViewById(R.id.imageView2)
        var image: ImageView = itemView.findViewById(R.id.imageView)
        var title: TextView = itemView.findViewById(R.id.textView)
        var count: TextView = itemView.findViewById(R.id.textView2)


        fun displayImage(){
            Glide.with(itemView.context).asBitmap().load(imageUrl).placeholder(R.drawable.noface)
                //.transform(GlideCircleWithBorder(2, ThemeUtil.getColorAccent(image.context)))
                .into(object :CustomTarget<Bitmap>(){
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        image.setImageBitmap(resource);
                        val builder =Palette.from(resource);
                        builder.generate {
                            //亮、柔和
                            val lightMutedColor: Int = it!!.getLightMutedColor(ThemeUtil.getColorAccent(itemView.context))
                            //暗、鲜艳
                            val darkVibrantColor: Int = it.getDarkVibrantColor(Color.BLACK)
                            ico.setBackgroundColor(lightMutedColor)
                            title.setBackgroundColor(lightMutedColor)
                            title.setTextColor(darkVibrantColor)
                        }
                    }

                })

            icoUrl=icoUrl.let {
                Glide.with(itemView.context).load(it).placeholder(R.drawable.noface)
                    .transform(GlideCircleWithBorder(2, ThemeUtil.getColorAccent(image.context)))
                .into(ico)
                null

            }
        }
    }

}
