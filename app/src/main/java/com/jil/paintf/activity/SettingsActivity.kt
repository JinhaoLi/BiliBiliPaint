package com.jil.paintf.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.jil.paintf.R
import com.jil.paintf.adapter.SettingPagerAdapter
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.fragment.SettingFragment.Companion.REQUESTCODE_FROM_SELECT_DIR
import com.jil.paintf.service.AppPaintF
import kotlinx.android.synthetic.main.settings_activity.*


class SettingsActivity : AppCompatActivity() {
    var adapter:SettingPagerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        adapter = SettingPagerAdapter(supportFragmentManager)
        settingpager!!.adapter =adapter
        tabs!!.setupWithViewPager(settingpager)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_SELECT_DIR) {
                val path: String = data!!.getStringExtra("path")!!

                val lbm= LocalBroadcastManager.getInstance(this)
                val intent=Intent("ACTION_CHANGE_DIR")
                intent.putExtra("PATH",path)
                lbm.sendBroadcast(intent)
            }
        }
    }


}