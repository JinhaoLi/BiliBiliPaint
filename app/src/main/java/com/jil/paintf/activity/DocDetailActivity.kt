package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.DocDetailAdapter

import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.fragment_recyclerview_layout.*


class DocDetailActivity : AppCompatActivity() {
    var adapter:DocDetailAdapter?=null
    var viewModel:DocViewModel? =null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_detail)
        val bundle =intent.getBundleExtra("param1")
        var idArray=bundle!!.getIntArray("intArray")
        var docId =bundle.getInt("doc_id")
        var current =idArray!!.indexOf(docId)
        viewModel =ViewModelProvider.NewInstanceFactory().create(DocViewModel::class.java)

        viewModel!!.getData(docId).observeForever {
            if(adapter==null){
                adapter = DocDetailAdapter(it, this)
                val manager = LinearLayoutManager(this)
                recyclerview!!.layoutManager=manager
                recyclerview!!.adapter =adapter
            }else{
                adapter!!.docData=it
                adapter!!.notifyDataSetChanged()
            }
        }

    }

    companion object{

        fun startDocDetailActivity(context: Context,intArray: IntArray,id:Int) {
            val intent =Intent(context,DocDetailActivity::class.java)
            val bundle =Bundle()
            bundle.putIntArray("intArray",intArray)
            bundle.putInt("doc_id",id)
            intent.putExtra("param1",bundle)
            context.startActivity(intent)
        }
    }
}
