package com.jil.paintf.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jil.dirpicker.DirPicker
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.SettingItem
import com.jil.paintf.custom.ThemeUtil
import com.jil.paintf.service.AppPaintF
import com.jil.paintf.service.AppPaintF.Companion.save_dir_path
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment :LazyFragment(){
    var adapter :SuperRecyclerAdapter<SettingItem>?=null


    override fun loadAndObserveData() {
    }


    fun picDescription(): String {
        return when(AppPaintF.LoadLevel){
            720->{
                "720P"
            }
            1080->{
                "1080p"
            }
            else->{
                "原始尺寸"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingList.clear()
        val animator =object :SettingItem("界面动画",if (AppPaintF.instance.enableAnimator)"启用" else "禁用",AppPaintF.instance.enableAnimator,4){
            override fun click(v: View?) {
                if(v is Switch){
                    isSwitchOpen=v.isChecked
                    if(v.isChecked){
                        description="启用"
                    }else{
                        description="禁用"
                    }
                    AppPaintF.instance.enableAnimator =v.isChecked
                    PreferenceManager.getDefaultSharedPreferences(v.context).edit().putBoolean("EnableAnimator", v.isChecked).apply()
                    adapter!!.notifyDataSetChanged()

                }
            }

        }
        val loadLevel =object:SettingItem("图片质量",picDescription(),2){
            override fun click(v: View?) {
                val checkItem=when(AppPaintF.LoadLevel){
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
                                    AppPaintF.LoadLevel=level

                                }
                            }
                        this.description=picDescription()
                        dialog.dismiss()
                        adapter!!.notifyDataSetChanged()
                    }
                builder.create().show()
            }
        }
        val selectDir =object:SettingItem("图片保存位置", save_dir_path,3){
            override fun click(v: View?) {
                if(checkReadWrite()){
                    DirPicker.instance(requireContext()).startWith(Environment.getExternalStorageDirectory().path)
                        .useThemeRes(ThemeUtil.themeResId(requireContext()))
                        .chooseDir().addListener(object : DirPicker.DirPickerListener{
                            override fun onChoose(path: String) {
                                PreferenceManager.getDefaultSharedPreferences(requireContext())
                                    .apply {
                                        edit().let {
                                            it.putString("SAVE_DIR",path).apply()
                                        }
                                    }
                                save_dir_path =path
                                description=path
                                adapter!!.notifyDataSetChanged()

                            }
                        }).open()
                }else{
                    //进行授权
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        CAN_SELECT_DIR
                    )
                }
            }
        }
        val staggered =object:SettingItem("布局",if (AppPaintF.instance.stagger)"瀑布流布局" else "表格布局",AppPaintF.instance
            .stagger,5){
            override fun click(v: View?) {
                if(v is Switch){
                    isSwitchOpen=v.isChecked
                    if(v.isChecked){
                        description="瀑布流布局"
                    }else{
                        description="表格布局"
                    }
                    AppPaintF.instance.stagger =v.isChecked
                    PreferenceManager.getDefaultSharedPreferences(v.context).edit().putBoolean("Stagger", v.isChecked).apply()
                    adapter!!.notifyDataSetChanged()

                }
            }

        }

        settingList.add(loadLevel)
        settingList.add(selectDir)
        settingList.add(animator)
        settingList.add(staggered)
    }

    private fun checkReadWrite(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater,container,R.layout.fragment_setting)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(adapter==null){
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
            //recyclerview.addItemDecoration(RecycleItemDecoration(requireContext(),1))
            recyclerview!!.layoutManager=LinearLayoutManager(requireContext())
        }


    }

    companion object{
        @JvmStatic
        val CAN_SELECT_DIR = 6
        @JvmStatic
        val settingList = arrayListOf<SettingItem>()


        fun newInstance()= SettingFragment()
    }


}