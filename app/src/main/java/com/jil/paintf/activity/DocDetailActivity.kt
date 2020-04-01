package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.activity_doc_detail.*
import kotlinx.android.synthetic.main.item_doc_detail.*


class DocDetailActivity : AppCompatActivity(),ImagePagerAdapter.imageClickListener {

    var viewModel:DocViewModel? =null
    var adapter:ImagePagerAdapter<String>? =null
    private var lock =false
    var docData:DocData?=null
    var addReply =false
    var replyAdapter:SuperRecyclerAdapter<Reply>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)

        setContentView(R.layout.activity_doc_detail)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        val bundle =intent.getBundleExtra("param1")
        var idArray=bundle!!.getIntArray("intArray")
        var docId =bundle.getInt("doc_id")
        var current =idArray!!.indexOf(docId)
        viewModel =ViewModelProvider.NewInstanceFactory().create(DocViewModel::class.java)

        viewModel!!.getData(docId).observeForever { docData ->
            this.docData=docData
            val imageArray = arrayListOf<String>()
            docData.item.pictures.map {
                imageArray.add(it.img_src)
            }
            pager!!.currentItem=0
            if(adapter==null){
                adapter = ImagePagerAdapter(imageArray)
                pager!!.adapter =adapter
                adapter!!.setListener(this)
            }else{
                adapter!!.ts=imageArray
                adapter!!.notifyDataSetChanged()
            }
            //重设页码

            //获取评论
            viewModel!!.getReplyData(docData.item.doc_id,true)
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
        }
        //滑动效果
        //pager!!.setPageTransformer(false, ImageSlideTransformer())
        //下载
        imageButton.setOnClickListener{
            adapter!!.download(pager.currentItem,this)
        }
        //下一个插画
//        floatingActionButton!!.setOnClickListener {
//            current++
//            if(current>=idArray.size){
//                finish()
//            }else{
//                viewModel!!.getData(idArray[current])
//                viewModel!!.getReplyData(idArray[current],true)
//            }
//
//
//        }
        //分享
        imageView6!!.setOnClickListener{
            val intent =Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT,"https://h.bilibili.com/"+idArray[current])
            intent.type="text/plain"
            startActivity(intent)
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
                    pager.currentItem =docData!!.item.pictures.size-1
                    //======================下一个
                    current++
                    if(current>=idArray.size)
                        finish()
                    else{
                        viewModel!!.getData(idArray[current])
                        if(replyAdapter!=null)
                        replyAdapter!!.data.clear()
                    }
                    //=======================
                    return
                }
                val pic =docData!!.item.pictures[position]
                textView10.text=pic.img_width.toString()+"*"+pic.img_height
                textView13.text =(pager.currentItem+1).toString()+"/"+docData!!.item.pictures.size

            }
        })

        //============================================================================================评论窗口
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
                        addReply=true
                        viewModel!!.getReplyData(idArray[current],false)
                    }
                }
            }
        })
        viewModel!!.getReplyData(idArray[current],true).observeForever {
            if(it.replies==null||it.replies.isEmpty()){
                return@observeForever
            }

            if(replyAdapter==null){
                replyAdapter=object :SuperRecyclerAdapter<Reply>(it.replies as ArrayList<Reply>){
                    override fun bindData(holder: SuperVHolder, position: Int) {
                        holder.setText(data[position].content.message,android.R.id.text1)
                        holder.setImageIco(data[position].member.avatar,android.R.id.icon)
                    }

                    override fun setLayout(viewType: Int): Int {
                        return android.R.layout.activity_list_item
                    }


                }
                says.addItemDecoration(RecycleItemDecoration(this,1))
                says.layoutManager=replyLayoutManager
                says.adapter=replyAdapter
            }else{
                if(addReply){
                    replyAdapter!!.data.addAll(it.replies as ArrayList<Reply>)
                    replyAdapter!!.notifyItemInserted(replyAdapter!!.data.size)
                    addReply=false
                }else{
                    replyAdapter!!.data=it.replies as ArrayList<Reply>
                    replyAdapter!!.notifyDataSetChanged()
                }
            }
        }
//        floatingActionButton2.setOnClickListener {
//            if(replyAdapter==null||replyAdapter!!.data==null||replyAdapter!!.data.isEmpty()){
//                Toast.makeText(this,"没有评论",Toast.LENGTH_SHORT).show()
//            }else{
//                bottomSheetDialog.show()
//            }
//
//        }
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
    override fun onClick(view: View?) {
        hideLayout()
    }


    private fun hideLayout(){
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
}
