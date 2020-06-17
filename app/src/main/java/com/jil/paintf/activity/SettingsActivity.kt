package com.jil.paintf.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.jil.dirpicker.DirPicker
import com.jil.paintf.R
import com.jil.paintf.adapter.SettingPagerAdapter
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.fragment.SettingFragment.Companion.CAN_SELECT_DIR
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAN_SELECT_DIR->{
                for (element in grantResults) {
                    if (element == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                        return
                    }
                }

                if(checkReadWrite()){
                    DirPicker.instance(this).startWith(Environment.getExternalStorageDirectory().path)
                        .useThemeRes(ThemeUtil.themeResId(this))
                        .chooseDir().addListener(object : DirPicker.DirPickerListener{
                            override fun onChoose(path: String) {
                                PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity)
                                    .apply {
                                        edit().let {
                                            it.putString(AppPaintF.SAVE_DIR,path).apply()
                                        }
                                    }
                                AppPaintF.save_dir_path =path
                                adapter!!.notifyDataSetChanged()
                            }
                        }).open()

                }
            }
        }
    }

    private fun checkReadWrite(): Boolean {
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }


}