package com.jil.paintf.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jil.paintf.adapter.ItemAdapter
import com.jil.paintf.R
import com.jil.paintf.adapter.MainPagerAdapter
import com.jil.paintf.repository.Item
import com.jil.paintf.viewmodel.MainFragmentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_recyclerview_layout.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    var adapter: MainPagerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter =MainPagerAdapter(supportFragmentManager)
        viewpager!!.adapter =adapter
        viewpager!!.currentItem=0
        tab!!.setupWithViewPager(viewpager)

        var i =Intent(this,TestAc::class.java)
        startActivity(i)
    }

    private fun getRequest() { //1.创建OkHttpClient对象
        val url ="https://api.vc.bilibili.com/link_draw/v2/Doc/index?type=recommend&page_num=0&page_size=45"
        val okHttpClient = OkHttpClient()

        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        val request = Request.Builder().url(url).get().build()
        //3.创建一个call对象,参数就是Request请求对象
        val call =okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                print(response.code)
                val inputStream =response.body!!.byteStream()
                val fileOutputStream = FileOutputStream(File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"请求文件.json"))

                val bytes = ByteArray(10240)
                var len: Int

                while (inputStream.read(bytes).also { len = it } != -1) {
                    fileOutputStream.write(bytes, 0, len)
                    fileOutputStream.flush()
                }
            }

        })

    }
}
