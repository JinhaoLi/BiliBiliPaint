package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.*;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DocViewModel extends ViewModel {
    RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<DocData> data;

    public MutableLiveData<DocData> getData(int id){
        if(data==null){
            data=new MutableLiveData<>();
        }
        retrofitRepository.getDocDetail(id).subscribe(new Observer<DocRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocRepository docRepository) {
                data.postValue(docRepository.getData());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
        return data;
    }
}
