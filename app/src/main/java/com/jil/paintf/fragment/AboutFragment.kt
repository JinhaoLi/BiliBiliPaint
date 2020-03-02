package com.jil.paintf.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.LinearLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.custom.SettingItem
import kotlinx.android.synthetic.main.fragment_setting.*

class AboutFragment :LazyFragment(){

    override fun loadAndObserveData() {
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(inflater,container,R.layout.fragment_setting)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerview!!.adapter=object :SuperRecyclerAdapter<SettingItem>(aboutList){
            override fun bindData(holder: SuperVHolder, position: Int) {
                holder.setText(data[position].name,R.id.textView2)
                if (data[position].description != null && data[position].description == "") {
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

    }

    companion object{
        @JvmStatic
        val aboutList = arrayListOf<SettingItem>()

        @JvmStatic
        private val project = object :SettingItem("项目地址","",1){
            override fun click(v: View?) {

            }

        }
        @JvmStatic
        private val bilibili =object:SettingItem("关注开发者B站","",2){
            override fun click(v: View?) {

            }

        }

        init {
            aboutList.add(project)
            aboutList.add(bilibili)
        }

        fun newInstance()= AboutFragment()
    }


}