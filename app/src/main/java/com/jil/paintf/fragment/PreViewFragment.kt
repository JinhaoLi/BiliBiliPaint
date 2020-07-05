package com.jil.paintf.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.jil.paintf.R
import com.jil.paintf.adapter.PreViewAdapter
import com.jil.paintf.custom.RecycleItemDecoration
import com.jil.paintf.repository.DocData
import com.jil.paintf.viewmodel.DocViewModel
import kotlinx.android.synthetic.main.fragment_pre_view.*
import kotlinx.android.synthetic.main.item_pre_header.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PreViewFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PreViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PreViewFragment : LazyFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var docId: Int = -1
    private var listener: OnFragmentInteractionListener? = null
    private var preAdapter: PreViewAdapter?=null
    private lateinit var viewModel: DocViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            docId = it.getInt(ARG_PARAM2)
        }

    }

//    Type checking has run into a recursive problem. Easiest workaround: specify types of your declarations explicitly
    val docDataObserver = Observer<DocData> {
//        Log.d("Paint", "picUrl=${it.item.pictures[0].img_src}\t docid=${it.item.doc_id}")
        if(it.item.doc_id!=docId){
            return@Observer
        }
        preAdapter =PreViewAdapter(it,viewModel,viewLifecycleOwner,recycler_page)
        recycler_page.adapter =preAdapter
        recycler_page.layoutManager =GridLayoutManager(requireContext(),3).apply {
            spanSizeLookup =object :GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return spanCount
                }
            }
        }

        it.let {
            textView5.text =it.user.name
            textView4.text =it.item.title
            textView6.text =it.item.upload_time_text
            Glide.with(requireContext()).load(it.user.head_url).into(imageView5)
            download.setOnClickListener{
                /*下载*/
            }
            imageView6.setOnClickListener {
                /*分享*/
            }
        }
        recycler_page.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy>0){
                    if(header!!.visibility== View.INVISIBLE){
                        header!!.visibility= View.VISIBLE
                        header!!.animate().translationY(0f).setInterpolator(
                            AccelerateInterpolator()
                        ).start()

                    }
                }else if (dy<0){
                    if(header!!.visibility== View.VISIBLE){
                        header!!.animate().translationY(-(header.height).toFloat()).setInterpolator(
                            AccelerateInterpolator()
                        ).start()
                        header!!.postDelayed({
                            header!!.visibility= View.INVISIBLE
                        },500)
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        recycler_page.addItemDecoration(RecycleItemDecoration(requireContext(),1))
    }

    override fun onPause() {
        super.onPause()
        viewModel.data.removeObservers(this)
        viewModel.liveReplyData.removeObservers(this)
    }

    override fun loadAndObserveData() {
        viewModel.data.observe(this,docDataObserver)
        viewModel.doNetGetDoc(docId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel =ViewModelProvider(requireActivity()).get(DocViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initView(layoutInflater, container, R.layout.fragment_pre_view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(dx: Int, dy: Int)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PreViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, docId: Int) =
            PreViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, docId)
                }
            }
    }
}

