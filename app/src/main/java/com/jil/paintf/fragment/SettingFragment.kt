package com.jil.paintf.fragment

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.jil.dialog.InputDialog
import com.jil.dialog.Only
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.GlideCircleWithBorder
import com.jil.paintf.custom.RecycleItemDecoration
import com.jil.paintf.custom.SettingItem
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.service.AppPaintf
import com.jil.paintf.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment :LazyFragment(){
    var adapter :SuperRecyclerAdapter<SettingItem>?=null
    override fun loadAndObserveData() {
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater,container,R.layout.fragment_setting)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(adapter==null){
            uid.description=PreferenceManager.getDefaultSharedPreferences(requireContext()).getInt("UID",8888888).toString()
            adapter=object :SuperRecyclerAdapter<SettingItem>(settingList){
                override fun bindData(holder: SuperVHolder, position: Int) {
                    holder.setText(data[position].name,R.id.textView2)
                    if (data[position].description != null && data[position].description != "") {
                        holder.setText(data[position].description,R.id.textView)
                    } else {
                        holder.getView(R.id.textView).visibility = View.GONE
                    }
                    val isSwitch = data[position].itemLayout == R.layout.item_setting_switch_layout
                    val cuView = holder.getView(R.id.textView2)
                    if (isSwitch) {
                        val sw = cuView as Switch
                        sw.isChecked = data[position].isSwitchOpen
                    }
                    holder.itemView.setOnClickListener {
                        if (isSwitch) {
                            val sw = cuView as Switch
                            sw.isChecked = !sw.isChecked
                            data[position].click(sw)
                        } else {
                            data[position].click(cuView)
                        }
                    }

                }

                override fun getItemViewType(position: Int): Int {
                    return data[position].itemLayout
                }

                override fun setLayout(viewType: Int): Int {
                    if(viewType==0){
                        return R.layout.item_setting_normal_layout
                    }else{
                        return R.layout.item_setting_switch_layout
                    }
                }
            }
            recyclerview!!.adapter=adapter
            //设置分隔线
            //设置分隔线
            recyclerview.addItemDecoration(RecycleItemDecoration(requireContext(),1))
            recyclerview!!.layoutManager=LinearLayoutManager(requireContext())
        }


    }

    companion object{
        @JvmStatic
        val settingList = arrayListOf<SettingItem>()

        @JvmStatic
        val uid = object :SettingItem("你的uid","0000000",1){
            override fun click(v: View?) {
                val inputDialog =object :InputDialog(v!!.context,hint ="你的uid" ){
                    override fun inputEnterClick(input: String) {
                        if(input.isEmpty()){
                            errInput("不能为空")
                            return
                        }
                        PreferenceManager.getDefaultSharedPreferences(v!!.context)
                            .apply {
                                edit().let {
                                    it.putInt("UID",input.toInt()).apply()
                                }
                            }
                        ViewModelProvider.AndroidViewModelFactory(AppPaintf.APP as Application)
                            .create(UserViewModel::class.java)
                            .getUserData(input.toInt()).observeForever { userData ->
                                PreferenceManager.getDefaultSharedPreferences(v.context)
                                    .apply {
                                        edit().let {
                                            it.putString("HEAD",userData.data.face).apply()
                                            it.putString("NAME",userData.data.name).apply()
                                        }
                                    }
                            }

                        dismiss()
                    }

                }
                inputDialog.applyInputType(Only.number)
                inputDialog.setIcon(R.mipmap.ic_launcher).setTitle("设置你的uid")
                inputDialog.buttonBuilder.addButtonToBottom().setButtonName("Action")!!
                    .setButtonAction(View.OnClickListener { v -> Toast.makeText(v.context, "action", Toast.LENGTH_SHORT).show() })

                inputDialog.hideIcon()
                inputDialog.show()
            }

        }
        @JvmStatic
        val loadLevel =object:SettingItem("图片质量","选择图片质量",2){
            override fun click(v: View?) {
                val checkItem=when(AppPaintf.LoadLevel){
                    720->{
                        0
                    }
                    1080->{
                        1
                    }
                    else->{
                        2
                    }
                }
                val builder =AlertDialog.Builder(v!!.context).setTitle("选择图片质量")
                    .setSingleChoiceItems(R.array.reply_entries,checkItem) { dialog, which ->
                        val level: Int = when(which){

                            1->{
                                1080
                            }
                            2->{
                                5000
                            }
                            else->{
                                720
                            }
                        }
                        PreferenceManager.getDefaultSharedPreferences(v.context)
                            .apply {
                                edit().let {
                                    it.putInt("LOAD_LEVEL",level).apply()
                                    AppPaintf.LoadLevel=level
                                }
                            }
                        dialog.dismiss()
                    }
                builder.create().show()
            }

        }

        @JvmStatic
        val test =object:SettingItem("测试","测试",false,3){
            override fun click(v: View?) {

            }

        }

        @JvmStatic
        val cookie =object :SettingItem("导入cookie","使用cookie模拟登录状态",4){
            override fun click(v: View?) {

            }

        }

        init {
            settingList.add(uid)
            settingList.add(loadLevel)
            settingList.add(test)
        }



        fun newInstance()= SettingFragment()
    }


}