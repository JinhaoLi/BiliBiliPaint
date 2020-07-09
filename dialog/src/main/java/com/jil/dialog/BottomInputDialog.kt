package com.jil.dialog

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialogpart_button_two_group.*


/**==============================
 *===============================
 *== @Date: 2020/7/9 9:29
 *== @author JIL
 *===============================
 *===============================
 **/
class BottomInputDialog(context: Context) : BottomSheetDialog(context,R.style.BottomSheetStyle){
    var title:String ="无标题"
    private val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_input,null)
    lateinit var mClickListener:(view:View,input:String)->Unit
    var canClick =false
    fun setClickListener(listener:(view:View,input:String)->Unit){
        canClick=true
        mClickListener=listener
    }
    init {
        setContentView(rootView)
        window?.findViewById<View>(R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        enter.setOnClickListener {
            enter.isClickable=false
            if (canClick) {
                val inputEdit = super.findViewById<EditText>(R.id.edit)
                mClickListener.invoke(it, inputEdit!!.text.toString())
            }
            enter.isClickable=true
        }
        cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun setTitle(title: CharSequence?) {
        findViewById<TextView>(R.id.title)?.setText(title)
    }

    fun setIcon(res:Int){
        findViewById<ImageView>(R.id.icon)?.setImageResource(res)
    }

    fun setHeight(){
        val mDialogBehavior = BottomSheetBehavior.from<View>(rootView.parent as View)
        mDialogBehavior.peekHeight = getWindowHeight()//dialog的高度
    }

    private fun getWindowHeight(): Int {
        val res: Resources = context.getResources()
        val displayMetrics: DisplayMetrics = res.getDisplayMetrics()
        return displayMetrics.heightPixels
    }

}