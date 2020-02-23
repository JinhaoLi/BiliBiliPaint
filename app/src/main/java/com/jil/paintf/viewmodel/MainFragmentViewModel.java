package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jil.paintf.R;
import com.jil.paintf.repository.Item;
import com.jil.paintf.repository.DocListRepository;
import com.jil.paintf.repository.RetrofitRepository;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.List;

import static com.jil.paintf.repository.DataListRetryWithDelay.*;

public class MainFragmentViewModel extends ViewModel {
    private static int[] page={0,0,0,0,0,0};
    private static final int SIZE=45;
    RetrofitRepository retrofitRepository =RetrofitRepository.getInstance();
    private MutableLiveData<List<Item>> recommendIllustsList;
    private MutableLiveData<List<Item>> newIllustsList;
    private MutableLiveData<List<Item>> hotIllustsList;

    private MutableLiveData<List<Item>> recommendCosplayList;
    private MutableLiveData<List<Item>> newCosplayList;
    private MutableLiveData<List<Item>> hotCosplayList;

    public MutableLiveData<List<Item>> getRecommendCosplayList() {
        if(recommendCosplayList==null)
            recommendCosplayList=new MutableLiveData<>();
        recommendCosplayNext();
        return recommendCosplayList;
    }

    private void recommendCosplayNext() {
        retrofitRepository.getRecommendCosplay(page[RC],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                recommendCosplayList.postValue(docListRepository.getData().getItems());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                page[RC]++;
            }
        });
    }

    public MutableLiveData<List<Item>> getNewCosplayList() {
        if(newCosplayList==null)
            newCosplayList=new MutableLiveData<>();
        newCosplayNext();
        return newCosplayList;
    }

    private void newCosplayNext() {
        retrofitRepository.getNewCosplay(page[NC],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                newCosplayList.postValue(docListRepository.getData().getItems());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                page[NC]++;
            }
        });
    }

    private void hotCosplayNext() {
        retrofitRepository.getHotCosplay(page[HC],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                hotCosplayList.postValue(docListRepository.getData().getItems());
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                page[HC]++;
            }
        });
    }

    public MutableLiveData<List<Item>> getHotCosplayList() {
        if(hotCosplayList==null)
            hotCosplayList=new MutableLiveData<>();
        hotCosplayNext();
        return hotCosplayList;
    }

    public MutableLiveData<List<Item>> getRecommendIllustsList() {
        if(recommendIllustsList ==null){
            recommendIllustsList =new MutableLiveData<>();
        }
        recommendNextPage();
        return recommendIllustsList;
    }

    public MutableLiveData<List<Item>> getNewIllustsList() {
        if(newIllustsList ==null){
            newIllustsList =new MutableLiveData<>();
        }
        newNextPage();
        return newIllustsList;
    }

    private void newNextPage() {
        retrofitRepository.getNewIllusts(page[1],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                newIllustsList.postValue(docListRepository.getData().getItems());
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

    public MutableLiveData<List<Item>> getHotIllustsList() {
        if(hotIllustsList ==null){
            hotIllustsList =new MutableLiveData<>();
        }
        hotNextPage();
        return hotIllustsList;
    }

    private void hotNextPage() {
        retrofitRepository.getHotIllusts(page[2],20).subscribe(new Observer<DocListRepository>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocListRepository docListRepository) {
                hotIllustsList.postValue(docListRepository.getData().getItems());
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
                recommendIllustsList.postValue(docListRepository.getData().getItems());
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

    public void refresh(int type) {

        switch(type){
            case RI:
                getRecommendIllustsList();
                break;
            case NI:
                getNewIllustsList();
                break;
            case HI:
                getHotIllustsList();
                break;
            case RC:
                getRecommendCosplayList();
                break;
            case NC:
                getNewCosplayList();
                break;
            case HC:
                getHotCosplayList();
                break;
            default:
                break;
        }
    }
}
