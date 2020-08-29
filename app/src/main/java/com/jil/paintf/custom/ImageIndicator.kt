package com.jil.paintf.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter

/**
 * 2020/8/24 22:45
 * @author JIL
 **/
class ImageIndicator(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    RecyclerView(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,-1)
    constructor(context: Context):this(context,null)
    private val HALF_DARK_COLOR =Color.argb(0, 0, 0, 0)
    private val IMAGE_SELECTED_COLOR = ThemeUtil.getColorPrimary(context)
    private var selectPosition = -1
    private var stateStopSelect =false
    private lateinit var viewPager: ViewPager

    fun bindViewPager(viewPager: ViewPager):ImageIndicator{
        this.viewPager =viewPager
        this.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (selectPosition != -1) {
                    if (layoutManager!!.findViewByPosition(selectPosition) != null)
                        layoutManager!!.findViewByPosition(selectPosition)!!.setBackgroundColor(HALF_DARK_COLOR)
                }
                selectPosition = position
                layoutManager!!.scrollToPosition(position)
                if (layoutManager!!.findViewByPosition(position) != null) {
                    layoutManager!!.findViewByPosition(position)!!.setBackgroundColor(IMAGE_SELECTED_COLOR)
                } else {
                    stateStopSelect = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (stateStopSelect && state == 0) {
                    try {
                        layoutManager!!.findViewByPosition(selectPosition)!!.setBackgroundColor(IMAGE_SELECTED_COLOR)
                    } catch (e: Exception) {
                        Log.e("设置选中颜色时发生的错误", e.message + "由于被设置的view不可见，view==null")
                    }
                    stateStopSelect = false
                }
            }

        })
        return this
    }

    fun initCurrentItem(current:Int):ImageIndicator{
        this.selectPosition =current
        return this
    }

    /**
     *
     */
    fun bindPreViewData(list:List<String>){
        if(list.isNullOrEmpty())
            throw NullPointerException("list.isNullOrEmpty")

        adapter =object :SuperRecyclerAdapter<String>(list){
            override fun bindData(holder: SuperVHolder, position: Int) {
                holder.setImage(data[position],R.id.preview)
                holder.itemView.setOnClickListener {
                    viewPager.currentItem = position
                }
                if(selectPosition!=position)
                    holder.itemView.setBackgroundColor(HALF_DARK_COLOR);
                else
                    holder.itemView.setBackgroundColor(IMAGE_SELECTED_COLOR);
            }

            override fun setLayout(viewType: Int): Int {
                return R.layout.item_small_preview_indicator
            }
        }
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
    }
}