package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.adapter.UserPagerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
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
        val uid =intent.getIntExtra("uid",0)
        viewModel=ViewModelProvider.AndroidViewModelFactory(application!!).create(UserViewModel::class.java)
        viewModel!!.getUserDoc(uid).observeForever {
            var intArray  =IntArray(3)
            intArray[0] =it.data.all_count
            intArray[1] =it.data.draw_count
            intArray[2] =it.data.photo_count
            adapter =UserPagerAdapter(supportFragmentManager,uid,intArray)
            viewpager!!.adapter=adapter
            viewpager!!.currentItem=0
            tabs!!.setupWithViewPager(viewpager)
        }
        viewModel!!.getUserData(uid).observeForever {
            user_name.text =it.data.name
            textView14.text=it.data.sign
            textView15.setBackgroundResource(CorrespondingValue.getLvBg(it.data.level))
            textView15.text="LV "+it.data.level
            Glide.with(this).load(it.data.face)
                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this))).into(imageView11)
            Glide.with(this).load(it.data.top_photo).into(user_bac)
        }

    }

    companion object{
        fun startUserActivity(context: Context,uid: Int){
            val intent = Intent(context,UserActivity::class.java)
            intent.putExtra("uid",uid)
            context.startActivity(intent)
        }
    }
}
