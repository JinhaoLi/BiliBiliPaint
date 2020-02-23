package com.jil.paintf.activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.jil.paintf.R
import com.jil.paintf.adapter.MainPagerAdapter
import com.jil.paintf.custom.ThemeUtil
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    var adapter: MainPagerAdapter?=null
    var isIllust:Boolean=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val recreate = savedInstanceState?.getBoolean("isRecreate") ?: false

        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar!!)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        adapter =MainPagerAdapter(supportFragmentManager)
        viewpager!!.adapter =adapter
        viewpager!!.currentItem=0

        tab!!.setupWithViewPager(viewpager)

        viewpager!!.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if(position>=3){
                    tablyout!!.selectTab(tablyout.getTabAt(1))
                }else{
                    tablyout!!.selectTab(tablyout.getTabAt(0))
                }
            }

        })
        nav_view!!.setNavigationItemSelectedListener(this)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_theme -> {
                startActivity(Intent(this, ThemeActivity::class.java))
            }
        }
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
}

