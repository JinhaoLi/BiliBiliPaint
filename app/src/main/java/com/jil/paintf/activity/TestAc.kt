package com.jil.paintf.activity

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.jil.paintf.R
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TestAc : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
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