package com.jil.paintf.viewmodel;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.*;
import com.jil.paintf.service.AppApiService;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel extends ViewModel {
    @SuppressLint("UseSparseArrays")
    private static Map<Integer,String> map=new HashMap<>();
    int[] page ={0,0,0};

    private MutableLiveData<UserInfo> userInfo;
    private MutableLiveData<UserUpLoadInfo> userUpLoadInfo;
    private RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    static {
        map.put(0,"all");
        map.put(1,"draw");
        map.put(2,"photo");
    }
    private MutableLiveData<UserDoc> docListData0;
    private MutableLiveData<UserDoc> docListData1;
    private MutableLiveData<UserDoc> docListData2;
    private MutableLiveData<UserOperateResult> mutableOpResult;

    int blackListPn =1;
    public MutableLiveData<List<User2>> muBlackList;
    private List<User2> blackList =new ArrayList<>();

    public UserViewModel() {
        muBlackList =new MutableLiveData<>();
        userUpLoadInfo =new MutableLiveData<>();
        docListData0=new MutableLiveData<>();
        docListData1=new MutableLiveData<>();
        docListData2=new MutableLiveData<>();
        userInfo =new MutableLiveData<>();
        mutableOpResult =new MutableLiveData<>();
    }

    public boolean checkUidInBlack(int uid){
        for (User2 user2:
             blackList) {
            if (user2.getMid()==uid){
                return true;
            }
        }
        return false;
    }

    public void doNetBlackList() {
        retrofitRepository.getBlackList(blackListPn).subscribe(new Observer<BlackListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }
            @Override
            public void onNext(BlackListRepository blackListRepository) {
                if(blackListRepository.getData()==null){
                    blackListPn=-1;
                    return;
                }
                List<User2> temp =blackListRepository.getData().getList();
                if(temp==null||temp.isEmpty()){
                    blackListPn=-1;
                }else {
                    blackList.addAll(temp);
                }
                muBlackList.postValue(blackList);

            }
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public MutableLiveData<UserUpLoadInfo> getUserUpLoadInfo() {
        return userUpLoadInfo;
    }

    public void doNetUserUpLoadInfo(int uid){
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
    }

    public MutableLiveData<UserDoc> getDocListData(int type) {
        if(type==0){
            return docListData0;
        }else if(type==1){
            return docListData1;
        }else {
            return docListData2;
        }
    }

    public void doNetDocListData(int uid, final int type){
        retrofitRepository.getUserDocList(uid,page[type],map.get(type)).subscribe(new Observer<UserDocListRep>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserDocListRep userDocListRep) {
                if(type==0){
                    docListData0.postValue(userDocListRep.getData());
                }else if(type==1){
                    docListData1.postValue(userDocListRep.getData());
                }else {
                    docListData2.postValue(userDocListRep.getData());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                page[type]++;
            }
        });
    }

    public MutableLiveData<UserInfo> getUserInfo() {
        return userInfo;
    }

    public void doNetUserInfo(int mid){
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
