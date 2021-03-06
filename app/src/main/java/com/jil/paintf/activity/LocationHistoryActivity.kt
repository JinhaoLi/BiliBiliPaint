package com.jil.paintf.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.HisItem
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.HistoryViewModel
import kotlinx.android.synthetic.main.activity_location_history.*


class LocationHistoryActivity : AppCompatActivity() {
    var adapter:SuperRecyclerAdapter<HisItem>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_location_history)
        setSupportActionBar(toolbar3)
        title ="本地历史记录"
        val viewModel =ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.mutableLiveData.observe(this, Observer {
            if(adapter==null){
                adapter =object :SuperRecyclerAdapter<HisItem>(it){
                    override fun bindData(holder: SuperVHolder, position: Int) {
//                        val params: ViewGroup.LayoutParams = holder.itemView.getLayoutParams()
//                        params.height = params.height + Random().nextInt(300)
//                        holder.itemView.setLayoutParams(params)
                        holder.setImage(data[position].image+"@512w_384h_1e.webp",R.id.imageView)
                        holder.setText(data[position].title,R.id.textView)
                        holder.itemView.setOnClickListener {
                            val bundle = Bundle()
                            val intent = Intent(this@LocationHistoryActivity, PreViewActivity::class.java)

                            val intArray =IntArray(data.size)
                            for (index in 0 until data.size){
                                intArray[index] =data[index].docId
                            }
                            bundle.putInt("doc_id",data[position].docId)
                            bundle.putIntArray("intArray",intArray)
                            intent.putExtra("param1",bundle)
                            if (AppPaintF.instance.enableAnimator){
                                val options1 =ActivityOptionsCompat.makeSceneTransitionAnimation(this@LocationHistoryActivity,holder.itemView,"mainimage")
                                ActivityCompat.startActivity(this@LocationHistoryActivity, intent, options1.toBundle())
                            }else{
                                PreViewActivity.startDocDetailActivity(this@LocationHistoryActivity,intArray,data[position].docId)
                            }
                        }
                        if(data[position].pageCount!=1)
                            holder.setText(data[position].pageCount.toString(),R.id.textView2)
                        else
                            holder.getView(R.id.textView2).visibility=View.INVISIBLE
                    }

                    override fun setLayout(viewType: Int): Int {
                        return if(AppPaintF.instance.stagger){
                            R.layout.staggered_item_doc_list_no_header_ico
                        }else{
                            R.layout.item_doc_list_no_header_ico
                        }

                    }
                }
                val cfg = resources.configuration
                val spanCount =if(cfg.orientation == Configuration.ORIENTATION_LANDSCAPE)4 else 2
//                recycleview.layoutManager= GridLayoutManager(this,spanCount)
                recycleview.layoutManager = StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
                recycleview.adapter=adapter
            }else{
                adapter!!.data=it
                adapter!!.notifyDataSetChanged()
            }

        })
        viewModel.doLoadHis()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.add(1,1,1,"清除历史记录")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            1->{
                val viewModel =ViewModelProvider(this).get(HistoryViewModel::class.java)
                viewModel.clearHis()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

    }
}
