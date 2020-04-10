package com.jil.paintf.viewmodel;

import android.annotation.SuppressLint;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.*;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {
    @SuppressLint("UseSparseArrays")
    private static Map<Integer,String> map=new HashMap<>();
    int[] page ={0,0};
    private MutableLiveData<UserInfo> userData;
    private MutableLiveData<UserUpLoad> userDoc;
    private RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<UserDoc> docListData;

    public MutableLiveData<UserUpLoad> getUserDoc(int uid) {
        if(userDoc==null){
            userDoc=new MutableLiveData<>();
        }
        retrofitRepository.getUserUpLoad(uid).subscribe(new Observer<UserUpLoad>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserUpLoad userUpLoad) {
                userDoc.postValue(userUpLoad);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        return userDoc;
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
        if(docListData==null){
            docListData=new MutableLiveData<>();
        }
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

    public MutableLiveData<UserInfo> getUserData(int mid) {
        if(userData==null)
            userData=new MutableLiveData<>();
        retrofitRepository.getUserInfo(mid).subscribe(new Observer<UserInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserInfo userInfo) {
                userData.postValue(userInfo);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        return userData;
    }
}
