package com.jil.paintf.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
import com.jil.dialog.TextShowDialog
import com.jil.paintf.R
import com.jil.paintf.adapter.MainPagerAdapter
import com.jil.paintf.custom.BackgroundGridItem
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var adapter: MainPagerAdapter
    lateinit var adapter2: MainPagerAdapter
    lateinit var ico: ImageView
    lateinit var header: View
    lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar!!)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        adapter = MainPagerAdapter(supportFragmentManager, 0)
        viewpager!!.adapter = adapter
        adapter2 = MainPagerAdapter(supportFragmentManager, 1)
        viewpager2!!.adapter = adapter2
        tab_title!!.setupWithViewPager(viewpager)
        header = nav_view!!.getHeaderView(0)
        ico = header.findViewById(R.id.imageView3)
        viewModel.myInfoMutableLiveData.observe(this, Observer {
            changHeaderView(it.data.face, it.data.uname)
        })
        if (AppPaintF.instance.csrf != null) {
            viewModel.doNetMyInfo()
        } else {
            changHeaderView()
        }
        ico.setOnClickListener {
            if (AppPaintF.instance.csrf == null) {
                startActivityForResult(Intent(this, LoginActivity::class.java), 3)
                return@setOnClickListener
            }
            MySelfActivity.startUserActivity(this, AppPaintF.instance.cookie!!.DedeUserID)
        }
        main_tabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (main_tabs!!.selectedTabPosition == 0) {
                    viewpager!!.visibility = View.VISIBLE
                    viewpager2!!.visibility = View.GONE
                    tab_title!!.setupWithViewPager(viewpager)
                } else {
                    viewpager2!!.visibility = View.VISIBLE
                    viewpager!!.visibility = View.GONE
                    tab_title!!.setupWithViewPager(viewpager2)
                }
            }

        })
        nav_view!!.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        floatingActionButton.setOnClickListener {
            if (main_tabs.selectedTabPosition == 0) {
                startActivity(Intent(this, UpLoadIllustActivity::class.java))
            } else if (main_tabs.selectedTabPosition == 1) {
                startActivity(Intent(this, UpLoadPhotoActivity::class.java))
            }

        }
        toggle.syncState()
        drawer_layout.addDrawerListener(toggle)
        if (AppPaintF.instance.FirstEntry) {
            object : TextShowDialog(this, getString(R.string.first_info), getString(R.string.first_tips)){
                override fun dismiss() {
                    super.dismiss()
                    AppPaintF.instance.FirstEntry=false
                    PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit().putBoolean("FirstEntry", false).apply()
                }
            }.setIcon(R.drawable.ic_info_outline_black_24dp).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                viewModel.doNetMyInfo()
            }
            if (resultCode == -2) {
                //代表退出登录
                changHeaderView()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 左滑
     * default icoUrl = https://static.hdslb.com/images/akari.jpg
     * default name = 未登录
     */
    private fun changHeaderView(
        icoUrl: String = getString(R.string.no_header_img_url),
        name: String = getString(R.string.unlogin)
    ) {
        Glide.with(this@MainActivity).load(icoUrl)
            .transform(GlideCircleWithBorder(2, ThemeUtil.getColorAccent(this@MainActivity)))
            .into(ico)
        header.findViewById<TextView>(R.id.textView3).text = name
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        main_tabs!!.selectTab(main_tabs!!.getTabAt(savedInstanceState.getInt("main_tabs", 0)))
        viewpager!!.currentItem = savedInstanceState.getInt("pager_select", 0)
        viewpager2!!.currentItem = savedInstanceState.getInt("pager2_select", 0)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_theme -> {
                val items = listOf(
                    BackgroundGridItem(R.color.colorPrimary, "Primary"),
                    BackgroundGridItem(R.color.pink, "pink"),
                    BackgroundGridItem(R.color.green, "green"),
                    BackgroundGridItem(R.color.purple, "purple"),
                    BackgroundGridItem(R.color.tea, "tea"),
                    BackgroundGridItem(R.color.walnut, "walnut"),
                    BackgroundGridItem(R.color.seaPine, "seaPine"),
                    BackgroundGridItem(R.color.seedling, "seedling")

                )
                MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    var action: (() -> Unit)? = null
                    title(R.string.change_theme)
                    gridItems(items) { _, index, item ->
                        //it.summary = item.title
                        PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                            .apply { edit().putInt("THEME", index).apply() }

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
            }

            R.id.nav_setting -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }

            R.id.nav_history -> {
                startActivity(Intent(this, LocationHistoryActivity::class.java))
            }

            R.id.nav_collection -> {
                if(AppPaintF.instance.csrf.isNullOrEmpty()){
                    Toast.makeText(this, "你还没有登录！", Toast.LENGTH_SHORT).show()
                }else{
                    startActivity(Intent(this, CollectionActivity::class.java))
                }

            }
            R.id.nav_black_uid->{
                startActivity(Intent(this, BlackListActivity::class.java))
            }
            else -> {}

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("main_tabs", main_tabs.selectedTabPosition)
        outState.putInt("pager_select", viewpager.currentItem)
        outState.putInt("pager2_select", viewpager2.currentItem)

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

