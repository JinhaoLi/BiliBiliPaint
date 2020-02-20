package com.jil.paintf.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.repository.DocData
import com.jil.paintf.repository.Picture
import java.util.*
import kotlin.collections.ArrayList

class DocDetailAdapter(var docData: DocData, var mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var illusts: ArrayList<Picture> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemVHolder(
            LayoutInflater.from(mContext).inflate(
                R.layout.doc_illusts_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemVHolder) {
            Glide.with(mContext).load(illusts[position].img_src).into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return illusts.size
    }

    private class ItemVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.imageView)

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

    init {
        findIllustsUrl()
    }
}