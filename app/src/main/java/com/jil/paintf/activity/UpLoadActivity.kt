package com.jil.paintf.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.AddTagAdapter
import com.jil.paintf.adapter.UpLoadImgAdapter
import com.jil.paintf.custom.NestedPopupListDialog
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.viewmodel.UpLoadViewModel
import kotlinx.android.synthetic.main.activity_up_load.*
import kotlin.concurrent.thread

class UpLoadActivity : AppCompatActivity() {
    var adapter:UpLoadImgAdapter?=null
    lateinit var viewModel: UpLoadViewModel
    var category:String ="draw"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_up_load)

        setSupportActionBar(toolbar5)
        title ="上传创作"
        viewModel =ViewModelProvider(this).get(UpLoadViewModel::class.java)
        adapter= UpLoadImgAdapter(this)
        recyclerView.layoutManager=GridLayoutManager(this,4)
        recyclerView.adapter=adapter

        val operate: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("不设置转载权限") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("不设置转载权限")
                nestedPopupListDialog.dismiss()
            }
        }
        val operate1: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("开放授权-署名-非商用转载") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("开放授权-署名-非商用转载")
                nestedPopupListDialog.dismiss()
            }
        }
        val operate2: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("作者授权-署名-非商用转载") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("作者授权-署名-非商用转载")
                nestedPopupListDialog.dismiss()
            }
        }
        val operate3: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("禁止转载") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("禁止转载")
                nestedPopupListDialog.dismiss()
            }
        }
        val list1 = arrayListOf<NestedPopupListDialog.NestedPopupItem>()
        list1.add(operate)
        list1.add(operate1)
        list1.add(operate2)
        list1.add(operate3)
        button2.setOnClickListener{
            NestedPopupListDialog(this, textView27, list1).show()
        }
        tags.layoutManager=GridLayoutManager(this,3)
        tags.adapter =AddTagAdapter()
        viewModel.mutableLiveData.observe(this, Observer {
            adapter!!.addImage(it.data.image_url)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==UpLoadImgAdapter.UPLOAD_CODE){
            if(resultCode==Activity.RESULT_OK){
                if(data!=null)
                    if (data.data!=null) {
                        thread {
                            val input = contentResolver.openInputStream(data.data!!) ?: return@thread
                            val byteArray = input.readBytes()
                            val temp: Array<String> = data.data!!.path!!.split(":".toRegex()).toTypedArray()
                            val fileName = temp[temp.size - 1]
                            viewModel.doNetUpLoad(byteArray,fileName,category)
                        }
                    }
            }
        }
    }
}
