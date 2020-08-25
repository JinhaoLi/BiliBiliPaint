package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jil.paintf.R
import com.jil.paintf.adapter.ImagePagerAdapter
import kotlinx.android.synthetic.main.activity_scale_image.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val ScaleImageActivity_Param ="pic_list"
class ScaleImageActivity : AppCompatActivity() {
    companion object{
        fun startActivity(context: Context, list: List<String>){
            val i =Intent(context,ScaleImageActivity::class.java)
            i.putExtra(ScaleImageActivity_Param,list as ArrayList)
            context.startActivity(i)
        }
    }
    private lateinit var list: ArrayList<String>
    private lateinit var imageAdapter:ImagePagerAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_scale_image)
        list =intent.getStringArrayListExtra(ScaleImageActivity_Param)?:throw Exception("image list is NULL!")
        imageAdapter =ImagePagerAdapter(list)
        imageAdapter.setListener {
            if(preview.visibility== View.GONE){
                preview.visibility =View.VISIBLE
            }else if(preview.visibility== View.VISIBLE){
                preview.visibility =View.GONE
            }
        }
        image_pager.adapter=imageAdapter

        GlobalScope.launch {
            val preList = arrayListOf<String>()
            list.map {
                preList.add("$it@128w_128h.webp")
            }
            preview.bindViewPager(image_pager).bindPreViewData(preList)
        }

    }
}
