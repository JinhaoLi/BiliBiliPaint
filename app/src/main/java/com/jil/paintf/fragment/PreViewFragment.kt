package com.jil.paintf.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jil.dialog.BottomInputDialog
import com.jil.paintf.R
import com.jil.paintf.activity.UserActivity
import com.jil.paintf.adapter.PreViewAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.NestedPopupListDialog
import com.jil.paintf.custom.RecycleItemDecoration
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.DocData
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.DocOperateModel
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.activity_up_load.*
import kotlinx.android.synthetic.main.fragment_pre_view.*
import kotlinx.android.synthetic.main.item_doc_detail.*
import kotlinx.android.synthetic.main.item_pre_header.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
const val WRITE_PERMISSION = 6

class PreViewFragment : LazyFragment() {
    private var param1: String? = null
    private var docId: Int = -1
    private var listener: OnFragmentInteractionListener? = null
    private var preAdapter: PreViewAdapter? = null
    private lateinit var viewModel: DocViewModel
    private lateinit var operateViewModel: DocOperateModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            docId = it.getInt(ARG_PARAM2)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(layoutInflater, container, R.layout.fragment_pre_view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(DocViewModel::class.java)
        operateViewModel = ViewModelProvider(requireActivity()).get(DocOperateModel::class.java)
        swiperefresh.setOnRefreshListener {
            loadAndObserveData()
        }
        floatingActionButton3.setOnClickListener {
            preAdapter?.mOperateReplyArt?.invoke(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION) {
            for (i in 1 until permissions.size) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(requireContext(), "没有读写权限无法下载", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
        if (willDownload != -1)
            preAdapter?.mOperateDownload?.invoke(willDownload)
    }

    //抖动
    @SuppressLint("ObjectAnimatorBinding")
    fun shakeView(v: View) {
        val width = v.width / 10f
        val animator = ObjectAnimator.ofFloat(
            v, "translationX", 0f,
            width, -width, width, -width, width, -width,
            width, -width, width, -width, width, 0f
        )
        animator.duration = 500
        animator.start()
    }

    private var willDownload = -1
    val docDataObserver = Observer<DocData> { docData ->
        if (docData.item.doc_id != docId) {
            return@Observer
        }
        if (swiperefresh.isRefreshing)
            swiperefresh.isRefreshing = false
        if (preAdapter == null) {
            preAdapter = PreViewAdapter(docData, viewModel, viewLifecycleOwner).also {
                it.setOperateVote {
                    shakeView(it)
                    if (!operateViewModel.voteResult.hasActiveObservers()) {
                        if (AppPaintF.instance.csrf == null) {
                            Toast.makeText(requireContext(), "你还没有登录！", Toast.LENGTH_SHORT).show()
                        } else {
                            var type = 1
                            if (docData.item.already_voted == 1)
                                type = 2
                            operateViewModel.voteResult.observe(this, Observer {
                                //点赞按钮
                                if (it.data.type == 1) {
                                    docData!!.item.already_voted = 1
                                    imageView8.setImageResource(R.drawable.ic_like)
                                } else if (it.data.type == 2) {
                                    docData!!.item.already_voted = 0
                                    imageView8.setImageResource(R.drawable.ic_no_vote_big)
                                } else {
                                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                                }
                                /**
                                 * 不理解！！！ post请求取消点赞时，会返回两次结果
                                 * 通过对比发现！
                                 * b站对已经点过赞的->先返回操作之前的状态->再进行操作之后的状态
                                 * 这里进行判断，只有得到正确的结果后才移除观察者
                                 */
                                if (it.data.type == type) {
                                    operateViewModel.voteResult.removeObservers(this)
                                }

                            })
                            operateViewModel.doNetVote(docId, type)
                        }

                    }

                }
                it.setOperateCollect {
                    shakeView(it)
                    if (!operateViewModel.removeFavResult.hasActiveObservers()) {
                        if (AppPaintF.instance.csrf == null) {
                            Toast.makeText(requireContext(), "你还没有登录！", Toast.LENGTH_SHORT).show()
                        } else {
                            var isCollection = false
                            if (docData!!.item.already_collected == 1)
                                isCollection = true
                            if (isCollection) {
                                operateViewModel.removeFavResult.observe(this, Observer {
                                    if (it.code == 0) {
                                        docData.item.already_collected = 0
                                        imageView9.setImageResource(R.drawable.ic_no_star)
                                        operateViewModel.removeFavResult.removeObservers(this)
                                    } else {
                                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                                operateViewModel.doNetDeleteFav(docId)
                            } else {
                                operateViewModel.favResult.observe(this, Observer {
                                    if (it.code == 0) {
                                        docData.item.already_collected = 1
                                        imageView9.setImageResource(R.drawable.ic_star)
                                        operateViewModel.favResult.removeObservers(this)
                                    } else {
                                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                                    }
                                })
                                operateViewModel.doNetAddFav(docId)
                            }

                        }

                    }
                }
                it.setOperateShare {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, "https://h.bilibili.com/$docId")
                    intent.type = "text/plain"
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "没有找到分享程序", Toast.LENGTH_SHORT).show()
                    }

                }
                it.setOperateDownload {
                    if (listener != null) {
                        if (listener!!.checkPermissionAndDownLoad()) {
                            preAdapter?.download(it, requireContext())
                        } else {
                            willDownload = it
                            Toast.makeText(requireContext(), "请授予权限之后再进行操作", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                it.setOperateArtWork {
                    UserActivity.startUserActivity(requireContext(), docData.user.uid)
                }
                it.setOprateVoteReply { reply, position ->
                    if (AppPaintF.instance.csrf == null) {
                        Toast.makeText(requireContext(), "你还没有登录！", Toast.LENGTH_SHORT).show()
                        return@setOprateVoteReply
                    }
                    val action = if (reply.action == 1) 0 else 1
                    operateViewModel.voteReplyLive.observe(viewLifecycleOwner, Observer {
                        reply.action = action
                        preAdapter!!.notifyItemChanged(position)
                        operateViewModel.voteReplyLive.removeObservers(viewLifecycleOwner)
                    })
                    operateViewModel.doNetVoteReply(reply.oid.toLong(), reply.type, reply.rpid, action)
                }
                it.setOperateReplyArt {
                    if (AppPaintF.instance.csrf == null) {
                    Toast.makeText(requireContext(), "请登录之后再进行操作", Toast.LENGTH_SHORT).show()
                    return@setOperateReplyArt
                }
                    AppPaintF.instance.csrf?.let { it1 ->
                        reply(docData.item.doc_id, 11
                            ,-1L,0L,1, it1
                        )
                    }
                }
                it.setOprateReply2 { reply, position, view ->
                    if (AppPaintF.instance.csrf == null) {
                        Toast.makeText(requireContext(), "请登录之后再进行操作", Toast.LENGTH_SHORT).show()
                        return@setOprateReply2
                    }
                    reply(reply.oid,11,reply.root,reply.rpid,1, AppPaintF.instance.csrf!!)
//                    val operate: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("回复") {
//                        override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
//                            super.doAction(nestedPopupListDialog)
//
//                            nestedPopupListDialog.dismiss()
//                        }
//                    }
//                    NestedPopupListDialog(requireContext(), view, listOf(operate)).show()
                }
            }
            recycler_page.adapter = preAdapter
            recycler_page.layoutManager = GridLayoutManager(requireContext(), 3).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return spanCount
                    }
                }
            }
            recycler_page.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        visibleFlag = true
                    } else if (dy < 0) {
                        visibleFlag = false
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val first_position = layoutManager.findFirstVisibleItemPosition()
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {//静止
                        if (!recyclerView.canScrollVertically(1)) {
                            //滑动到最下面的时候，界面处于静止
                            preAdapter!!.replyInit()
                        }
                    }

                    if (first_position == 0)
                        return
                    //向下滑动且可见
                    if (visibleFlag && !headerIsVisible) {
                        alpToView(header)
                        headerIsVisible = true
                    } else if (!visibleFlag && headerIsVisible) {
                        toAlpView(header)
                        headerIsVisible = false
                    }

                    val layoutParams: FrameLayout.LayoutParams =
                        floatingActionButton3.getLayoutParams() as FrameLayout.LayoutParams
                    val hideDistance = layoutParams.rightMargin + floatingActionButton3.width

                    if (first_position > docData.item.pictures.size + 1) {
                        floatingActionButton3.animate().translationX(0f).setInterpolator(LinearInterpolator()).start()
                    } else {
                        floatingActionButton3.animate().translationX(hideDistance.toFloat())
                            .setInterpolator(LinearInterpolator()).start()
                    }
                }
            })
            recycler_page.addItemDecoration(RecycleItemDecoration(requireContext(), 1))
        } else {
            preAdapter!!.refresh(docData)
        }

        docData.let {
            textView5.text = it.user.name
            textView4.text = it.item.title
            textView6.text = it.item.upload_time_text
            Glide.with(requireContext()).load(it.user.head_url)
                .transform(GlideCircleWithBorder(2, ThemeUtil.getColorAccent(requireContext()))).into(imageView5)
            imageView6.setOnClickListener { preAdapter!!.mOperateShare.invoke() }
            imageView5.setOnClickListener(preAdapter!!.mOperateArtWork)
        }
    }
    var headerIsVisible = true
    var visibleFlag = false

    @SuppressLint("ObjectAnimatorBinding")
    fun alpToView(v: View) {
        val animator = ObjectAnimator.ofFloat(v, "alpha", 0f, 0.25f, 0.75f, 1f)
        animator.duration = 500
        animator.start()
    }

    fun toAlpView(v: View) {
        val animator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0.75f, 0.25f, 0f)
        animator.duration = 500
        animator.start()
    }

    override fun onPause() {
        super.onPause()
        viewModel.data.removeObservers(viewLifecycleOwner)
        viewModel.liveReplyData.removeObservers(viewLifecycleOwner)
    }

    override fun loadAndObserveData() {
        viewModel.data.observe(viewLifecycleOwner, docDataObserver)
        viewModel.doNetGetDoc(docId)
    }

    fun reply(oid:Int ,type: Int , root:Long
              , parent:Long , plat:Int , csrf:String ){
        BottomInputDialog(requireContext()).apply {
            setClickListener { view, input ->
                operateViewModel.postReplyResult.observe(viewLifecycleOwner, Observer {
                    //Key: 'AddReplyReq.ReplyCommonReq.Type' Error:Field validation for 'Type' failed on the 'required' tag
                    if (it.code == 0) {
                        preAdapter?.addReply(it.data.reply)
                    } else {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    operateViewModel.postReplyResult.removeObservers(viewLifecycleOwner)
                })

                if(root ==-1L){
                    operateViewModel.doNetPostReplyArt(
                        oid, type
                        , input, plat, csrf
                    )
                }else{
                    operateViewModel.doNetPostReply(
                        oid, type, root,parent,input, plat, csrf
                    )
                }

                dismiss()
            }
            setTitle("请输入评论")
            setIcon(R.drawable.ic_folder_black_24dp)
        }.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (preAdapter != null) {
            preAdapter?.setOperateDownload { }
            preAdapter?.setOperateArtWork { }
            preAdapter?.setOperateVote { }
            preAdapter?.setOperateCollect { }
            preAdapter?.setOperateDownload { }
            preAdapter?.setOprateVoteReply { _, _ -> }
            preAdapter?.setOperateReplyArt { }
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(dx: Int, dy: Int)
        fun checkPermissionAndDownLoad(): Boolean
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, docId: Int) =
            PreViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, docId)
                }
            }
    }
}


