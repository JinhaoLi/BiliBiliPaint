package com.jil.paintf.viewmodel;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.*;
import com.jil.paintf.service.AppApiService;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {
    @SuppressLint("UseSparseArrays")
    private static Map<Integer,String> map=new HashMap<>();
    int[] page ={0,0};
    private MutableLiveData<UserInfo> userInfo;
    private MutableLiveData<UserUpLoadInfo> userUpLoadInfo;
    private RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<UserDoc> docListData;
    private MutableLiveData<UserOperateResult> mutableOpResult;

    public UserViewModel() {
        userUpLoadInfo =new MutableLiveData<>();
        docListData=new MutableLiveData<>();
        userInfo =new MutableLiveData<>();
        mutableOpResult =new MutableLiveData<>();
    }

    public MutableLiveData<UserUpLoadInfo> getUserUpLoadInfo(int uid) {
        retrofitRepository.getUserUpLoad(uid).subscribe(new Observer<UserUpLoadInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserUpLoadInfo userUpLoadInfo) {
                UserViewModel.this.userUpLoadInfo.postValue(userUpLoadInfo);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        return userUpLoadInfo;
    }

    public void reSetPage(){
        page[0]=0;
        page[1]=0;
    }

    static {
        map.put(0,"all");
        map.put(1,"draw");
        map.put(2,"photo");
    }

    public MutableLiveData<UserDoc> getDocListData(int uid,int type) {

        retrofitRepository.getUserDocList(uid,page[0],map.get(type)).subscribe(new Observer<UserDocListRep>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserDocListRep userDocListRep) {
                docListData.postValue(userDocListRep.getData());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                page[0]++;
            }
        });
        return docListData;
    }

    public MutableLiveData<UserInfo> getUserInfo(int mid) {
        retrofitRepository.getUserInfo(mid).subscribe(new Observer<UserInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserInfo userInfo) {
                UserViewModel.this.userInfo.postValue(userInfo);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        return userInfo;
    }

    public MutableLiveData<UserOperateResult> getMutableOpResult() {
        return mutableOpResult;
    }

    public void joinToBlackList(int uid){
        subscribe(retrofitRepository.userOperate(uid, AppApiService.JOIN_BLACK_LIST));
    }

    public void removeBlackList(int uid){
        subscribe(retrofitRepository.userOperate(uid,AppApiService.REMOVE_BLACK_LIST));
    }

    public void joinAttentionList(int uid){
        subscribe(retrofitRepository.userOperate(uid,AppApiService.JOIN_ATTENTION_LIST));
    }

    public void removeAttentionList(int uid){
        subscribe(retrofitRepository.userOperate(uid,AppApiService.REMOVE_ATTENTION_LIST));
    }

    private void subscribe(Observable<UserOperateResult> observable){
        observable.subscribe(new Observer<UserOperateResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserOperateResult userOperateResult) {
                mutableOpResult.postValue(userOperateResult);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
