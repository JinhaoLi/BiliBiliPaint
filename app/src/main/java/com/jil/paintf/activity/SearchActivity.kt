package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.adapter.SearchItemAdapter
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.Result
import com.jil.paintf.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.fragment_user.*
import java.util.ArrayList

class SearchActivity :AppCompatActivity(){

    companion object{
        fun startSearchActivity(context: Context,tag:String){
            val intent = Intent(context,SearchActivity::class.java)
            intent.putExtra("param1",tag)
            context.startActivity(intent)
        }
    }
    var category =0
    var viewModel :SearchViewModel?=null
    var pageCount =1
    var adapter: SearchItemAdapter?=null
    var keyword =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel =ViewModelProvider.AndroidViewModelFactory(application).create(SearchViewModel::class.java)
        val layoutManager =GridLayoutManager(this,2)
        adapter = SearchItemAdapter(this)
        recycler_view.adapter =adapter
        recycler_view.layoutManager =layoutManager
        viewModel!!.searchDatas.observeForever {
            refresh(it.data.result as ArrayList<Result>)
        }
        intent.getStringExtra("param1")?.let {
            keyword =it
            search_edit!!.setText(it)
            search()
        }
        recycler_view!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(!recyclerView.canScrollVertically(1)){
                        if(adapter!=null&&adapter!!.data.size>600){
                            adapter!!.data.removeAll(adapter!!.data.subList(0,200))
                        }
                        adapter!!.status="正在加载..."
                        pageCount++
                        viewModel!!.nextPage(keyword,pageCount,category)
                    }
                }
            }
        })

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.isBottomView(position))
                    layoutManager.spanCount
                else 1
            }
        }
        search!!.setOnClickListener{
            keyword =search_edit!!.text.toString()
            search()
        }

        spinner!!.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner.setSelection(1,true)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                pageCount=1
                if(position==0){
                    category=1
                    categ!!.text ="插画"
                }else{
                    category=2
                    categ!!.text ="摄影"
                }
            }

        }

    }

    private fun search(){
        if(keyword.isEmpty()){
            return
        }
        pageCount=1
        if(adapter!=null){
            adapter!!.data.clear()
            adapter!!.notifyDataSetChanged()
        }
        viewModel!!.nextPage(keyword,pageCount,category)
    }

    fun refresh(list: ArrayList<Result>){
        if(adapter==null){
            adapter = SearchItemAdapter(this)
        }
        adapter!!.data.addAll(list)
        adapter!!.notifyItemInserted(adapter!!.data.size)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
