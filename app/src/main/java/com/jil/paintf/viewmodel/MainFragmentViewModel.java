package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.repository.Item;
import com.jil.paintf.repository.DocListRepository;
import com.jil.paintf.repository.RetrofitRepository;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.List;

public class MainFragmentViewModel extends ViewModel {
    private static int[] page={0,0,0};
    private static final int SIZE=45;
    RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<List<Item>> recommedData;
    private MutableLiveData<List<Item>> newdData;
    private MutableLiveData<List<Item>> hotData;

    public MutableLiveData<List<Item>> getRecommedData() {
        if(recommedData ==null){
            recommedData =new MutableLiveData<>();
        }
        recommendNextPage();
        return recommedData;
    }

    public MutableLiveData<List<Item>> getNewdData() {
        if(newdData ==null){
            newdData =new MutableLiveData<>();
        }
        newNextPage();
        return newdData;
    }

    private void newNextPage() {
        retrofitRepository.getNew(page[1],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                newdData.postValue(docListRepository.getData().getItems());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                page[1]++;
            }
        });
    }

    public MutableLiveData<List<Item>> getHotData() {
        if(hotData ==null){
            hotData =new MutableLiveData<>();
        }
        hotNextPage();
        return hotData;
    }

    private void hotNextPage() {
        retrofitRepository.getHot(page[2],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                hotData.postValue(docListRepository.getData().getItems());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                page[2]++;
            }
        });
    }

    private void recommendNextPage(){
        retrofitRepository.getRecommend(page[0],SIZE).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                recommedData.postValue(docListRepository.getData().getItems());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                page[0]++;
            }
        });
    }
}
