package com.jil.paintf.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.jil.paintf.repository.*;
import com.jil.paintf.service.AppPaintF;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DocOperateModel extends BaseViewModel {
    MutableLiveData<OperateResult> voteResult =new MutableLiveData<>();
    MutableLiveData<FavOperateResult> favResult =new MutableLiveData<>();
    MutableLiveData<FavOperateResult> removeFavResult =new MutableLiveData<>();
    MutableLiveData<UserOperateResult> voteReplyLive =new MutableLiveData<>();
    MutableLiveData<AfterReplyResult> postReplyResult =new MutableLiveData<>();
    private RetrofitRepository retrofitRepository=RetrofitRepository.getInstance();

    public MutableLiveData<AfterReplyResult> getPostReplyResult() {
        return postReplyResult;
    }

    public MutableLiveData<UserOperateResult> getVoteReplyLive() {
        return voteReplyLive;
    }

    public MutableLiveData<OperateResult> getVoteResult() {
        return voteResult;
    }

    public MutableLiveData<FavOperateResult> getFavResult() {
        return favResult;
    }

    public MutableLiveData<FavOperateResult> getRemoveFavResult() {
        return removeFavResult;
    }

    public void doNetVoteReply(long oid, final int type,long rpid,int action){
        if(AppPaintF.instance.getCsrf()==null)
            return;
        add(retrofitRepository.voteReply(oid,type,rpid,action,AppPaintF.instance.getCsrf()).subscribe(new Consumer<UserOperateResult>() {
            @Override
            public void accept(UserOperateResult userOperateResult) {
                voteReplyLive.postValue(userOperateResult);
            }
        }));
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
        if(AppPaintF.instance.getCsrf()==null){
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
        if(AppPaintF.instance.getCsrf()==null){
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

    public void doNetPostReply(int oid, final int type, final long root
            , final long parent, final String message, final int plat, final String csrf){
        add(retrofitRepository.postReply(oid, type, root,parent,message, plat, csrf).subscribe(new Consumer<AfterReplyResult>() {
            @Override
            public void accept(AfterReplyResult afterReplyResult) throws Exception {
                postReplyResult.postValue(afterReplyResult);
            }
        }));
    }

    /**
     * 没有谁比我更懂自救
     * @param oid
     * @param type
     * @param message
     * @param plat
     * @param csrf
     */
    public void doNetPostReplyArt(int oid, final int type, final String message, final int plat, final String csrf){
        add(retrofitRepository.postReplyArt(oid, type,message, plat, csrf).subscribe(new Consumer<AfterReplyResult>() {
            @Override
            public void accept(AfterReplyResult afterReplyResult) throws Exception {
                if(afterReplyResult==null)
                    return;
                postReplyResult.postValue(afterReplyResult);
            }
        }));
    }
}
