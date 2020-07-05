package com.jil.paintf.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jil.paintf.R
import com.jil.paintf.activity.SearchActivity
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.DocData
import com.jil.paintf.repository.Reply
import com.jil.paintf.repository.Tag
import com.jil.paintf.service.AppPaintF.Companion.LoadLevel
import com.jil.paintf.viewmodel.DocViewModel
import java.lang.Exception

/**==============================
 *===============================
 *== @Date: 2020/7/5 0:11
 *== @author JIL
 *===============================
 *===============================
 **/
class PreViewAdapter(
    val docData: DocData,
    val viewModel: DocViewModel,
    val viewLifecycleOwner: LifecycleOwner,
    val recyclerPage: RecyclerView
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var loadLevel:String = when (LoadLevel) {
        1080 -> "@1080w_1e.webp"
        5000 -> ""
        else -> "@720w_1e.webp"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE.image.ordinal->{
                PreImageItem(LayoutInflater.from(parent.context).inflate(R.layout.item_pre_image,parent,false))
            }
            VIEW_TYPE.header.ordinal ->{
                PreHeaderItem(LayoutInflater.from(parent.context).inflate(R.layout.item_pre_header,parent,false))
            }
            VIEW_TYPE.bottom.ordinal->{
                PreBottomInfo(LayoutInflater.from(parent.context).inflate(R.layout.item_pre_bottom,parent,false))
            }
            VIEW_TYPE.reply.ordinal->{
                PreReplyItem(LayoutInflater.from(parent.context).inflate(R.layout.item_pre_reply,parent,false))
            }
            VIEW_TYPE.show_reply.ordinal->{
                PreShowReply(LayoutInflater.from(parent.context).inflate(R.layout.item_pre_show_reply,parent,false))
            }
            else->{
                PreImageItem(LayoutInflater.from(parent.context).inflate(R.layout.item_pre_image,parent,false))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(position ==0){//header
            return VIEW_TYPE.header.ordinal
        }else if(position==docData.item.pictures.size+1){//info
            return VIEW_TYPE.bottom.ordinal
        }else if(position==docData.item.pictures.size+reply_list.size+2){//show
            return VIEW_TYPE.show_reply.ordinal
        }else if(position>0&&position<docData.item.pictures.size+1){//pic
            return VIEW_TYPE.image.ordinal
        }else if(position>docData.item.pictures.size+1&&position<docData.item.pictures.size+reply_list.size+2){//reply
            return VIEW_TYPE.reply.ordinal
        }else{
            throw Exception("没有找到布局类型")
        }
    }

    enum class VIEW_TYPE{
        image,header,bottom,show_reply,reply
    }

    override fun getItemCount(): Int {
        /*header + image.size + bottominfo + show_reply + reply.size*/
        return 1+docData.item.pictures.size+1+1+reply_list.size
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun alpView(v:View){
        val animator = ObjectAnimator.ofFloat(v,"alpha",0f,0.25f,0.75f,1f)
        animator.duration=700
        animator.start()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PreImageItem){
            val image_url =docData.item.pictures[position-1].img_src
            holder.image.visibility=View.INVISIBLE
            Glide.with(holder.itemView.context).load(image_url+loadLevel).placeholder(R.color.white).into(object :CustomTarget<Drawable>(){
                override fun onLoadCleared(placeholder: Drawable?) {
                }
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    holder.image.setImageDrawable(resource)
                    holder.image.visibility=View.VISIBLE
                    alpView(holder.image)

                }
            })
        }
        if(holder is PreHeaderItem){
            docData.let {
                holder.artical_name.text =it.user.name
                holder.title.text =it.item.title
                holder.upload_date.text =it.item.upload_time_text
                Glide.with(holder.itemView.context).load(it.user.head_url).into(holder.ico)
                holder.download.setOnClickListener{
                    /*下载*/
                }
                holder.share.setOnClickListener {
                    /*分享*/
                }
            }
        }
        if(holder is PreBottomInfo){
            docData.let {
                holder.see_count.text =it.item.view_count.toString()
                holder.collect_count.text =it.item.collect_count.toString()
                holder.vote_count.text =it.item.vote_count.toString()
                holder.resolving.text =it.item.pictures[0].let {
                    val h =it.img_height
                    val w =it.img_width
                    "$h*$w"
                }
                holder.describe.text =it.item.description
                val layoutManager = LinearLayoutManager(holder.itemView.context)
                holder.tags.adapter =object : SuperRecyclerAdapter<Tag>(docData.item.tags as ArrayList<Tag>){
                    override fun bindData(holder: SuperVHolder, position: Int) {
                        holder.setText(data[position].name,R.id.text)
                        holder.getView(R.id.text).setOnClickListener {
                            SearchActivity.startSearchActivity(it.context,data[position].name)
                        }
                    }

                    override fun setLayout(viewType: Int): Int {
                        return R.layout.item_tag
                    }

                }
                layoutManager.orientation= RecyclerView.HORIZONTAL
                holder.tags.layoutManager=layoutManager

            }
        }
        if(holder is PreShowReply){
            recyclerPage.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        if(!recyclerView.canScrollVertically(1)){
                            replyInit()
                        }
                    }
                }
            })
            holder.reply.setOnClickListener {
                replyInit()
            }
        }
        if(holder is PreReplyItem){
            val rela =position-(docData.item.pictures.size+2)
            Glide.with(holder.itemView.context).load(reply_list[rela].member.avatar).into(holder.reply_ico)
            holder.reply_text.text =reply_list[rela].content.message
            holder.reply_user.text =reply_list[rela].member.uname
            if(reply_list[rela].replies.isNullOrEmpty()){
                holder.show_reply.visibility=View.GONE
                return
            }
            holder.show_reply.visibility=View.VISIBLE
            holder.show_reply.text = "查看"+reply_list[rela].replies.size+"条评论"
            holder.show_reply.setOnClickListener {
                val listPopupWindow =ListPopupWindow(it.context)
                listPopupWindow.setAdapter(object :ArrayAdapter<Reply>(it.context,R.layout.item_reply_layout,reply_list[rela].replies){
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val layout =LayoutInflater.from(context).inflate(R.layout.item_reply_layout,null)
                        val data =getItem(position)

                        val nameTextView =layout.findViewById<TextView>(R.id.textView22)
                        nameTextView.text = data!!.member.uname

                        val headerIcon=layout.findViewById<ImageView>(R.id.icon)
                        Glide.with(parent.context).load(data.member.avatar)
                            .transform(GlideCircleWithBorder(1, ThemeUtil.getColorAccent(headerIcon.context)))
                            .into(headerIcon)

                        val content =layout.findViewById<TextView>(R.id.textView20)
                        content.text = data.content.message

                        return layout
                    }
                })

                listPopupWindow.setOnItemClickListener { _, _, _, id ->
                    listPopupWindow.dismiss()
                }
                listPopupWindow.anchorView =it
                listPopupWindow.width=-1
                listPopupWindow.show()
            }
        }


    }

    val replyObserver  = Observer<List<Reply>> {
        if(it.isNullOrEmpty()){
            notifyDataSetChanged()
           return@Observer
        }
        if(it[0].oid!=docData.item.doc_id){
//            notifyDataSetChanged()
            return@Observer
        }
        val oldCount =reply_list.size
        val add =it.size
        if(reply_list.contains(it[0])){
            return@Observer
        }
        viewModel.liveReplyData.removeObservers(viewLifecycleOwner)
        reply_list.addAll(it as ArrayList<Reply>)
        Log.d("Paint","oldCount =$oldCount \t add_count =$add")
        notifyItemRangeInserted(1+docData.item.pictures.size+1+oldCount,add)
        notifyItemChanged(itemCount-1)
//        notifyDataSetChanged()

    }
    fun replyInit(){
        viewModel.liveReplyData.observe(viewLifecycleOwner,replyObserver)
        viewModel.doNetReply(docData.item.doc_id,false)
    }


    class PreImageItem(itemView: View): RecyclerView.ViewHolder(itemView){
        val image =itemView.findViewById<ImageView>(R.id.imageView4)
    }

    class PreHeaderItem(itemView: View): RecyclerView.ViewHolder(itemView){
        val ico =itemView.findViewById<ImageView>(R.id.imageView5)
        val title =itemView.findViewById<TextView>(R.id.textView4)
        val artical_name =itemView.findViewById<TextView>(R.id.textView5)
        val upload_date =itemView.findViewById<TextView>(R.id.textView6)
        val download =itemView.findViewById<ImageView>(R.id.download)
        val share =itemView.findViewById<ImageView>(R.id.imageView6)
    }

    class PreBottomInfo(itemView: View): RecyclerView.ViewHolder(itemView){
        val see_count =itemView.findViewById<TextView>(R.id.textView7)
        val vote_count =itemView.findViewById<TextView>(R.id.textView8)
        val collect_count =itemView.findViewById<TextView>(R.id.textView9)
        val resolving =itemView.findViewById<TextView>(R.id.textView10)
        val describe =itemView.findViewById<TextView>(R.id.textView12)
        val tags =itemView.findViewById<RecyclerView>(R.id.tags)
    }

    class PreShowReply(itemView: View): RecyclerView.ViewHolder(itemView){
        val reply =itemView.findViewById<ImageView>(R.id.imageView10)
    }

    val reply_list = arrayListOf<Reply>()
    class PreReplyItem(itemView: View): RecyclerView.ViewHolder(itemView){
        val reply_ico =itemView.findViewById<ImageView>(R.id.icon)
        val reply_user =itemView.findViewById<TextView>(R.id.textView22)
        val reply_text =itemView.findViewById<TextView>(R.id.textView20)
        val show_reply =itemView.findViewById<TextView>(R.id.textView21)
    }


}