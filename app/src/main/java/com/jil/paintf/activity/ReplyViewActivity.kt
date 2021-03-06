package com.jil.paintf.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.dialog.BottomInputDialog
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.OnDoubleClickListener
import com.jil.paintf.custom.RecycleItemDecoration
import com.jil.paintf.repository.Reply
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.CorrespondingValue
import com.jil.paintf.viewmodel.ReplyViewModel
import kotlinx.android.synthetic.main.activity_reply_view.*
import java.util.*

class ReplyViewActivity : AppCompatActivity() {
    var adapter: SuperRecyclerAdapter<Reply>?=null
    lateinit var viewMode:ReplyViewModel
    companion object{
        fun startActivity(context: Context,oid: Int, root: Long){
            val intent =Intent(context,ReplyViewActivity::class.java)
            intent.putExtra("oid",oid)
            intent.putExtra("root",root)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply_view)
        val oid =intent.getIntExtra("oid",-1)
        val root  = intent.getLongExtra("root",-1L)
        if(oid==-1)
            return
        viewMode =ViewModelProvider(this).get(ReplyViewModel::class.java)
        viewMode.reply2Data.observe(this, Observer {
            val _data =it.data.replies as ArrayList<Reply>?
            if(_data.isNullOrEmpty())
                return@Observer
            if(adapter==null){
                adapter =object :SuperRecyclerAdapter<Reply>(_data){
                    @SuppressLint("ClickableViewAccessibility")
                    override fun bindData(holder: SuperVHolder, position: Int) {
                        val reply =data[position]
                        holder.setImageIco(reply.member.avatar,R.id.icon)
                        holder.setText(reply.member.uname,R.id.textView22)
                        val textView:TextView = holder.getView(R.id.textView20) as TextView
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textView.text = CorrespondingValue.getHtmlFormte(reply.content.message)
                        }else{
                            textView.text =reply.content.message
                        }
                        textView.setOnTouchListener(OnDoubleClickListener(object :OnDoubleClickListener.DoubleClickCallback{
                            override fun onLongClick(): Boolean {
                                return true
                            }

                            override fun onDoubleClick(view: View?) {
                                voteReply(reply,position)
                            }

                        }))

                        holder.getView(R.id.textView21).visibility=View.GONE
                        val ico =holder.getView(R.id.icon) as ImageView
                        ico.setOnClickListener {
                            UserActivity.startUserActivity(this@ReplyViewActivity,reply.mid)
                        }

                        val doReply =holder.getView(R.id.imageView13) as ImageView
                        doReply.setOnClickListener {
                            if(AppPaintF.instance.csrf!=null)
                                reply(reply.oid,11,reply.root,reply.rpid,1, AppPaintF.instance.csrf!!,reply.member.uname)
                        }

                        val vote =holder.getView(R.id.imageView12) as ImageView
                        if(reply.action==1){
                            vote.setImageResource(R.drawable.ic_already_voted)
                        }else
                            vote.setImageResource(R.drawable.ic_no_voted)
                        vote.setOnClickListener {
                            voteReply(reply,position)
                        }
                    }

                    override fun setLayout(viewType: Int): Int {
                        return R.layout.item_pre_reply
                    }

                }
                listview.adapter=adapter
            }else{
                val oldSize =adapter!!.data.size
                adapter!!.data.addAll(_data)
                adapter!!.notifyItemInserted(oldSize)
            }

        })
        listview.layoutManager =LinearLayoutManager(this)
        listview.addItemDecoration(RecycleItemDecoration(this,1))
        listview.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val last_position = layoutManager.findLastVisibleItemPosition()
                if (last_position==adapter!!.itemCount-1){
                    viewMode.doNetReply2Data(oid,root)
                }
            }
        })

        viewMode.doNetReply2Data(oid,root)
    }

    fun voteReply(reply:Reply,position:Int){
        val action =if(reply.action==1) 0 else 1
        if(AppPaintF.instance.csrf!=null){
            viewMode.voteReplyLive.observe(this@ReplyViewActivity, Observer {
                viewMode.postReplyResult.removeObservers(this@ReplyViewActivity)
                if(it.code==0){
                    reply.action=action
                    adapter!!.notifyItemChanged(position)
                }else{
                    Toast.makeText(this@ReplyViewActivity, it.message, Toast.LENGTH_SHORT).show()
                }

            })
            viewMode.doNetVoteReply(reply.oid .toLong(),11,reply.rpid,action,AppPaintF.instance.csrf)
        }
    }

    fun reply(oid: Int, type: Int, root: Long, parent: Long, plat: Int, csrf: String, uname: String){
        BottomInputDialog(this).apply {
            setClickListener { view, input ->
                if(input.isEmpty()){
                    Toast.makeText(this@ReplyViewActivity, "回复不能为空", Toast.LENGTH_SHORT).show()
                    return@setClickListener
                }
                viewMode.postReplyResult.observe(this@ReplyViewActivity, Observer {
                    if (it.code == 0) {
                        val size =adapter?.data?.size!!
                        adapter?.data?.add((it.data.reply))
                        adapter!!.notifyItemInserted(size)
                    } else {
                        Toast.makeText(this@ReplyViewActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                    viewMode.postReplyResult.removeObservers(this@ReplyViewActivity)
                })
                viewMode.doNetPostReply(
                    oid, type, root,parent, "回复 @$uname：$input", plat, csrf
                )
                dismiss()
            }
            setTitle("回复 $uname")
            setIcon(R.drawable.ic_message_white)
        }.show()
    }
}
