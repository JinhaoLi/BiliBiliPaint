package com.jil.paintf.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jil.paintf.fragment.PreViewFragment

/**==============================
 *===============================
 *== @Date: 2020/7/5 0:15
 *== @author JIL
 *===============================
 *===============================
 **/

class PreViewFragmentAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {

    private var doc_ids:IntArray?=null

    override fun getItem(position: Int): Fragment {
        return PreViewFragment.newInstance(
            "",
            doc_ids!![position]
        )
    }

    override fun getCount(): Int {
        return if(doc_ids!=null){
            doc_ids!!.size
        }else{
            0
        }
    }

    fun changeData(doc_ids:IntArray) {
        this.doc_ids =doc_ids
        notifyDataSetChanged()
    }


}