package com.jil.paintf.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.jil.paintf.R
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.adapter.PreViewFragmentAdapter
import com.jil.paintf.fragment.PreViewFragment
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.activity_doc_detail.*
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

    override fun onFragmentInteraction(dx: Int, dy: Int) {
    }
}
