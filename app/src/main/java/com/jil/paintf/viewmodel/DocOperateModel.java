package com.jil.paintf.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.OperateResult;
import com.jil.paintf.repository.RetrofitRepository;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DocOperateModel extends ViewModel {
    MutableLiveData<OperateResult> data=new MutableLiveData<>();
    private RetrofitRepository retrofitRepository=RetrofitRepository.getInstance();

    public MutableLiveData<OperateResult> getData() {
        return data;
    }

    public MutableLiveData<OperateResult> doAction(int id , int type){
        retrofitRepository.postVote(id, type).subscribe(new Observer<OperateResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                if(data==null)
                    data=new MutableLiveData<>();
            }

            @Override
            public void onNext(OperateResult operateResult) {
                data.postValue(operateResult);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        return data;
    }
}
