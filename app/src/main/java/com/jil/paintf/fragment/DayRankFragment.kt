package com.jil.paintf.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jil.paintf.R
import com.jil.paintf.adapter.RankListAdapter
import com.jil.paintf.viewmodel.RankFragmentViewModel
import kotlinx.android.synthetic.main.fragment_week_rank.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [DayRankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DayRankFragment : LazyFragment() {
    // TODO: Rename and change types of parameters
    private var param1: Int? = null
    private lateinit var viewmodel: RankFragmentViewModel
    private var adapter: RankListAdapter? =null
    override fun loadAndObserveData() {
        viewmodel.dayLiveData[param1!!].observe(this, {
            adapter = RankListAdapter(it.data.items)
            rank_list.adapter=adapter
            rank_list.layoutManager = LinearLayoutManager(requireContext())
        })
        if(param1==0)
            viewmodel.doNetPaintDay()
        else
            viewmodel.doNetCosDay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1, 0)
        }
        viewmodel = ViewModelProvider(requireActivity()).get(RankFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return initView(inflater, container, R.layout.fragment_day_rank)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DayRankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int) =
            DayRankFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}
