package com.jil.paintf.fragment

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.activity.DocDetailActivity
import com.jil.paintf.activity.PreViewActivity
import com.jil.paintf.adapter.ItemAdapter
import com.jil.paintf.repository.Item
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.MainViewModel
import com.jil.paintf.viewmodel.MainViewModel.Companion.HC
import com.jil.paintf.viewmodel.MainViewModel.Companion.HI
import com.jil.paintf.viewmodel.MainViewModel.Companion.NC
import com.jil.paintf.viewmodel.MainViewModel.Companion.NI
import com.jil.paintf.viewmodel.MainViewModel.Companion.RC
import com.jil.paintf.viewmodel.MainViewModel.Companion.RI
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_main.*

private const val ARG_PARAM1 = "param1"

class MainFragment: LazyFragment() {
    var saveList:List<Item>? =null
    private var param1=0
    private var viewModel: MainViewModel?=null
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel?.onSaveData(param1,adapter!!.data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater,container,R.layout.fragment_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            //arguments不为空则执行
            param1 = it.getInt(ARG_PARAM1)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (adapter==null){
            viewModel =ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
            adapter = ItemAdapter(requireActivity())
            adapter!!.itemOnClickListener= AdapterView.OnItemClickListener { parent, view, position, id ->

                val bundle = Bundle()
                val intent = Intent(requireContext(), PreViewActivity::class.java)
                val intArray =IntArray(adapter!!.data.size)
                for (index in 0 until adapter!!.data.size){
                    intArray[index] =adapter!!.data[index].item.doc_id
                }
                bundle.putInt("doc_id",adapter!!.data[position].item.doc_id)
                bundle.putIntArray("intArray",intArray)
                intent.putExtra("param1",bundle)
                if (AppPaintF.instance.enableAnimator){
                    val options1 =ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),view,"mainimage")
                    ActivityCompat.startActivity(requireContext(), intent, options1.toBundle())
                }else{
                    PreViewActivity.startDocDetailActivity(requireContext(),intArray,adapter!!.data[position].item.doc_id)
                }

            }
            val cfg = resources.configuration
            val spanCount =if(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE)4 else 2
            val manager = GridLayoutManager(context,spanCount)

            recyclerview!!.layoutManager=manager
            recyclerview!!.adapter =adapter
            swiperefresh!!.setOnRefreshListener {

                addAtStart =true

                if(adapter!=null&&adapter!!.data.size>600){
                    adapter!!.data.removeAll(adapter!!.data.subList(400,600))
                }
                viewModel?.refresh(param1)
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
                            adapter!!.status="正在加载..."
                            viewModel?.refresh(param1)
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

            saveList= viewModel?.saveData?.get(param1)
            if(!saveList.isNullOrEmpty())
                refresh(saveList!!)
        }
    }

    private fun hotCosplay() {
        viewModel?.hotCosplayList?.observeForever {
            refresh(it)
        }
    }

    private fun newCosplay() {
        viewModel?.newCosplayList?.observe(this, Observer<List<Item>> {
            refresh(it)
        })
    }

    private fun recommendCosplay() {
        viewModel?.recommendCosPlayList?.observe(this, Observer<List<Item>> {
            refresh(it)
        })
    }

    private fun recommendIllusts(){
        viewModel?.recommendIllustsList?.observe(this, Observer<List<Item>> {
                refresh(it)
            })
    }

    private fun newIllusts(){
        viewModel?.newIllustsList?.observe(this, Observer<List<Item>> {
            refresh(it)
        })
    }

    private fun hotIllusts(){
        viewModel?.hotIllustsList?.observe(this, Observer<List<Item>> {
            refresh(it)
        })
    }

    private fun refresh(list: List<Item>){
        if(swiperefresh!=null&&swiperefresh.isRefreshing)
            swiperefresh?.isRefreshing=false
//        Logger.d("当前数据大小:"+adapter!!.data.size.toString()+"--添加条目："+list.size.toString())
        if(list.isNullOrEmpty()){
            adapter!!.status="已经没有了..."
            adapter!!.notifyItemChanged(adapter!!.data.size)
            return
        }
        val updataPosition=adapter!!.data.size
        if(addAtStart) {
            adapter!!.data.addAll(0, list)
            adapter!!.notifyItemRangeInserted(0,list.size)
            adapter!!.notifyItemRangeChanged(list.size,updataPosition)
            recyclerview!!.scrollToPosition(list.size)
        }else{
            adapter!!.data.addAll(list)
            adapter!!.notifyItemInserted(updataPosition)
        }

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
        if(saveList.isNullOrEmpty())
            viewModel?.refresh(param1)
    }
}


