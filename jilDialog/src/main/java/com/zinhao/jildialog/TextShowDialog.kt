package com.jil.dialog

import android.content.Context
import android.widget.TextView
import com.zinhao.jildialog.R

open class TextShowDialog(context: Context, msg:String,title:String?=null) :BaseDialog(context, layoutId =R.layout.base_text) {

    init {
        enter.setOnClickListener {
            enter.isClickable=false
            dismiss()
            enter.isClickable=true
        }
        super.findViewById<TextView>(R.id.text)!!.text = msg

        if(title.isNullOrEmpty())
            setTitle("")
        else
            setTitle(title)
    }


}