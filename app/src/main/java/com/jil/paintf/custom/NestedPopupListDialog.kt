package com.jil.paintf.custom

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import java.util.*
import kotlin.collections.ArrayList

/***
 *  多层不遮挡ListPopupWindow
 */
class NestedPopupListDialog(context: Context, val arch: View,var list:List<NestedPopupItem>, val parent: NestedPopupListDialog? =null): PopupWindow(context) {
    var mScreenWidth:Int=0
    var mScreenHeight:Int=0
    private lateinit var lv:ListView
    var xoff:Int=0
    var yoff:Int=0
    var itemLayout:Int=android.R.layout.simple_list_item_1

    //用于确定子view
    var nxoff:Int=0
    var nyoff:Int=0

    var flag: Int=1
    init {
        val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics= DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        mScreenWidth=outMetrics.widthPixels
        mScreenHeight=outMetrics.heightPixels
        if(parent!=null){
            flag=parent.flag+1
            xoff=parent.nxoff
            yoff=parent.nyoff
        }
        build(context)

    }

    private fun build(context: Context){
        contentView= ListView(context)
        width=arch.width
        height= LinearLayout.LayoutParams.WRAP_CONTENT
        isFocusable = true
        lv  = contentView as ListView
        lv.setBackgroundColor(Color.WHITE)
        lv.adapter=ArrayAdapter<NestedPopupItem>(context,itemLayout, list)
        setOnDismissListener {
            parent?.dismiss()
        }
        lv.setOnItemClickListener { p, view, position, id ->
            nxoff=GetXoff(view,flag,xoff)
            nyoff=GetYoff(view,position,yoff,list.size)
            list[position].doAction(this)
        }
    }

    fun show(){
        val location3 = IntArray(2)
        arch.getLocationOnScreen(location3)
        if (arch.y*2 >= mScreenHeight) {
            showAsDropDown(arch, xoff, yoff - arch.height - list.size * 84)
        } else
            showAsDropDown(arch, xoff, yoff)
    }

    private fun GetXoff(clickView: View, flag: Int, parentXoff:Int): Int {
        val location2 = IntArray(2)
        clickView.getLocationOnScreen(location2) //获取在整个屏幕内的绝对坐标
        if(parentXoff>0){
            if(location2[0]+clickView.width*2<=mScreenWidth)
                return clickView.width*flag
            else{
                Toast.makeText(clickView.context, "右边空间不足，无法弹出", Toast.LENGTH_SHORT).show()
                return clickView.width*(flag-2)
            }

        }else if(parentXoff<0){
            if(location2[0]-clickView.width>=0)
                return parentXoff-clickView.width
            else
                Toast.makeText(clickView.context, "左边空间不足，无法弹出", Toast.LENGTH_SHORT).show()
        }else{
            val location4 = IntArray(2)
            arch.getLocationOnScreen(location4)
            if(mScreenWidth<location4[0]*2){
                return -clickView.width*flag
            }else{
                return clickView.width*flag
            }
        }
        return -99999
    }

    fun GetYoff(clickView: View, position: Int, parentYoff: Int, itemCount: Int): Int {
        val location2 = IntArray(2)
        clickView.getLocationOnScreen(location2)
        if (parentYoff > 0) {
            if (location2[1]*2 <= mScreenHeight)
                return parentYoff + position * clickView.height
            else
                return parentYoff - (itemCount - position - 1) * clickView.height
        } else if (parentYoff < 0) {

            if (location2[1]*2 <= mScreenHeight)
                return parentYoff + position * clickView.height
            else {
                return parentYoff - (itemCount - position - 1) * clickView.height
            }
        } else {
            if (location2[1]*2 <= mScreenHeight)
                return parentYoff + position * clickView.height
            else
                return parentYoff - (itemCount - position - 1) * clickView.height
        }
    }


    abstract class NestedPopupItem(var title: String) {
        var menu: ArrayList<NestedPopupItem> = ArrayList()
        override fun toString(): String {
            return title
        }

        open fun doAction(nestedPopupListDialog: NestedPopupListDialog) {}

    }
}