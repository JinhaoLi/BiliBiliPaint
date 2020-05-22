package com.jil.paintf.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jil.paintf.repository.*
import com.jil.paintf.service.AppPaintF
import io.reactivex.functions.Consumer
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

class UpLoadViewModel : BaseViewModel() {
    val mutableLiveData = MutableLiveData<UpLoadResultLocation>()
    val createLiveData =MutableLiveData<ResponseBody>()

    fun doNetUpLoad(byteArray: ByteArray,fileName:String,category:String){
        if(AppPaintF.instance.cookie==null){
            //mutableLiveData.postValue(UpLoadResult(-1, DataXX(0,"还没登录",0)))
            return
        }
        val requestFile: RequestBody = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file_up",fileName,requestFile)
        val part1 = MultipartBody.Part.createFormData("category",category)
        val part2 = MultipartBody.Part.createFormData("csrf_token", AppPaintF.instance.cookie!!.bili_jct)
        val postList  = arrayListOf<MultipartBody.Part>()
        postList.add(part)
        postList.add(part1)
        postList.add(part2)
        RetrofitRepository.getInstance().postUpload(postList).subscribe({
            /**
             * 返回的数据中不包含contentLength，自行装箱
             */
            val upLoadResultLocation=UpLoadResultLocation(it.code,DataXXX(it.data.image_height,it.data.image_url,it.data.image_width,requestFile.contentLength())
                ,it.message)
            mutableLiveData.postValue(upLoadResultLocation)

        }, {
            if(!it.message.isNullOrEmpty()){
                mutableLiveData.postValue(UpLoadResultLocation(-3, DataXXX(0,"null",0,0),"请上传宽高>=420像素的图片"))
            }
        }).add()
    }

    fun doNetCreateDoc(biz: Int, category: Int, type: Int, title: String?, description: String?, copy_forbidden: Int
                       , tags: Map<String?, String?>?, imgs:Map<String, String>){
        if(AppPaintF.instance.cookie==null){
            return
        }
        RetrofitRepository.getInstance().createDoc(biz,category,type,title,description,copy_forbidden
                            ,AppPaintF.instance.cookie!!.bili_jct,tags,imgs).subscribe(Consumer {
            createLiveData.postValue(it)
        }).add()
    }

    fun doNetCreatePhotoDoc(biz: Int, category: Int, title: String?, description: String?, copy_forbidden: Int
                       , tags: Map<String?, String?>?, imgs:Map<String, String>){
        if(AppPaintF.instance.cookie==null){
            return
        }
        RetrofitRepository.getInstance().createPhotoDoc(biz,category,title,description,copy_forbidden
            ,AppPaintF.instance.cookie!!.bili_jct,tags,imgs).subscribe(Consumer {
            createLiveData.postValue(it)
        }).add()
    }
}