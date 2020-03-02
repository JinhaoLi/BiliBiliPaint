package com.jil.paintf.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.jil.paintf.R
import com.jil.paintf.adapter.MainPagerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_input.*


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    var adapter: MainPagerAdapter?=null
    var adapter2: MainPagerAdapter?=null
    var isIllust:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val recreate = savedInstanceState?.getBoolean("isRecreate") ?: false

        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar!!)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        adapter =MainPagerAdapter(supportFragmentManager,0)
        viewpager!!.adapter =adapter
        viewpager!!.currentItem=0

        adapter2 =MainPagerAdapter(supportFragmentManager,1)
        viewpager2!!.adapter =adapter2
        viewpager2!!.currentItem=0

        tab_title!!.setupWithViewPager(viewpager)

        val header=nav_view!!.getHeaderView(0)
        val ico =header.findViewById<ImageView>(R.id.imageView3)
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            getString("HEAD","").let {
                if(it!!.isNotEmpty())
                Glide.with(this@MainActivity).load(it)
                    .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this@MainActivity)))
                    .into(ico)
            }

            getString("NAME","").let {
                if(it!!.isNotEmpty())
                header.findViewById<TextView>(R.id.textView3).text=it
            }
        }


        ico!!.setOnClickListener {
            MaterialDialog(this).show {
                setContentView(R.layout.dialog_input)
                textView16!!.text="输入UID以设置你的头像"
                button!!.setOnClickListener {
                    editText!!.inputType=EditorInfo.TYPE_CLASS_NUMBER
                    ViewModelProvider.AndroidViewModelFactory(application)
                        .create(UserViewModel::class.java)
                        .getUserData(editText!!.text.toString().toInt()).observeForever { userData ->
                            PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                                .apply {
                                    edit().let {
                                        it.putString("HEAD",userData.face).apply()
                                        it.putString("NAME",userData.name).apply()
                                    }
                                }
                            Glide.with(this@MainActivity).load(userData.face)
                                .transform(GlideCircleWithBorder(2,ThemeUtil.getColorAccent(this@MainActivity)))
                                .into(ico)
                            header.findViewById<TextView>(R.id.textView3).text=userData.name
                        }

                    dismiss()
                }

            }
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


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_theme -> {
                startActivity(Intent(this, ThemeActivity::class.java))
            }

            R.id.nav_setting->{
                startActivity(Intent(this,SettingsActivity::class.java))
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
            R.id.action_settings -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
}

