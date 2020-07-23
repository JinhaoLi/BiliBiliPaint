package com.jil.paintf.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jil.paintf.R
import com.jil.paintf.activity.ReplyViewActivity
import com.jil.paintf.activity.SearchActivity
import com.jil.paintf.activity.UserActivity
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.OnDoubleClickListener
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.DocData
import com.jil.paintf.repository.Reply
import com.jil.paintf.repository.ReplyRepository
import com.jil.paintf.repository.Tag
import com.jil.paintf.service.AppPaintF.Companion.LoadLevel
import com.jil.paintf.service.AppPaintF.Companion.save_dir_path
import com.jil.paintf.service.CorrespondingValue.getHtmlFormte
import com.jil.paintf.viewmodel.DocViewModel
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.*
import java.net.MalformedURLException
import java.net.URL

/**==============================
 *===============================
 *== @Date: 2020/7/5 0:11
 *== @author JIL
 *===============================
 *===============================
 **/
class PreViewAdapter(
    var docData: DocData,
    val viewModel: DocViewModel
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var loadLevel:String = when (LoadLevel) {
        1080 -> "@1080w_1e.webp"
        5000 -> ""
        else -> "@720w_1e.webp"
    }

    lateinit var mOperateVote: (view:View ) -> Unit
    fun setOperateVote(listener:(view:View) -> Unit ){
        mOperateVote =listener
    }

    lateinit var mOperateCollect:(view:View ) ->Unit
    fun setOperateCollect(listener:(view:View)->Unit){
        mOperateCollect =listener
    }

    lateinit var mOperateShare:() ->Unit
    fun setOperateShare(listener:()->Unit){
        mOperateShare =listener
    }

    lateinit var mOperateDownload:(position:Int) ->Unit
    fun setOperateDownload(listener:(position:Int)->Unit){
        mOperateDownload =listener
    }

    lateinit var mOperateArtWork:(view:View ) ->Unit
    fun setOperateArtWork(listener:(view:View)->Unit){
        mOperateArtWork =listener
    }

    lateinit var mOperateVoteReply:(reply:Reply,position:Int) -> Unit
    fun setOprateVoteReply(listener: (reply:Reply,position:Int) -> Unit){
        mOperateVoteReply=listener
    }

    lateinit var mOperateReply2:(reply:Reply,position:Int,view:View) -> Unit
    fun setOprateReply2(listener: (reply:Reply,position:Int,view:View) -> Unit){
        mOperateReply2=listener
    }

    lateinit var mOperateReplyArt:(view:View) ->Unit
    fun setOperateReplyArt(listener:(view:View)->Unit){
        mOperateReplyArt=listener
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
                PreReplyItem(LayoutInflater.from(parent.context).inflate(R.layout.item_reply_layout,parent,false))
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

    var pic: File? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PreImageItem){
            val image_url =docData.item.pictures[position-1].img_src
            holder.image.visibility=View.INVISIBLE
            Glide.with(holder.itemView.context).asFile().load(image_url+loadLevel).placeholder(R.color.white).into(object :CustomTarget<File>(){
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                    holder.image.setImageURI(Uri.fromFile(resource))
                    holder.image.visibility=View.VISIBLE
                    alpView(holder.image)
                    pic = resource
                }

            })
            holder.itemView.setOnLongClickListener {
                mOperateDownload.invoke(position-1)
                return@setOnLongClickListener false
            }
        }
        if(holder is PreHeaderItem){
            docData.let {
                holder.share!!.setOnClickListener{
                    mOperateShare.invoke()
                }
                holder.ico.setOnClickListener(mOperateArtWork)
                holder.artical_name.text =it.user.name
                holder.title.text =it.item.title
                holder.upload_date.text =it.item.upload_time_text
                Glide.with(holder.itemView.context).load(it.user.head_url)
                    .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(holder.itemView.context))).into(holder.ico)
            }
        }
        if(holder is PreBottomInfo){
            //点赞
            holder.vote!!.setOnClickListener {
                mOperateVote.invoke(it)
            }

            //收藏
            holder.collect.setOnClickListener {
                mOperateCollect.invoke(it)
            }
            if(docData.item.already_voted==1){
                holder.itemView.findViewById<ImageView>(R.id.imageView8).setImageResource(R.drawable.ic_like)
            }else{
                holder.itemView.findViewById<ImageView>(R.id.imageView8).setImageResource(R.drawable.ic_no_vote_big)
            }

            if(docData.item.already_collected==1){
                holder.itemView.findViewById<ImageView>(R.id.imageView9).setImageResource(R.drawable.ic_star)
            }else{
                holder.itemView.findViewById<ImageView>(R.id.imageView9).setImageResource(R.drawable.ic_no_star)
            }
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
            if(reply_list.isEmpty()&&isFirstLoadReply){
                isFirstLoadReply=false
                viewModel.liveMyReply.observeForever {
                    reply_list.add(0,it)
                    notifyItemInserted(0)
                }
                viewModel.liveReplyData.observeForever(replyObserver)
                replyInit()
            }
            holder.reply.setOnClickListener (mOperateReplyArt)
        }
        if(holder is PreReplyItem){
            val rela =position-(docData.item.pictures.size+2)
            val reply =reply_list[rela]
            holder.vote_reply.apply {
                if(reply.action==1)
                    setImageResource(R.drawable.ic_already_voted)
                else
                    setImageResource(R.drawable.ic_no_voted)
            }.setOnClickListener{
                reply.let {
                    mOperateVoteReply.invoke(it,position)
                }
            }
            holder.reply_this.setOnClickListener{
                reply.let {
                    mOperateReply2.invoke(it,position,holder.itemView)
                }
            }
            holder.reply_ico.setOnClickListener {
                UserActivity.startUserActivity(it.context,reply.mid)
            }
            Glide.with(holder.itemView.context).load(reply.member.avatar)
                .transform(GlideCircleWithBorder(1,ThemeUtil.getColorAccent(holder.itemView.context))).into(holder.reply_ico)
            holder.itemView.setOnClickListener {  }
            holder.reply_text.setOnTouchListener(OnDoubleClickListener(object : OnDoubleClickListener.DoubleClickCallback {
                override fun onLongClick(): Boolean {
                    return true
                }

                override fun onDoubleClick(view: View) {
                    reply.let {
                        mOperateVoteReply.invoke(reply,position)
                    }
                }
            }))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.reply_text.text = getHtmlFormte(reply.content.message)
            }else{
                holder.reply_text.text =reply.content.message
            }
            holder.reply_text.movementMethod = LinkMovementMethod.getInstance()
            holder.reply_user.text =reply.member.uname

            if(reply.replies.isNullOrEmpty()){
                holder.reply_start.visibility=View.GONE
                holder.show_reply.visibility=View.GONE
                return
            }

            holder.reply_start.visibility=View.VISIBLE
            holder.show_reply.visibility=View.VISIBLE
            holder.show_reply.text = "共"+reply_list[rela].rcount+"条评论"
            holder.reply_start.removeAllViews()
            holder.reply_start.setOnClickListener{
                if(reply.replies.isNullOrEmpty())
                    return@setOnClickListener
                ReplyViewActivity.startActivity(it.context,reply.oid,reply.rpid)
            }
            reply_list[rela].replies.map {
                val _tempReply =LayoutInflater.from(holder.reply_start.context).inflate(R.layout.item_simple_reply,null)
                _tempReply.findViewById<TextView>(R.id.textView17).text =it.member.uname+":"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    _tempReply.findViewById<TextView>(R.id.textView30).text = getHtmlFormte(it.content.message)
                }else{
                    _tempReply.findViewById<TextView>(R.id.textView30).text = it.content.message
                }
                holder.reply_start.addView(_tempReply)
            }
        }
    }

    var isFirstLoadReply =true

    private val replyObserver  = Observer<ReplyRepository> {
        if(it.data.replies.isNullOrEmpty()){
           return@Observer
        }
        if(it.data.replies[0].oid!=docData.item.doc_id){
            return@Observer
        }
        val oldCount =reply_list.size
        val add =it.data.replies.size
        Log.d("PaintF","接收到ReplyReponsitory\told_count =$oldCount" +
                "\tadd=$add")
        reply_list.addAll(it.data.replies as ArrayList<Reply>)
        try {
            notifyItemRangeInserted(1+docData.item.pictures.size+1+oldCount,add)
            notifyItemChanged(itemCount-1)
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }

    }

    fun addReply(reply: Reply){
        reply_list.add(reply)
        notifyItemInserted(itemCount-2)
    }

    fun refresh(docData: DocData){
        this.docData=docData
        notifyItemRangeChanged(0,itemCount)
    }
    fun replyInit(){
        viewModel.doNetReply(docData.item.doc_id)
    }

    fun download(position: Int, context: Context) {
        AlertDialog.Builder(context).setTitle("下载图片?")
            .setNegativeButton("保存预览") { dialog, which ->
                try {
                    pic?.let { downLoadPic(docData.item.pictures[position].img_src , it) }
                    Toast.makeText(context, "已保存！", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show()
                }
            }.setPositiveButton("保存原图") { dialog, which ->
                if (loadLevel == "") {
                    try {
                        pic?.let { downLoadPic(docData.item.pictures[position].img_src, it) }
                        Toast.makeText(context, "已保存！", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    downLoadPic(docData.item.pictures[position].img_src, context)
                }
            }.create().show()
    }

    @Throws(IOException::class)
    private fun downLoadPic(urlStr: String, into: File) {
        val f = File(save_dir_path)
        if (!f.exists()) if (!f.mkdir()) {
            throw FileNotFoundException("创建文件夹失败")
        }
        val strs = urlStr.split("/".toRegex()).toTypedArray()
        val pic = File(f, strs[strs.size - 1])
        val fi = FileInputStream(into)
        val fo = FileOutputStream(pic)
        val fic = fi.channel
        val foc = fo.channel
        foc.transferFrom(fic, 0, fic.size())
        fo.close()
        fi.close()
        fic.close()
        foc.close()
    }

    @SuppressLint("CheckResult")
    private fun downLoadPic(urlStr: String, context: Context) {
        val pd1 = ProgressDialog(context)
        var picSize = 0L
        Observable.just(urlStr).flatMap{
            ObservableSource<Bundle> {
                var url: URL? = null
                try {
                    url = URL(urlStr)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
                if (url == null) {
                    return@ObservableSource
                }
                val f = File(save_dir_path)
                if (!f.exists()) f.mkdir()
                val strs = urlStr.split("/".toRegex()).toTypedArray()
                val pic = File(f, strs[strs.size - 1])
                if (pic.exists()) {
                    it.onError(Throwable("图片已存在！"))
                    return@ObservableSource
                }
                val builder = OkHttpClient.Builder()
                val rq = Request.Builder().url(urlStr)
                val call = builder.build().newCall(rq.build())
                call.enqueue(object : Callback {
                    var bundle = Bundle()
                    override fun onFailure(call: Call, e: IOException) {}

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.code == 200) {
                            val inputStream = response.body!!.byteStream()
                            val fileOutputStream = FileOutputStream(pic)
                            val bytes = ByteArray(1024)
                            var len = 0
                            var all_size: Long = 0
                            bundle.putLong("pic_size", response.body!!.contentLength() / 1024)
                            bundle.putLong("loaded", 0)
                            it.onNext(bundle)
                            while (inputStream.read(bytes).also { len = it } != -1) {
                                all_size += len.toLong()
                                fileOutputStream.write(bytes, 0, len)
                                fileOutputStream.flush()
                                bundle.putLong("loaded", all_size / 1024)
                                it.onNext(bundle)
                            }
                            inputStream.close()
                            fileOutputStream.close()
                            it.onComplete()
                        } else {
                            //                            Logger.d(response);
                        }
                    }
                })
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            if (picSize == 0L) picSize = it.getLong("pic_size")
            val isLoad = it.getLong("loaded")
            val percentage = (isLoad * 100 / picSize).toInt()
            pd1.progress = percentage
            pd1.setMessage(picSize.toString() + "/" + isLoad + "Kb")
        },{
            pd1.setTitle("错误")
            pd1.progress = 100
            pd1.setMessage(it.message)
        },{
            pd1.setTitle("下载成功")
            pd1.progress = 100
            pd1.setMessage("原图已下载(共" + picSize + "Kb)")
        },{
            //依次设置标题,内容,是否用取消按钮关闭,是否显示进度
            pd1.setTitle("图片下载中")
            pd1.setMessage("图片正在下载中,请稍后...")
            pd1.setCancelable(true)
            //这里是设置进度条的风格,HORIZONTAL是水平进度条,SPINNER是圆形进度条
            pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            //调用show()方法将ProgressDialog显示出来
            pd1.show()
        })

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
        val vote =itemView.findViewById<ImageView>(R.id.imageView8)
        val collect_count =itemView.findViewById<TextView>(R.id.textView9)
        val collect =itemView.findViewById<ImageView>(R.id.imageView9)
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
        val vote_reply =itemView.findViewById<ImageView>(R.id.imageView12)
        val reply_this =itemView.findViewById<ImageView>(R.id.imageView13)
        val reply_start =itemView.findViewById<ViewGroup>(R.id.start_reply)
    }


}

