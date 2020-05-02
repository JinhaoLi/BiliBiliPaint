package com.jil.paintf.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.gridItems
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.jil.paintf.R
import com.jil.paintf.adapter.MainPagerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    var adapter: MainPagerAdapter?=null
    var adapter2: MainPagerAdapter?=null
    var ico:ImageView?=null
    var header:View? =null
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar!!)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        viewModel=ViewModelProvider(this).get(MainViewModel::class.java)
        adapter =MainPagerAdapter(supportFragmentManager,0)
        viewpager!!.adapter =adapter
        viewpager!!.currentItem=0

        adapter2 =MainPagerAdapter(supportFragmentManager,1)
        viewpager2!!.adapter =adapter2
        viewpager2!!.currentItem=0

        tab_title!!.setupWithViewPager(viewpager)

        header=nav_view!!.getHeaderView(0)
        ico =header!!.findViewById<ImageView>(R.id.imageView3)

        viewModel.myInfoMutableLiveData.observe(this, Observer {
            changHeaderView(it.data.face,it.data.uname)
        })

        if(AppPaintF.instance.cookie!=null){
            viewModel.doNetMyInfo()
        }else{
            changHeaderView()
        }

        ico!!.setOnClickListener {
            startActivityForResult(Intent(this,LoginActivity::class.java),3)
        }

        main_tabs!!.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(main_tabs!!.selectedTabPosition==0){
                    viewpager!!.visibility= View.VISIBLE
                    viewpager2!!.visibility= View.GONE
                    tab_title!!.setupWithViewPager(viewpager)
                }else{
                    viewpager2!!.visibility= View.VISIBLE
                    viewpager!!.visibility= View.GONE
                    tab_title!!.setupWithViewPager(viewpager2)
                }
            }

        })

        nav_view!!.setNavigationItemSelectedListener(this)

        val toggle  = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()
        drawer_layout.addDrawerListener(toggle)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==3){
            if(resultCode==Activity.RESULT_OK&&data!=null){
                viewModel.doNetMyInfo()
            }
            if(resultCode==-2){
                //代表退出登录
                changHeaderView()
            }
            if(resultCode==Activity.RESULT_CANCELED){
                //没有进行操作
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 左滑
     * default icoUrl = https://static.hdslb.com/images/akari.jpg
     * default name = 未登录
     */
    private fun changHeaderView(icoUrl: String="https://static.hdslb.com/images/akari.jpg", name: String="未登录"): Unit {
        Glide.with(this@MainActivity).load(icoUrl)
            .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this@MainActivity)))
            .into(ico!!)
        header!!.findViewById<TextView>(R.id.textView3).text=name
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_theme -> {
                val items = listOf(
                    ThemeActivity.BackgroundGridItem(R.color.colorPrimary, "Primary"),
                    ThemeActivity.BackgroundGridItem(R.color.pink, "pink"),
                    ThemeActivity.BackgroundGridItem(R.color.green, "green"),
                    ThemeActivity.BackgroundGridItem(R.color.purple, "purple"),
                    ThemeActivity.BackgroundGridItem(R.color.tea, "tea"),
                    ThemeActivity.BackgroundGridItem(R.color.walnut, "walnut"),
                    ThemeActivity.BackgroundGridItem(R.color.seaPine, "seaPine"),
                    ThemeActivity.BackgroundGridItem(R.color.seedling, "seedling")

                )
                MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    var action: (() -> Unit)? = null

                    title(R.string.change_theme)
                    gridItems(items) { _, index, item ->
                        //it.summary = item.title
                        PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                            .apply { edit().putInt("THEME", index).apply()}

                        action = {
                            AppPaintF.ActivityCollector.recreate()
                        }
                    }
                    onDismiss { action?.invoke() }
                    cornerRadius(16.0F)
                    negativeButton(android.R.string.cancel)
                    positiveButton(R.string.apply)
                    lifecycleOwner(this@MainActivity)
                }
                //startActivity(Intent(this, ThemeActivity::class.java))
            }

            R.id.nav_setting->{
                startActivity(Intent(this,SettingsActivity::class.java))
            }
            else->{

            }

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isRecreate",true)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}

