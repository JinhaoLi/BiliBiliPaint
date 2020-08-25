package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
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
import com.jil.paintf.repository.UserUpLoadInfo
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.CorrespondingValue
import com.jil.paintf.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    var adapter:UserPagerAdapter?=null
    var viewModel:UserViewModel?=null
    var menu:Menu? =null
    var uid:Int=0
    var flag:Int =1
    var observer:Observer<UserUpLoadInfo>?=null
    private var listener:CompoundButton.OnCheckedChangeListener?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        title=""
        uid =intent.getIntExtra("uid",0)

        viewModel=ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel!!.doNetBlackList()
        listener= CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if(AppPaintF.instance.csrf==null){
                    Toast.makeText(this@UserActivity, "你还没有登录！", Toast.LENGTH_SHORT).show()
                }else{
                    if(isChecked){
                        viewModel!!.joinAttentionList(uid)
                    }else{
                        viewModel!!.removeAttentionList(uid)
                    }
                }
            }
        checkbox.setOnCheckedChangeListener (listener)

        viewModel!!.mutableOpResult.observe(this, Observer<UserOperateResult> {
            /**
             * 存在bug！post返回两次结果
             * */
            //viewModel!!.mutableOpResult.removeObservers(this)
            if (it.ttl==1&& it.code==0){
                if(checkbox.isChecked){
                    checkbox.text="已关注"
                }else{
                    checkbox.text="关注"
                }
                Toast.makeText(this, "操作成功:"+it.message, Toast.LENGTH_SHORT).show()
            }else{
                if(checkbox.isChecked){
                    updateCheckNoListener(false)
                    checkbox.text="关注"

                }else{
                    updateCheckNoListener(true)
                    checkbox.text="已关注"
                }
                Toast.makeText(this, "操作失败:"+it.message, Toast.LENGTH_SHORT).show()
            }
        })

        //用户作品
        observer=Observer{
            val intArray  =IntArray(3)
            intArray[0] =it.data.all_count
            intArray[1] =it.data.draw_count
            intArray[2] =it.data.photo_count
            adapter =UserPagerAdapter(supportFragmentManager,uid,intArray)
            viewpager!!.adapter=adapter
            viewpager!!.currentItem=0
            tabs!!.setupWithViewPager(viewpager)
            viewModel!!.userUpLoadInfo.removeObserver{ observer }
        }
        viewModel!!.userUpLoadInfo.observe(this, observer!!)
        viewModel!!.doNetUserUpLoadInfo(uid)
        //========================================================================
        //用户信息
        viewModel!!.userInfo.observe(this, Observer {
            viewModel!!.userInfo.removeObservers(this)
            user_name.text =it.data.name
            textView14.text=it.data.sign
            textView15.setBackgroundResource(CorrespondingValue.getLvBg(it.data.level))
            textView15.text="LV "+it.data.level
            updateCheckNoListener(it.data.is_followed)
            Glide.with(this).load(it.data.face)
                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this))).into(imageView11)
            Glide.with(this).load(it.data.top_photo).into(user_bac)
        })
        viewModel!!.doNetUserInfo(uid)
        //========================================================================
        //黑名单校验
        viewModel!!.muBlackList.observe(this, Observer {
            initMenuState(uid)
        })
    }

    /**
     * 移除监听->修改状态->设置监听
     */
    private fun updateCheckNoListener(state: Boolean){
        checkbox.setOnCheckedChangeListener(null)
        checkbox.setText(if (state) "已关注" else "关注")
        checkbox.isChecked=state
        checkbox.setOnCheckedChangeListener(listener)
    }

    private fun switchMenuState(isB:Boolean){
        if(isB){
            menu?.getItem(0)?.isVisible = false
            menu?.getItem(1)?.isVisible = true
        }else{
            menu?.getItem(1)?.isVisible = false
            menu?.getItem(0)?.isVisible = true
        }
    }

    /**
     * 初始黑名单状态
     * 仅用于初始化
     * 后续调用此方法不安全
     */
    private fun initMenuState(uid: Int) {
        if(viewModel!!.checkUidInBlack(uid)){
            switchMenuState(true)
        }else{
            switchMenuState(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(AppPaintF.instance.csrf!=null){//没有登录的话就不显示菜单
            menu!!.add(1,1,1,"加入黑名单")
            menu.add(1,2,2,"移除黑名单")
            this.menu=menu
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel!!.mutableOpResult.observe(this,Observer<UserOperateResult>{
            viewModel!!.mutableOpResult.removeObservers(this)
            if (it.ttl==1&& it.code==0){
                if(item.itemId==1){
                    switchMenuState(true)
                    Toast.makeText(this, "已加入黑名单", Toast.LENGTH_SHORT).show()
                }else{
                    switchMenuState(false)
                    Toast.makeText(this, "已移除黑名单", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()
            }
        })
        when(item.itemId){
            1->{
                viewModel!!.joinToBlackList(uid)
            }
            2->{
                viewModel!!.removeBlackList(uid)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object{
        fun startUserActivity(context: Context,uid: Int){
            val intent = Intent(context,UserActivity::class.java)
            intent.putExtra("uid",uid)
            context.startActivity(intent)
        }
    }
}
