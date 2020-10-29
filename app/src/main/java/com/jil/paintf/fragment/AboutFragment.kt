package com.jil.paintf.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.RecycleItemDecoration
import com.jil.paintf.custom.SettingItem
import kotlinx.android.synthetic.main.fragment_setting.*

class AboutFragment :LazyFragment(){

    override fun loadAndObserveData() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aboutList.clear()
        aboutList.add(project)
//        aboutList.add(bilibili)
        val versionName =context!!.applicationContext
            .packageManager
            .getPackageInfo(context!!.packageName, 0).versionName

        val version = object :SettingItem("应用版本", versionName,4){
            override fun click(v: View?) {

            }
        }
        aboutList.add(version)
        aboutList.add(qqGroup)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater,container,R.layout.fragment_setting)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerview!!.adapter=object :SuperRecyclerAdapter<SettingItem>(aboutList){
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

        recyclerview!!.layoutManager=LinearLayoutManager(requireContext())
        recyclerview.addItemDecoration(RecycleItemDecoration(requireContext(),1))
        recyclerview!!.layoutManager=LinearLayoutManager(requireContext())
    }

    companion object{
        @JvmStatic
        val aboutList = arrayListOf<SettingItem>()


        @JvmStatic
        private val project = object :SettingItem("项目地址","https://github.com/JinhaoLi/BiliBiliPaint",1){
            override fun click(v: View?) {
                val uri = Uri.parse("https://github.com/JinhaoLi/BiliBiliPaint")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                ActivityCompat.startActivity(v!!.context,intent,null)
            }

        }
        @JvmStatic
        private val bilibili =object:SettingItem("关注开发者B站","https://space.bilibili.com/75965179",2){
            override fun click(v: View?) {
                val uri = Uri.parse("https://space.bilibili.com/75965179")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                ActivityCompat.startActivity(v!!.context,intent,null)
            }
        }

        @JvmStatic
        private val qqGroup =object :SettingItem("加入计算机柯学与忌术","494103622",3){
            override fun click(v: View?) {
                val groupNumber =ClipData.newPlainText(null,"494103622")?:return
                val manager =getSystemService(v!!.context,ClipboardManager::class.java)?:return
                manager.setPrimaryClip(groupNumber)
                Toast.makeText(v.context, "群号已复制到粘贴板！", Toast.LENGTH_SHORT).show()
            }
        }

        fun newInstance()= AboutFragment()
    }


}