package com.jil.dialog

import android.content.Context
import android.graphics.Color
import android.widget.EditText

abstract class InputDialog(context: Context) : BaseDialog(context, layoutId=R.layout.base_edit) {
    private var hint=""
    private val inputEdit =super.findViewById<EditText>(R.id.edit)
    private val defaultC =inputEdit!!.hintTextColors
    init {
        enter.setOnClickListener {
            enter.isClickable=false
            inputEnterClick(inputEdit!!.text.toString())
            enter.isClickable=true
        }
    }

    constructor (context: Context,hint:String,default: String="") : this(context) {
        applyHint(hint)
        inputEdit!!.setText(default)
    }

    fun applyInputType(@Only inputType:Int):InputDialog{
        inputEdit!!.inputType=inputType
        return this
    }

    private fun applyHint(hint:String):InputDialog{
        this.hint=hint
        inputEdit!!.hint=this.hint
        return this
    }

    fun errInput(errMsg: String){
        inputEdit!!.hint=errMsg
        inputEdit.setHintTextColor(Color.RED)
//        val curTranslationX: Float = inputEdit.getTranslationX()
//        val width: Float = inputEdit.width /10f
//        val animator = ObjectAnimator.ofFloat(
//            inputEdit, "translationX", curTranslationX,
//            width, -width, width, -width, width, -width,
//            width, -width, width, -width, width,curTranslationX
//        )
//        animator.duration = 500
//        animator.start()

        shakeView(inputEdit)
        inputEdit.postDelayed({
            inputEdit.hint=hint
            inputEdit.setHintTextColor(defaultC)
        },1000)
    }

    abstract fun inputEnterClick(input:String)
}