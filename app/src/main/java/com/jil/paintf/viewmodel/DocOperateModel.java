package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.jil.paintf.repository.FavOperateResult;
import com.jil.paintf.repository.OperateResult;
import com.jil.paintf.repository.RetrofitRepository;
import com.jil.paintf.service.AppPaintF;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DocOperateModel extends BaseViewModel {
    MutableLiveData<OperateResult> voteResult =new MutableLiveData<>();
    MutableLiveData<FavOperateResult> favResult =new MutableLiveData<>();
    MutableLiveData<FavOperateResult> removeFavResult =new MutableLiveData<>();
    private RetrofitRepository retrofitRepository=RetrofitRepository.getInstance();

    public MutableLiveData<OperateResult> getVoteResult() {
        return voteResult;
    }

    public MutableLiveData<FavOperateResult> getFavResult() {
        return favResult;
    }

    public MutableLiveData<FavOperateResult> getRemoveFavResult() {
        return removeFavResult;
    }

    public void doNetVote(int id , int type){
        retrofitRepository.postVote(id, type).subscribe(new Observer<OperateResult>() {
            @Override
            public void onSubscribe(Disposable d) {
                if(voteResult ==null)
                    voteResult =new MutableLiveData<>();
            }

            @Override
            public void onNext(OperateResult operateResult) {
                voteResult.postValue(operateResult);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void doNetAddFav(int id){
        if(AppPaintF.instance.getCookie()==null){
            favResult.postValue(new FavOperateResult(-1,null,"没有登录","没有登录"));
            return;
        }
        add(retrofitRepository.postAddFav(id).subscribe(new Consumer<FavOperateResult>() {
            @Override
            public void accept(FavOperateResult favOperateResult) throws Exception {
                favResult.postValue(favOperateResult);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }));
    }

    public void doNetDeleteFav(int id){
        if(AppPaintF.instance.getCookie()==null){
            removeFavResult.postValue(new FavOperateResult(-1,null,"没有登录","没有登录"));
            return;
        }
        add(retrofitRepository.postDeleteFav(id).subscribe(new Consumer<FavOperateResult>() {
            @Override
            public void accept(FavOperateResult favOperateResult) throws Exception {
                removeFavResult.postValue(favOperateResult);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }));
    }
}
