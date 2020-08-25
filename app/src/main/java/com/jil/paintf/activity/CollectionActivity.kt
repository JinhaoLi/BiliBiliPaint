package com.jil.paintf.activity

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.adapter.CollectionAdapter
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.viewmodel.CollectionViewModel
import kotlinx.android.synthetic.main.activity_collection.*

class CollectionActivity : AppCompatActivity() {
    private lateinit var adapter: CollectionAdapter
    private lateinit var viewModel:CollectionViewModel
    private lateinit var layoutManager:GridLayoutManager
    var pageCount = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_collection)
        setSupportActionBar(toolbar6)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel =ViewModelProvider(this).get(CollectionViewModel::class.java)
        adapter= CollectionAdapter(this)
        val cfg = resources.configuration
        val spanCount =if(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE)4 else 2
        layoutManager=GridLayoutManager(this,spanCount)
        recycler_view.adapter=adapter
        recycler_view.layoutManager=layoutManager
        viewModel.mutableLiveData.observe(this, Observer {
            if(it.data.list.isNullOrEmpty()){
                adapter.status="已经没有了..."
                adapter.notifyItemChanged(adapter.data.size)
            }else if(it.data.list.size<30){
                adapter.status="已经没有了..."
                adapter.data.addAll(it.data.list)
                adapter.notifyItemInserted(adapter.data.size)
            }else{
                adapter.data.addAll(it.data.list)
                adapter.notifyItemInserted(adapter.data.size)
            }
        })

        recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(!recyclerView.canScrollVertically(1)){
                        if(adapter.data.size>600){
                            adapter.data.removeAll(adapter.data.subList(0,200))
                        }

                        pageCount++
                        if(viewModel.doNetMCollection(pageCount)){
                            adapter.status="正在加载..."
                        }

                    }
                }
            }
        })

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.isBottomView(position))
                    layoutManager.spanCount
                else 1
            }
        }
        viewModel.doNetMCollection(pageCount)
    }
}
