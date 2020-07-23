package com.jil.paintf.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.SuperRecyclerAdapter
import com.jil.paintf.service.DataRoomService
import com.jil.paintf.viewmodel.BaseViewModel
import com.jil.paintf.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_black_list.*
import java.util.*
import kotlin.concurrent.thread

/**
 * 黑名单页面
 */
class BlackListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_black_list)
        val viewmodel =ViewModelProvider(this).get(BlackListViewModel::class.java)
        viewmodel.liveData.observe(this, Observer {
            black_list.adapter =object: SuperRecyclerAdapter<Int>(it as ArrayList<Int>){
                override fun bindData(holder: SuperVHolder, position: Int) {
                    holder.setText(data[position].toString(),R.id.textView31)
                    holder.getView(R.id.textView32).setOnClickListener {
                        viewmodel.removeBlackUid(data[position])
                        data.removeAt(position)
                        notifyDataSetChanged()
                    }
                }

                override fun setLayout(viewType: Int): Int {
                    return R.layout.item_black_list
                }

            }
            black_list.layoutManager=LinearLayoutManager(this)
        })
        viewmodel.loadBlackList()
    }

    class BlackListViewModel : BaseViewModel() {
        val liveData =MutableLiveData<List<Int>>()

        fun loadBlackList(){
            thread {
                liveData.postValue(DataRoomService.getDatabase().blackDao.loadAll())
            }
        }

        fun removeBlackUid(uid: Int){
            thread {
                DataRoomService.getDatabase().blackDao.deleteByUid(uid)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainViewModel.initBlackList()
    }
}
