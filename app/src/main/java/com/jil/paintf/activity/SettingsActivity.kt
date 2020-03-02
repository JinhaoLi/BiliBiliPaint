package com.jil.paintf.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.viewpager.widget.PagerAdapter
import com.jil.paintf.R
import com.jil.paintf.adapter.SettingPagerAdapter
import com.jil.paintf.custom.ThemeUtil
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {
    var adapter:SettingPagerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.settings_activity)
        adapter = SettingPagerAdapter(supportFragmentManager)
        settingpager!!.adapter =adapter
        tabs!!.setupWithViewPager(settingpager)

    }

}