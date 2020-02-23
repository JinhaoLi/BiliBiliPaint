package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.adapter.ImagePagerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.activity_doc_detail.*


class DocDetailActivity : AppCompatActivity(),ImagePagerAdapter.imageClickListener {
    //var adapter:DocDetailAdapter?=null
    var viewModel:DocViewModel? =null
    var adapter:ImagePagerAdapter<String>? =null
    var lock =false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_doc_detail)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        val bundle =intent.getBundleExtra("param1")
        var idArray=bundle!!.getIntArray("intArray")
        var docId =bundle.getInt("doc_id")
        var current =idArray!!.indexOf(docId)
        viewModel =ViewModelProvider.NewInstanceFactory().create(DocViewModel::class.java)

        viewModel!!.getData(docId).observeForever { docData ->
            val imageArray = arrayListOf<String>()
            docData.item.pictures.map {
                imageArray.add(it.img_src)
            }
            if(adapter==null){
                adapter = ImagePagerAdapter(imageArray)
                pager!!.adapter =adapter
                adapter!!.setListener(this)
            }else{
                adapter!!.ts=imageArray
                adapter!!.notifyDataSetChanged()
            }

            Glide.with(this).load(docData.user.head_url).placeholder(R.drawable.noface)
                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this)))
                .into(imageView5)
            textView4!!.text=docData.item.title
            textView5!!.text=docData.user.name
            textView6!!.text=docData.item.upload_time
        }

        floatingActionButton!!.setOnClickListener {
            current++
            if(current<=idArray.size)
            viewModel!!.getData(idArray[current])

        }
        imageView6!!.setOnClickListener{
            lock=!lock
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

    override fun onClick(view: View?) {
        if(!floatingActionButton!!.isClickable||lock){
            return
        }
        val layoutParams = floatingActionButton!!.layoutParams as CoordinatorLayout.LayoutParams
        if(floatingActionButton!!.visibility==View.VISIBLE){
            constraintLayout!!.animate().translationY((-constraintLayout.height).toFloat()).setInterpolator(AccelerateInterpolator()).start()
            floatingActionButton!!.animate().translationX(floatingActionButton.width + layoutParams.rightMargin.toFloat())
                .setInterpolator(AccelerateInterpolator()).start()
            floatingActionButton!!.isClickable=false
            floatingActionButton!!.postDelayed(Runnable {
                floatingActionButton!!.visibility=View.INVISIBLE
                floatingActionButton!!.isClickable=true
            },500)

        }else{
            constraintLayout!!.animate().translationY(0f).setInterpolator(AccelerateInterpolator()).start()
            floatingActionButton!!.animate().translationX(0f)
                .setInterpolator(AccelerateInterpolator()).start()
            floatingActionButton!!.visibility=View.VISIBLE
            floatingActionButton!!.isClickable=false
            floatingActionButton!!.postDelayed(Runnable {
                floatingActionButton!!.isClickable=true
            },500)

        }

    }
}
