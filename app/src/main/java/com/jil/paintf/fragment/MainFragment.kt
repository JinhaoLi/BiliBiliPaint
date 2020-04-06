package com.jil.paintf.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.adapter.ItemAdapter
import com.jil.paintf.repository.DataListRetryWithDelay.*
import com.jil.paintf.repository.Item
import com.jil.paintf.viewmodel.MainFragmentViewModel
import com.jil.paintf.viewmodel.MainFragmentViewModel.*
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_main.*

private const val ARG_PARAM1 = "param1"

class MainFragment: LazyFragment() {
    private var param1=0
    private lateinit var viewModel: MainFragmentViewModel
    private var adapter: ItemAdapter?=null
    var addAtStart =false
    companion object {
        fun newInstance(type: Int):MainFragment{
            return MainFragment().apply {
                arguments =Bundle().apply { putInt(ARG_PARAM1, type)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.d(arguments?.getInt("param1").toString()+"onResume()")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Logger.d(arguments?.getInt(ARG_PARAM1).toString()+"onCreateView()")
        return initView(inflater,container,R.layout.fragment_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //param1=arguments?.getInt(ARG_PARAM1)
        arguments?.let {
            //arguments不为空则执行
            param1 = it.getInt(ARG_PARAM1)
        }
        Logger.d(arguments?.getInt(ARG_PARAM1).toString()+"onCreate()")
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Logger.d(arguments?.getInt(ARG_PARAM1).toString()+"onActivityCreated()")
        if (adapter==null){
            viewModel =ViewModelProvider.AndroidViewModelFactory(activity!!.application).create(MainFragmentViewModel::class.java)
            adapter = ItemAdapter(context!!)
            val manager = GridLayoutManager(context,2)
            recyclerview!!.layoutManager=manager
            recyclerview!!.adapter =adapter
            swiperefresh!!.setOnRefreshListener {

                addAtStart =true

                if(adapter!=null&&adapter!!.data.size>600){
                    adapter!!.data.removeAll(adapter!!.data.subList(400,600))
                }
                //loadAndObserveData()
                viewModel.refresh(param1)
            }
            recyclerview!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(newState == RecyclerView.SCROLL_STATE_IDLE){
                        if(!recyclerView.canScrollVertically(1)){
                            addAtStart=false
                            if(adapter!=null&&adapter!!.data.size>600){
                                adapter!!.data.removeAll(adapter!!.data.subList(0,200))
                            }
                            //loadAndObserveData()
                            viewModel.refresh(param1)
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

    private fun hotCosplay() {
        viewModel.hotCosplayList.observeForever {
            refresh(it)
        }
    }

    private fun newCosplay() {
        viewModel.newCosplayList.observeForever {
            refresh(it)
        }
    }

    private fun recommendCosplay() {
        viewModel.recommendCosplayList.observeForever {
            refresh(it)
        }
    }

    private fun recommendIllusts(){
//        viewModel.recommendIllustsList.observeForever {
//            refresh(it)
//        }

        viewModel.recommendIllustsList.observe(this,
            Observer<List<Item>> {
                Logger.d("125-->viewModel.recommendIllustsList.observe(this,\n" +
                        "            Observer<List<Item>> {\n" +
                        "                Logger.d(\"\")\n" +
                        "                refresh(it)\n" +
                        "            })")
                refresh(it)
            })
    }

    private fun newIllusts(){
        viewModel.newIllustsList.observeForever {
            refresh(it)
        }
    }

    private fun hotIllusts(){
        viewModel.hotIllustsList.observeForever{
            refresh(it)
        }
    }

    private fun refresh(list: List<Item>){
        if(addAtStart) {
            adapter!!.data.addAll(0, list)
            adapter!!.notifyItemRangeChanged(0,list.size)
        }else{
            val updataPosition=adapter!!.data.size
            adapter!!.data.addAll(list)
            adapter!!.notifyItemInserted(updataPosition)
        }
        Logger.d("当前数据大小:"+adapter!!.data.size.toString()+"--添加条目："+list.size.toString())
        if(swiperefresh!!.isRefreshing)
        swiperefresh!!.isRefreshing=false
    }

    override fun loadAndObserveData() {
        when(param1){
            RI->{ recommendIllusts() }
            NI->{ newIllusts() }
            HI->{ hotIllusts() }
            RC->{ recommendCosplay() }
            NC->{ newCosplay() }
            HC->{ hotCosplay() }
        }
    }


}


