package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.adapter.UserPagerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.UserOperateResult
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.CorrespondingValue
import com.jil.paintf.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    var adapter:UserPagerAdapter?=null
    var viewModel:UserViewModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        title=""
        val uid =intent.getIntExtra("uid",0)
        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(AppPaintF.instance.cookie==null){
                Toast.makeText(this, "你还没有登录！", Toast.LENGTH_SHORT).show()
                return@setOnCheckedChangeListener
            }
            if(isChecked){
                viewModel!!.joinAttentionList(uid)
            }else{
                viewModel!!.removeAttentionList(uid)
            }
            viewModel!!.mutableOpResult.observe(this, Observer<UserOperateResult> {
                viewModel!!.mutableOpResult.removeObservers(this)
                if (it.ttl==1&& it.code==0){
                    if(isChecked){
                        buttonView.text="已关注"
                    }else{
                        buttonView.text="关注"
                    }
                }else{
                    checkbox.isChecked=!isChecked
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()

                }

            })


        }


        viewModel=ViewModelProvider.AndroidViewModelFactory(application!!).create(UserViewModel::class.java)
        viewModel!!.getUserUpLoadInfo(uid).observeForever {
            var intArray  =IntArray(3)
            intArray[0] =it.data.all_count
            intArray[1] =it.data.draw_count
            intArray[2] =it.data.photo_count
            adapter =UserPagerAdapter(supportFragmentManager,uid,intArray)
            viewpager!!.adapter=adapter
            viewpager!!.currentItem=0
            tabs!!.setupWithViewPager(viewpager)
        }
        viewModel!!.getUserInfo(uid).observeForever {
            user_name.text =it.data.name
            //coll_toolbar__layout.title = it.data.name
            textView14.text=it.data.sign
            textView15.setBackgroundResource(CorrespondingValue.getLvBg(it.data.level))
            textView15.text="LV "+it.data.level
            checkbox.isChecked=it.data.is_followed
            Glide.with(this).load(it.data.face)
                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this))).into(imageView11)
            Glide.with(this).load(it.data.top_photo).into(user_bac)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu!!.add(1,1,1,"加入黑名单")
        menu.add(1,1,1,"移除黑名单")
        return super.onCreateOptionsMenu(menu)
    }

    companion object{
        fun startUserActivity(context: Context,uid: Int){
            val intent = Intent(context,UserActivity::class.java)
            intent.putExtra("uid",uid)
            context.startActivity(intent)
        }
    }
}
