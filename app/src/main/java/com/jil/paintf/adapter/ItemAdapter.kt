package com.jil.paintf.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jil.paintf.R
import com.jil.paintf.activity.UserActivity
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ItemTouchHelperAdapter
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.Item
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.DocOperateModel

class ItemAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),ItemTouchHelperAdapter {
    val data = arrayListOf<Item>()
    var itemOnClickListener: AdapterView.OnItemClickListener? = null
    var status = "正在加载..."
    private val viewModel =
        ViewModelProvider.AndroidViewModelFactory(AppPaintF.instance).create(DocOperateModel::class.java)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE.ITEM_TYPE_DATA.ordinal -> ItemVHolder(
                LayoutInflater.from(mContext).inflate(
                    if (AppPaintF.instance.stagger) R.layout.staggered_item_doc_list else R.layout.item_doc_list,
                    parent,
                    false
                )
            )
            else -> BottomViewHolder(
                LayoutInflater.from(mContext).inflate(
                    R.layout.item_recycleview_loading,
                    parent,
                    false
                )
            )
        }
    }

    init {
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                for (index in 0 until 6) {
                    notifyItemChanged(itemCount + index)
                }
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {


            }
        })
    }

    //底部 ViewHolder
    class BottomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView23)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        Logger.d(position.toString()+"onBindViewHolder->"+holder.toString())
        if (holder is ItemVHolder) {

            holder.imageUrl = data[position].item.pictures[0].img_src + "@512w_384h_1e.webp"
            holder.icoUrl = data[position].user.head_url + "@32w_32h.webp"
            holder.displayImage()
            if (data[position].item.already_voted == 0) {
                holder.voteIco.setImageResource(R.drawable.ic_no_voted)
            } else {
                holder.voteIco.setImageResource(R.drawable.ic_already_voted)
            }
            holder.title.text = data[position].item.title
            if (data[position].item.pictures.size == 1) {
                holder.count.visibility = View.GONE
            } else {
                holder.count.visibility = View.VISIBLE
                holder.count.text = data[position].item.pictures.size.toString() + "p"
            }

            holder.voteIco.setOnClickListener {
                if (AppPaintF.instance.csrf == null) {
                    Toast.makeText(mContext, "你还没有登录！", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                var type = 1
                if (data[position].item.already_voted == 0)
                    type = 1
                if (data[position].item.already_voted == 1)
                    type = 2

                viewModel.voteResult.observe(mContext as LifecycleOwner, {
                    //点赞按钮
                    if (it.data.type == 1) {
                        data[position].item.already_voted = 1
                        holder.voteIco.setImageResource(R.drawable.ic_already_voted)
                    } else if (it.code == 110022) {
                        Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                    } else if (it.data.type == 2) {
                        data[position].item.already_voted = 0
                        holder.voteIco.setImageResource(R.drawable.ic_no_voted)
                    }
                    /**
                     * 不理解！！！ post请求取消点赞时，会返回两次结果
                     * 通过对比发现！
                     * b站对已经点过赞的->先返回操作之前的状态->再进行操作之后的状态
                     * 这里进行判断，只有得到正确的结果后才移除观察者
                     */
                    if (it.data.type == type) {
                        viewModel.voteResult.removeObservers(mContext as LifecycleOwner)
                    }

                })
                viewModel.doNetVote(data[position].item.doc_id, type)
            }

            holder.image.setOnClickListener { v ->
                if (itemOnClickListener != null)
                    itemOnClickListener!!.onItemClick(null, v, position, position.toLong())
            }

            holder.ico.setOnClickListener {
                UserActivity.startUserActivity(mContext, data[position].user.uid)
            }
        }

        if (holder is BottomViewHolder) {
            holder.textView.text = status
            if (status == "正在加载...") {
                holder.progressBar.visibility = View.VISIBLE
            } else {
                holder.progressBar.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size + 1
    }

    //判断当前item是否是FooterView
    fun isBottomView(position: Int): Boolean {
        return position >= data.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            data.size -> ITEM_TYPE.ITEM_TYPE_LOAD.ordinal
            else -> {
                ITEM_TYPE.ITEM_TYPE_DATA.ordinal
            }
        }
    }

    enum class ITEM_TYPE {
        ITEM_TYPE_DATA,
        ITEM_TYPE_LOAD,
    }

    public class ItemVHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icoUrl: String? = null
        var imageUrl: String? = null
        var ico: ImageView = itemView.findViewById(R.id.imageView2)
        var image: ImageView = itemView.findViewById(R.id.imageView)
        var title: TextView = itemView.findViewById(R.id.textView)
        var count: TextView = itemView.findViewById(R.id.textView2)
        var voteIco: ImageView = itemView.findViewById(R.id.vote_ico)

        @SuppressLint("ObjectAnimatorBinding")
        fun alpView(v: View) {
            val animator = ObjectAnimator.ofFloat(v, "alpha", 0f, 0.25f, 0.75f, 1f)
            animator.duration = 700
            animator.start()
        }

        fun displayImage() {
            image.visibility = View.INVISIBLE
            Glide.with(itemView.context).asBitmap().load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        image.visibility = View.VISIBLE
                        super.onLoadFailed(errorDrawable)
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        image.setImageBitmap(resource)
                        image.visibility = View.VISIBLE
                        alpView(image)
                        val builder = Palette.from(resource)
                        builder.generate {
                            //亮、柔和
                            val lightMutedColor: Int =
                                it!!.getLightMutedColor(ThemeUtil.getColorAccent(itemView.context))
                            //暗、鲜艳
                            val darkVibrantColor: Int = it.getDarkVibrantColor(Color.BLACK)
                            ico.setBackgroundColor(lightMutedColor)
                            title.setBackgroundColor(lightMutedColor)
                            title.setTextColor(darkVibrantColor)
                            voteIco.setBackgroundColor(lightMutedColor)

                        }
                    }

                })

            icoUrl = icoUrl.let {
                Glide.with(itemView.context).load(it).placeholder(R.drawable.noface)
                    .transform(GlideCircleWithBorder(2, ThemeUtil.getColorAccent(image.context)))
                    .into(ico)
                null

            }
        }
    }

    override fun onItemMove(source: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?) {
        //拖动的 item 的下标
        val fromPosition: Int = source!!.adapterPosition
        //目标 item 的下标，目标 item 就是当拖曳过程中，不断和拖动的 item 做位置交换的条目。
        val toPosition = target!!.adapterPosition
        //交换数据
//        Collections.swap(data, fromPosition, toPosition)
        //交换位置index
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(source: RecyclerView.ViewHolder?) {
        //TODO("Not yet implemented")
    }

    override fun onItemSelectedChanged(source: RecyclerView.ViewHolder?, actionState: Int) {

    }

    override fun onItemClear(source: RecyclerView.ViewHolder?) {
        //给已经完成拖曳的 item 恢复开始的背景。
        //这里我们设置的颜色尽量和你 item 在 xml 中设置的颜色保持一致

    }

}
