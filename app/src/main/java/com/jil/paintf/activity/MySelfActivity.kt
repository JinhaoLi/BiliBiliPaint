package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.jil.paintf.R
import com.jil.paintf.adapter.UserPagerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.repository.UserUpLoadInfo
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.CorrespondingValue
import com.jil.paintf.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_user.*

class MySelfActivity : AppCompatActivity() {
    var adapter:UserPagerAdapter?=null
    var viewModel:UserViewModel?=null
    var menu:Menu? =null
    var uid:Int=0
    var observer:Observer<UserUpLoadInfo>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_user)
        setSupportActionBar(toolbar)
        title=""
        uid =intent.getIntExtra("uid",0)

        viewModel=ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel!!.doNetBlackList()

        checkbox.visibility= View.GONE
        textView14.setOnClickListener {
            editText14.setText(textView14.text)
            editText14.visibility=View.VISIBLE
            editText14.isFocusable = true
            editText14.selectAll()
            editText14.isFocusableInTouchMode = true
            editText14.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        //用户作品
        observer=Observer{
            var intArray  =IntArray(3)
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
            Glide.with(this).load(it.data.face)
                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this))).into(imageView11)
            Glide.with(this).load(it.data.top_photo).into(user_bac)
        })
        viewModel!!.doNetUserInfo(uid)
        //========================================================================
        viewModel!!.mutableOpResult.observe(this, Observer {
            if(it.code==0)
                Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(AppPaintF.instance.csrf!=null){//没有登录的话就不显示菜单
            menu!!.add(1,1,1,"退出登录")
            this.menu=menu
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            1->{
              //退出登录操作
                startActivityForResult(Intent(this,LoginActivity::class.java),3)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 点击除editText外时，editText范围失去焦点
     *
     * @param ev
     * @return
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (checkTouchEvent(v, ev)) {
                //点击editText控件外部
                if(v!!.visibility==View.VISIBLE){

                    val result =editText14.text.toString()
                    if(textView14.text.toString()!=result){
                        viewModel!!.updateSign(result)
                        textView14.text = result
                    }

                    v.visibility=View.GONE
                    v.isFocusable=false
                    v.clearFocus()

                    //隐藏输入法
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
                    imm.hideSoftInputFromWindow(v.windowToken,0)
                }
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
    }

    /**
     *
     *  检查是否触碰v以外区域
     * @param v
     * @param
     * @return 当v是edittext且点击外部时返回true
     */
    private fun checkTouchEvent(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            //判断点击的范围
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    companion object{
        fun startUserActivity(context: Context,uid: Int){
            val intent = Intent(context,MySelfActivity::class.java)
            intent.putExtra("uid",uid)
            context.startActivity(intent)
        }
    }
}
