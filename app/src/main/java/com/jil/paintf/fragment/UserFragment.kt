package com.jil.paintf.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.adapter.ItemAdapter
import com.jil.paintf.adapter.UserListItemAdapter
import com.jil.paintf.repository.Doc
import com.jil.paintf.repository.DocX
import com.jil.paintf.repository.Item
import com.jil.paintf.viewmodel.UserViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_user.*

private const val ARG_PARAM1 ="param1"
private const val ARG_PARAM2 ="param2"
class UserFragment:LazyFragment(){
    var param=0
    var uid =0
    var adapter: UserListItemAdapter?=null
    var viewModel:UserViewModel?=null
    override fun loadAndObserveData() {
        viewModel!!.getDocListData(uid,param).observeForever {
            refresh(it.items)
        }
    }

    companion object{
        fun newInstance(int: Int,uid:Int):Fragment{
            return UserFragment().apply {
                arguments= Bundle().apply {
                    putInt(ARG_PARAM1,int)
                    putInt(ARG_PARAM2,uid)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater, container, R.layout.fragment_user)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            //arguments不为空则执行
            param = it.getInt(ARG_PARAM1)
            uid =it.getInt(ARG_PARAM2)
        }
        Logger.d(arguments?.getInt(ARG_PARAM1).toString()+"onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(adapter==null){
            viewModel=ViewModelProvider.AndroidViewModelFactory(activity!!.application).create(UserViewModel::class.java)
            adapter= UserListItemAdapter(context!!)
            val manager =GridLayoutManager(context,2)
            recyclerview!!.layoutManager =manager
            recyclerview!!.adapter=adapter
            recyclerview!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        if(!recyclerView.canScrollVertically(1)){
                            if(adapter!=null&&adapter!!.data.size>600){
                                adapter!!.data.removeAll(adapter!!.data.subList(0,200))
                            }
                            adapter!!.status="正在加载..."
                            viewModel!!.getDocListData(uid,param)
                        }
                    }
                }
            })

            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (adapter!!.isBottomView(position))
                        manager.spanCount
                    else 1
                }
            }
        }
    }


    private fun refresh(items: List<DocX>) {
        if(items.isEmpty()){
            adapter!!.status="已经没有了..."
        }
        adapter!!.data.addAll(items)
        adapter!!.notifyDataSetChanged()
    }


}