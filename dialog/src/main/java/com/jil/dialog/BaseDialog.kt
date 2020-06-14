package com.jil.dialog

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

abstract class BaseDialog(context: Context, layoutId:Int) : DialogInterface, Dialog(context){
    private var viewGroup:ViewGroup?=null
    @SuppressLint("InflateParams")
    private val headerView =LayoutInflater.from(context).inflate(R.layout.dialogpart_title_icon_text_group,null)
    private var contentView:ViewGroup= LayoutInflater.from(context).inflate(layoutId,null) as ViewGroup
    private val bottomView =LayoutInflater.from(context).inflate(R.layout.dialogpart_button_two_group,null)
    protected val enter:Button =bottomView.findViewById(R.id.enter)
    protected val cancel:Button =bottomView.findViewById(R.id.cancel)
    val buttonBuilder =ButtonBuilder()
    init {
        super.setContentView(R.layout.dialog_main)
        viewGroup = super.findViewById(R.id.linear)
        viewGroup!!.addView(headerView)
        viewGroup!!.addView(contentView)
        viewGroup!!.addView(bottomView)
        enter.setOnClickListener {
            dismiss()
        }
        cancel.setOnClickListener {
            dismiss()
        }

    }

    //抖动
    @SuppressLint("ObjectAnimatorBinding")
    fun shakeView(v:View){
        val width =v.width/10f
        val animator =ObjectAnimator.ofFloat(v,"translationX",0f,
            width, -width, width, -width, width, -width,
            width, -width, width, -width, width,0f)
        animator.duration=700
        animator.start()
    }

    final override fun setTitle(title: CharSequence?) {
        headerView.findViewById<TextView>(R.id.title).text = title
    }

    fun hideIcon():BaseDialog{
        headerView.findViewById<ImageView>(R.id.icon).visibility=View.GONE
        return this
    }

    fun setIcon(resId:Int):BaseDialog{
        headerView.findViewById<ImageView>(R.id.icon).setImageResource(resId)
        return this
    }

    final override fun <T : View?> findViewById(id: Int): T? {
        return contentView.findViewById<T>(id)
    }

    inner class ButtonBuilder{
        private val buttonList = arrayListOf<Button>()
        fun addButtonToBottom():ButtonBuilder{
            val group=bottomView.findViewById<ViewGroup>(R.id.bottom_add)
            val button =LayoutInflater.from(context).inflate(R.layout.base_button,null)
            group.addView(button)
            buttonList.add(button.findViewById(R.id.add_button))
            return this
        }

        fun setButtonName(name:String):ButtonBuilder?{
            buttonList[buttonList.size-1].text = name
            return this

        }

        fun setButtonAction(l:View.OnClickListener):ButtonBuilder{
            buttonList[buttonList.size-1].setOnClickListener(l)
            return this
        }
    }



    override fun dismiss() {
        super.dismiss()
    }

    override fun cancel() {
        super.cancel()
    }
}