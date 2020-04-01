package com.jil.paintf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.repository.DocData
import com.jil.paintf.repository.Picture
import com.jil.paintf.repository.Tag
import kotlin.collections.ArrayList

class DocDetailAdapter(var docData: DocData, var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var illusts: ArrayList<Picture> = ArrayList()
    var width :Int=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        width=parent.width

        return when(viewType){
            ITEM.BLANK.ordinal->{
                BlankVHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.blank_view,
                        parent,
                        false
                    )
                )
            }
            ITEM.IMAGE.ordinal->{
                ImageVHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.item_doc_illusts_layout,
                        parent,
                        false
                    ))
            }
            ITEM.DETAIL.ordinal->{
                DetailVHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.item_doc_detail,
                        parent,
                        false
                    )
                )
            }
            else -> {
                ImageVHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.item_doc_illusts_layout,
                        parent,
                        false
                    ))
            }
        }

    }



    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageVHolder) {
            Glide.with(mContext).load(illusts[position-1].img_src).override(illusts[position-1].img_width,illusts[position].img_height)
                .into(holder.image)
        }
        if(holder is BlankVHolder){

        }
        if(holder is DetailVHolder){
            holder.like.text =docData.item.like_count.toString()
            holder.see.text =docData.item.view_count.toString()
            holder.star.text =docData.item.collect_count.toString()
            holder.imageSize.text=illusts[0].img_width.toString()+"*"+illusts[0].img_height
            holder.displayTags(docData.item.tags as ArrayList<Tag>)
        }
    }

    override fun getItemCount(): Int {
        return illusts.size+2
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0->{
               ITEM.BLANK.ordinal
            }
            illusts.size+1->{
                ITEM.DETAIL.ordinal
            }
            else->{
                ITEM.IMAGE.ordinal
            }
        }
    }

    private class ImageVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imageView)
    }

    private class BlankVHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    private class DetailVHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val see: TextView =itemView.findViewById(R.id.textView7)
        val like:TextView=itemView.findViewById(R.id.title)
        val star:TextView =itemView.findViewById(R.id.textView9)
        val tags:RecyclerView =itemView.findViewById(R.id.tags)
        val imageSize:TextView =itemView.findViewById(R.id.textView10)

        fun displayTags(list: ArrayList<Tag>){
            val layoutManager =GridLayoutManager(itemView.context,3)
            tags.adapter =object :SuperRecyclerAdapter<Tag>(list){
                override fun bindData(holder: SuperVHolder, position: Int) {
                    holder.setText(data[position].name,R.id.text)
                }

                override fun setLayout(viewType: Int): Int {
                    return R.layout.item_tag
                }


            }
            tags.layoutManager=layoutManager
            layoutManager.spanSizeLookup=object :GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return list[position].name.length/5
                }
            }

        }
    }

    fun findIllustsUrl(){
        illusts.clear()
        if (docData.item.pictures.isEmpty()) {

        } else {
            docData.item.pictures.map {
                illusts.add(it)
            }
        }
    }

    enum class ITEM{
        BLANK,
        IMAGE,
        DETAIL,
    }

    init {
        findIllustsUrl()
    }
}