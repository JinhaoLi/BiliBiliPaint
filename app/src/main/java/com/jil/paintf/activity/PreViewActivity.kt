package com.jil.paintf.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.jil.paintf.R
import com.jil.paintf.adapter.PreViewFragmentAdapter
import com.jil.paintf.custom.ImageSlideTransformer
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.fragment.PreViewFragment
import com.jil.paintf.fragment.WRITE_PERMISSION
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_pre_view.*

class PreViewActivity : AppCompatActivity(), PreViewFragment.OnFragmentInteractionListener {
    companion object{
        fun startDocDetailActivity(context: Context, intArray: IntArray, id:Int) {
            val intent = Intent(context,PreViewActivity::class.java)
            val bundle =Bundle()
            bundle.putIntArray("intArray",intArray)
            bundle.putInt("doc_id",id)
            intent.putExtra("param1",bundle)
            context.startActivity(intent)
        }
    }

    private lateinit var adapter: PreViewFragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.initTheme(this)
        setContentView(R.layout.activity_pre_view)
        adapter = PreViewFragmentAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        if(AppPaintF.instance.enableAnimator)
            page.setPageTransformer(false, ImageSlideTransformer())
        val viewmodel =ViewModelProvider(this).get(DocViewModel::class.java)
        intent?.let { intent ->
            page.currentItem =intent.getBundleExtra("param1")?.let {
                val list=it.getIntArray("intArray")?:return
                page.adapter =adapter

                adapter.changeData(list)
                val current = it.getInt("doc_id")
                page.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                    }

                    override fun onPageSelected(position: Int) {
                        viewmodel.resetReply()
                    }

                })
                list.indexOf(current)
            }?:return
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        super.onRestart()
    }

    private fun checkReadWrite(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onFragmentInteraction(dx: Int, dy: Int) {
    }

    override fun checkPermissionAndDownLoad(): Boolean {
        if(!checkReadWrite(this)){
            //进行授权
            if(!checkReadWrite(this)){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    WRITE_PERMISSION
                )
            }
            return false
        }else{
            return true
        }
    }
}
