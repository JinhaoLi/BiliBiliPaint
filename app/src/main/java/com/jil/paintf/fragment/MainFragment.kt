package com.jil.paintf.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.adapter.ItemAdapter
import com.jil.paintf.repository.Item
import com.jil.paintf.viewmodel.MainFragmentViewModel
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.*


class MainFragment(private val type:Int): Fragment() {
    private var isRecovery: Boolean=false
    private lateinit var viewModel: MainFragmentViewModel
    private var adapter: ItemAdapter?=null
    var addAtStart =false
    var rootView:View? =null
    companion object {
        fun newInstance(type: Int) = MainFragment(type)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return initView(inflater,container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(isRecovery){
            return
        }
        viewModel =ViewModelProvider.AndroidViewModelFactory(activity!!.application).create(MainFragmentViewModel::class.java)
        when(type){
            0->{ recommend() }
            1->{ new() }
            2->{ hot() }
        }

        swiperefresh!!.setOnRefreshListener {

            addAtStart =true

            if(adapter!=null&&adapter!!.data.size>600){
                adapter!!.data.removeAll(adapter!!.data.subList(400,600))
            }
            when(type){
                0->{ viewModel.recommedData }
                1->{ viewModel.newdData }
                2->{ viewModel.hotData }
            }
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
                        when(type){
                            0->{ viewModel.recommedData }
                            1->{ viewModel.newdData }
                            2->{ viewModel.hotData }
                        }
                    }
                }
            }
        })
    }

    fun recommend(){
        viewModel.recommedData.observeForever {
            if(adapter==null){
                adapter = ItemAdapter(it as ArrayList<Item>, context!!)
                val manager = GridLayoutManager(context,2)
                recyclerview!!.layoutManager=manager
                recyclerview!!.adapter =adapter
            }else{
                if(addAtStart) {
                    adapter!!.data.addAll(0, it)
                    adapter!!.notifyItemRangeChanged(0,it.size)
                }else{
                    val updataPosition=adapter!!.data.size
                    adapter!!.data.addAll(it)
                    adapter!!.notifyItemInserted(updataPosition)
                }
            }
            swiperefresh!!.isRefreshing=false
        }
    }

    fun new(){
        viewModel.newdData.observeForever {
            if(adapter==null){
                adapter = ItemAdapter(it as ArrayList<Item>, context!!)
                val manager = GridLayoutManager(context,2)
                recyclerview!!.layoutManager=manager
                recyclerview!!.adapter =adapter
            }else{
                if(addAtStart)
                    adapter!!.data.addAll(0,it)
                else
                    adapter!!.data.addAll(it)
                adapter!!.notifyDataSetChanged()
            }
            swiperefresh!!.isRefreshing=false
        }
    }

    fun hot(){
        viewModel.hotData.observeForever {
            if(adapter==null){
                adapter = ItemAdapter(it as ArrayList<Item>, context!!)
                val manager = GridLayoutManager(context,2)
                recyclerview!!.layoutManager=manager
                recyclerview!!.adapter =adapter
            }else{
                if(addAtStart)
                    adapter!!.data.addAll(0,it)
                else
                    adapter!!.data.addAll(it)
                adapter!!.notifyDataSetChanged()
            }
            swiperefresh!!.isRefreshing=false
        }
    }

    private fun initView(inflater: LayoutInflater, container: ViewGroup?): View? {
        if (rootView == null) {
            isRecovery = false
            rootView = inflater.inflate(R.layout.main_fragment, container, false)
        } else {
            isRecovery = true
        }
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rootView !=null ) {
            (rootView!!.parent as ViewGroup).removeView(rootView)
        }
    }



}
