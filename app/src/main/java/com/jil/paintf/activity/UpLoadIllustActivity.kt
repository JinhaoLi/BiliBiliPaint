package com.jil.paintf.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.AddTagAdapter
import com.jil.paintf.adapter.UpLoadImgAdapter
import com.jil.paintf.custom.NestedPopupListDialog
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.UpLoadViewModel
import kotlinx.android.synthetic.main.activity_up_load.*
import kotlin.concurrent.thread

class UpLoadIllustActivity : AppCompatActivity() {
    var adapter:UpLoadImgAdapter?=null
    val addTagAdapter:AddTagAdapter = AddTagAdapter()
    lateinit var viewModel: UpLoadViewModel
    var category:String ="draw"
    var forbidden =0
    var type =1
    var categoryInt =4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_up_load)

        setSupportActionBar(toolbar5)
        title ="发布绘画"
        viewModel =ViewModelProvider(this).get(UpLoadViewModel::class.java)
        adapter= UpLoadImgAdapter(this)
        recyclerView.layoutManager=GridLayoutManager(this,3)
        recyclerView.adapter=adapter

        radioButton3.isChecked=true

        radioGroup1.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when(checkedId){
                    R.id.radioButton3->{
                        categoryInt=4
                    }
                    R.id.radioButton4->{
                        categoryInt=5
                    }
                    R.id.radioButton5->{
                        categoryInt=1
                    }
                }
            }

        })

        radioButton2.isChecked=true

        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when(checkedId){
                    R.id.radioButton->{
                        type=0
                    }
                    R.id.radioButton2->{
                        type=1
                    }
                }
            }

        })

        val operate: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("不设置转载权限") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("不设置转载权限")
                forbidden=0
                nestedPopupListDialog.dismiss()
            }
        }
        val operate1: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("开放授权-署名-非商用转载") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("开放授权-署名-非商用转载")
                forbidden=1
                nestedPopupListDialog.dismiss()
            }
        }
        val operate2: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("作者授权-署名-非商用转载") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("作者授权-署名-非商用转载")
                forbidden=2
                nestedPopupListDialog.dismiss()
            }
        }
        val operate3: NestedPopupListDialog.NestedPopupItem = object : NestedPopupListDialog.NestedPopupItem("禁止转载") {
            override fun doAction(nestedPopupListDialog: NestedPopupListDialog) {
                super.doAction(nestedPopupListDialog)
                textView27.setText("禁止转载")
                forbidden=3
                nestedPopupListDialog.dismiss()
            }
        }
        val list1 = arrayListOf<NestedPopupListDialog.NestedPopupItem>()
        list1.add(operate)
        list1.add(operate1)
        list1.add(operate2)
        list1.add(operate3)

        textView27.setOnClickListener {
            NestedPopupListDialog(this, textView27, list1).show()
        }
        button2.setOnClickListener{
            NestedPopupListDialog(this, textView27, list1).show()
        }
        tags.layoutManager=GridLayoutManager(this,3)
        tags.adapter =addTagAdapter
        viewModel.mutableLiveData.observe(this, Observer {
            if(it.code!=0){
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }else{
                adapter!!.addImage(it.data)
            }

        })

        floatingActionButton2.setOnClickListener {
            floatingActionButton2.isClickable=false
            if(!checkParam()){
                return@setOnClickListener
            }

            viewModel.doNetCreateDoc(1,categoryInt,type,title = editText2.text.toString()
                    ,description =editText3.text.toString(),copy_forbidden = forbidden,imgs = adapter!!.buildMap()
                    ,tags = addTagAdapter.map
            )

        }
        viewModel.createLiveData.observe(this, Observer {
            finish()
        })
    }



    private fun checkParam(): Boolean {
        if(AppPaintF.instance.cookie==null){
            Toast.makeText(this, "你还没登录", Toast.LENGTH_SHORT).show()
            floatingActionButton2.isClickable=true
            return false
        }
        val title=editText2.text.toString()
        if(title.isNullOrEmpty()){
            Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show()
            floatingActionButton2.isClickable=true
            return false
        }

        if(adapter==null||adapter!!.itemCount==1){
            Toast.makeText(this, "你还没添加选择作品", Toast.LENGTH_SHORT).show()
            floatingActionButton2.isClickable=true
            return false
        }

        if(type==1&&addTagAdapter.itemCount==1){
            Toast.makeText(this, "同人作品必须添加一个tag", Toast.LENGTH_SHORT).show()
            floatingActionButton2.isClickable=true
            return false
        }

        return true

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
