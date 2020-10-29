package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.R
import com.jil.paintf.adapter.SearchItemAdapter
import com.jil.paintf.custom.NestedPopupListDialog
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.Result
import com.jil.paintf.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.item_add_tag_layout.*
import java.util.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar2)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel =ViewModelProvider.AndroidViewModelFactory(application).create(SearchViewModel::class.java)
        val cfg = resources.configuration
        val spanCount =if(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE)4 else 2
        val layoutManager =GridLayoutManager(this,spanCount)
        adapter = SearchItemAdapter(this)
        recycler_view.adapter =adapter
        recycler_view.layoutManager =layoutManager
        viewModel!!.searchDatas.observeForever {
            refresh(it.data.result as ArrayList<Result>)
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
                        search_edit!!.text.toString().let {
                            if(it.isNotEmpty())
                                viewModel!!.nextPage(it,pageCount,category)
                        }
                    }
                }
            }
        })
        intent.getStringExtra("param1")?.let {
            search_edit?.setText(it)
            search(it)
        }

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter!!.isBottomView(position))
                    layoutManager.spanCount
                else 1
            }
        }

        val illustsItem =object :NestedPopupListDialog.NestedPopupItem("插画"){
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                pageCount=1
                category=1
                nestedPopupListDialog.dismiss()
                categ!!.text ="插画"
                search(search_edit?.text.toString())
            }
        }
        val photoItem =object :NestedPopupListDialog.NestedPopupItem("摄影"){
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                pageCount=1
                category=2
                nestedPopupListDialog.dismiss()
                categ!!.text ="摄影"
                search(search_edit?.text.toString())
            }
        }
        categ.setOnClickListener {
            showChoosePopupDialog(illustsItem,photoItem)
        }
    }

    private fun showChoosePopupDialog(vararg item:NestedPopupListDialog.NestedPopupItem){
        val dialog =NestedPopupListDialog(this,categ,item,null)
        dialog.show()
    }

    private fun search(keyword:String){
        if(keyword.isEmpty()){
            return
        }
        if(adapter!=null){
            adapter!!.data.clear()
            adapter!!.notifyDataSetChanged()
        }
        viewModel!!.nextPage(keyword,pageCount,category)
    }

    private fun refresh(list: ArrayList<Result>){
        if(adapter==null){
            adapter = SearchItemAdapter(this)
        }
        if(list.isNullOrEmpty()){
            adapter!!.status="已经没有了..."
            adapter!!.notifyItemChanged(adapter!!.data.size)
        }else{
            adapter!!.data.addAll(list)
            adapter!!.notifyItemInserted(adapter!!.data.size)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                finish()
            }
            R.id.action_search -> {
                search(search_edit.text.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
