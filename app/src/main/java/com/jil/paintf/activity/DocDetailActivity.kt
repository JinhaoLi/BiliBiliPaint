package com.jil.paintf.activity

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jil.paintf.R
import com.jil.paintf.adapter.ImagePagerAdapter
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.RecycleItemDecoration
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.DocData
import com.jil.paintf.repository.Reply
import com.jil.paintf.repository.Tag
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.DocOperateModel
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.activity_doc_detail.*
import kotlinx.android.synthetic.main.item_doc_detail.*


class DocDetailActivity : AppCompatActivity(),
    ImagePagerAdapter.HideLayoutCallBack,ImagePagerAdapter.UpAndNextPagerCallBack {
    var current=0
    var idArray:IntArray?=null
    var viewModel:DocViewModel? =null
    private lateinit var operateViewModel:DocOperateModel
    var adapter:ImagePagerAdapter<String>? =null
    private var lock =false
    var docData:DocData?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_doc_detail)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        val bundle =intent.getBundleExtra("param1")
        idArray=bundle!!.getIntArray("intArray")
        val docId =bundle.getInt("doc_id")
        current =idArray!!.indexOf(docId)
        viewModel =ViewModelProvider(this).get(DocViewModel::class.java)
        operateViewModel =ViewModelProvider(this).get(DocOperateModel::class.java)
        viewModel!!.data.observe(this, Observer { docData ->
            this.docData=docData
            if(docData!=null){
                imageView5!!.setOnClickListener {
                    UserActivity.startUserActivity(it.context,docData.user.uid)
                }
            }
            val imageArray = arrayListOf<String>()
            docData.item.pictures.map {
                imageArray.add(it.img_src)
            }
            pager!!.currentItem=0
            if(adapter==null){
                adapter = ImagePagerAdapter(imageArray)
                pager!!.adapter =adapter
                adapter!!.setListener(this)
                adapter!!.setUpAndNextPagerCallBack(this)
            }else{
                adapter!!.ts=imageArray
                adapter!!.notifyDataSetChanged()
            }
            //手动选择页码
            textView13!!.setOnClickListener {
                //Toast.makeText(it.context, "未实现", Toast.LENGTH_SHORT).show()
                val listPopupWindow =ListPopupWindow(it.context)
                val picIndex = arrayListOf<Int>()
                for (index in 1..imageArray.size){
                    picIndex.add(index)
                }
                listPopupWindow.setAdapter(ArrayAdapter<Int>(it.context, android.R.layout.simple_list_item_1,picIndex))
                listPopupWindow.setOnItemClickListener { _, _, position, _ ->
                    pager.currentItem=position
                    listPopupWindow.dismiss()
                }
                listPopupWindow.anchorView=it
                listPopupWindow.width=200
                listPopupWindow.show()
            }
            //获取评论
            showReply()

            //================================================================================标题，上传者头像
            if(!isDestroyed)
            Glide.with(this).load(docData.user.head_url).placeholder(R.drawable.noface)
                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this)))
                .into(imageView5)
            textView4!!.text=docData.item.title
            textView5!!.text=docData.user.name
            textView6!!.text=docData.item.upload_time
            //================================================================================喜欢，收藏，支持，tags
            val pic0 =docData.item.pictures[0]
            textView7.text =docData.item.view_count.toString()
            textView8.text =docData.item.vote_count.toString()
            textView9.text =docData.item.collect_count.toString()
            textView10.text=pic0.img_width.toString()+"*"+pic0.img_height
            textView12.text =if(docData.item.description=="")"一切尽在不言中..." else docData.item.description
            textView13.text ="1/"+docData.item.pictures.size
            if(docData.item.already_voted==1){
                imageView8.setImageResource(R.drawable.ic_like)
            }else{
                imageView8.setImageResource(R.drawable.ic_no_vote_big)
            }

            if(docData.item.already_collected==1){
                imageView9.setImageResource(R.drawable.ic_star)
            }else{
                imageView9.setImageResource(R.drawable.ic_no_star)
            }
            val layoutManager = LinearLayoutManager(this)
            tags.adapter =object : SuperRecyclerAdapter<Tag>(docData.item.tags as ArrayList<Tag>){
                override fun bindData(holder: SuperVHolder, position: Int) {
                    holder.setText(data[position].name,R.id.text)
                    holder.getView(R.id.text).setOnClickListener {
                        SearchActivity.startSearchActivity(this@DocDetailActivity,data[position].name)
                    }
                }

                override fun setLayout(viewType: Int): Int {
                    return R.layout.item_tag
                }

            }
            layoutManager.orientation= RecyclerView.HORIZONTAL
            tags.layoutManager=layoutManager

            //==================================================================================喜欢，收藏，支持，tags
        })
        viewModel!!.doNetGetDoc(docId)
        //滑动效果
        //pager!!.setPageTransformer(false, ImageSlideTransformer())

        //下载
        imageButton.setOnClickListener{
            if(checkReadWrite()){
                adapter!!.download(pager.currentItem,this)
            }else{
                //进行授权
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    6
                )
            }

        }

        //分享
        imageView6!!.setOnClickListener{
            val intent =Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT,"https://h.bilibili.com/"+idArray!![current])
            intent.type="text/plain"
            startActivity(intent)
        }

        //点赞
        imageView8!!.setOnClickListener {
            shakeView(it)
            if(operateViewModel.voteResult.hasActiveObservers()){
                return@setOnClickListener
            }
            if(AppPaintF.instance.cookie==null){
                Toast.makeText(this, "你还没有登录！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var type=1
            if(docData!!.item.already_voted==1)
                type=2
            operateViewModel.voteResult.observe(this, Observer {
                //点赞按钮
                if(it.data.type==1){
                    docData!!.item.already_voted=1
                    imageView8.setImageResource(R.drawable.ic_like)
                }else if(it.data.type==2){
                    docData!!.item.already_voted=0
                    imageView8.setImageResource(R.drawable.ic_no_vote_big)
                }else{
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                /**
                 * 不理解！！！ post请求取消点赞时，会返回两次结果
                 * 通过对比发现！
                 * b站对已经点过赞的->先返回操作之前的状态->再进行操作之后的状态
                 * 这里进行判断，只有得到正确的结果后才移除观察者
                 */
                if(it.data.type==type){
                    operateViewModel.voteResult.removeObservers(this)
                }

            })
            operateViewModel.doNetVote(idArray!![current],type)
        }

        //收藏
        imageView9.setOnClickListener {
            shakeView(it)
            if(operateViewModel.removeFavResult.hasActiveObservers()){
                return@setOnClickListener
            }
            if(AppPaintF.instance.cookie==null){
                Toast.makeText(this, "你还没有登录！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var isCollection =false
            if(docData!!.item.already_collected==1)
                isCollection=true
            if (isCollection){
                operateViewModel.removeFavResult.observe(this, Observer {
                    if(it.code==0){
                        docData!!.item.already_collected=0
                        imageView9.setImageResource(R.drawable.ic_no_star)
                        operateViewModel.removeFavResult.removeObservers(this)
                    }else{
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                })
                operateViewModel.doNetDeleteFav(idArray!![current])
            }else{
                operateViewModel.favResult.observe(this, Observer {
                    if(it.code==0){
                        docData!!.item.already_collected=1
                        imageView9.setImageResource(R.drawable.ic_star)
                        operateViewModel.favResult.removeObservers(this)
                    }else{
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                })
                operateViewModel.doNetAddFav(idArray!![current])
            }
        }

        var mIsScrolled =false
        pager!!.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                when(state){
                    ViewPager.SCROLL_STATE_DRAGGING->{
                        mIsScrolled=false
                    }
                    ViewPager.SCROLL_STATE_IDLE->{
                        if (!mIsScrolled ) {

                        }
                        mIsScrolled=true
                    }
                    ViewPager.SCROLL_STATE_SETTLING->{
                        mIsScrolled=true
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if(position ==docData!!.item.pictures.size){
                    imageButton!!.visibility=View.INVISIBLE
                    return
                }
                imageButton!!.visibility=View.VISIBLE
                val pic =docData!!.item.pictures[position]
                textView10.text=pic.img_width.toString()+"*"+pic.img_height
                textView13.text =(pager.currentItem+1).toString()+"/"+docData!!.item.pictures.size

            }
        })

    }

    //抖动
    @SuppressLint("ObjectAnimatorBinding")
    fun shakeView(v:View){
        val width =v.width/10f
        val animator = ObjectAnimator.ofFloat(v,"translationX",0f,
            width, -width, width, -width, width, -width,
            width, -width, width, -width, width,0f)
        animator.duration=500
        animator.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 6) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                adapter!!.download(pager.currentItem,this)
            } else {
                Toast.makeText(this, "没读写权限我太难了！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkReadWrite(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun showReply(){
        //============================================================================================评论窗口
        var replyAdapter:SuperRecyclerAdapter<Reply>?=null
        val bottomSheetDialog =BottomSheetDialog(this)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(R.layout.dialog_bottom_recyclerview)
        val replyLayoutManager=LinearLayoutManager(this)
        val says =bottomSheetDialog.findViewById<RecyclerView>(R.id.recycler_view)
        says!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(!recyclerView.canScrollVertically(1)){
                        if(replyAdapter!=null&&replyAdapter!!.data.size>600){
                            replyAdapter!!.data.removeAll(replyAdapter!!.data.subList(0,200))
                        }
                        viewModel!!.doNetReply(idArray!![current],false)
                    }
                }
            }
        })
        viewModel!!.liveReplyData.observe(this, Observer<List<Reply>> { replyData ->
            if(replyData.isEmpty())
                return@Observer
            if(replyAdapter==null){
                replyAdapter=object :SuperRecyclerAdapter<Reply>(replyData as ArrayList<Reply>){
                    override fun bindData(holder: SuperVHolder, position: Int) {
                        val bindData =data[position]

                        val name:TextView=holder.getView(R.id.textView22) as TextView
                        name.text =bindData.member.uname

                        holder.setText(bindData.content.message,R.id.textView20)
                        holder.setImageIco(bindData.member.avatar,R.id.icon)
                        val checkReply =holder.getView(R.id.textView21) as TextView
                        if(bindData.replies.isNullOrEmpty()){
                            checkReply.visibility=View.GONE
                            return
                        }
                        checkReply.visibility=View.VISIBLE
                        checkReply.text = "查看"+data[position].replies.size+"条评论"
                        checkReply.setOnClickListener {
                            val listPopupWindow =ListPopupWindow(it.context)
                            listPopupWindow.setAdapter(object :ArrayAdapter<Reply>(it.context,R.layout.item_reply_layout,bindData.replies){
                                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                    val layout =LayoutInflater.from(this@DocDetailActivity).inflate(R.layout.item_reply_layout,null)
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

                    override fun setLayout(viewType: Int): Int {
                        return R.layout.item_reply_layout
                    }
                }
                says.addItemDecoration(RecycleItemDecoration(this,1))
                says.layoutManager=replyLayoutManager
                says.adapter=replyAdapter
            }else{
                val oldSize =replyAdapter!!.data.size
                replyAdapter!!.data=replyData as ArrayList<Reply>
                replyAdapter!!.notifyItemInserted(oldSize-20)
            }
        })
        viewModel!!.doNetReply(idArray!![current],true)

        imageView10!!.setOnClickListener {
            if(replyAdapter==null||replyAdapter!!.data==null||replyAdapter!!.data.isEmpty()){
                val toast=Toast.makeText(this,"没有评论",Toast.LENGTH_SHORT)
                toast.show()
                toast.view.postDelayed({toast.cancel()},1000)

            }else{
                bottomSheetDialog.show()
            }
        }
        //============================================================================================评论窗口
    }

    companion object{

        fun startDocDetailActivity(context: Context,intArray: IntArray,id:Int) {
            val intent =Intent(context,DocDetailActivity::class.java)
            val bundle =Bundle()
            bundle.putIntArray("intArray",intArray)
            bundle.putInt("doc_id",id)
            intent.putExtra("param1",bundle)
            context.startActivity(intent)
        }
    }

    /**
     * 隐藏界面
     */
    override fun hideLayout(view: View?) {
        if(constraintLayout!!.visibility==View.VISIBLE){
            bottom_layout!!.animate().translationY((bottom_layout.height).toFloat()).setInterpolator(AccelerateInterpolator()).start()
            constraintLayout!!.animate().translationY((-constraintLayout.height).toFloat()).setInterpolator(AccelerateInterpolator()).start()
            constraintLayout!!.postDelayed({
                constraintLayout!!.visibility=View.INVISIBLE
            },500)
        }else{
            bottom_layout!!.animate().translationY(0f).setInterpolator(AccelerateInterpolator()).start()
            constraintLayout!!.visibility=View.VISIBLE
            constraintLayout!!.animate().translationY(0f).setInterpolator(AccelerateInterpolator()).start()
        }
    }

    override fun next(view: View?) {
        //view!!.animate().translationY(view.height.toFloat()).setInterpolator(AccelerateInterpolator()).start()
        current++
        if(current>= idArray!!.size)
            finish()
        else{
            viewModel!!.doNetGetDoc(idArray!![current])

        }
    }

    override fun up(view: View?) {
        //view!!.animate().translationY(-view.height.toFloat()).setInterpolator(AccelerateInterpolator()).start()
        current--
        if(current<0)
            finish()
        else{
            viewModel!!.doNetGetDoc(idArray!![current])
        }
    }
}
