package com.jil.paintf.repository;

import com.jil.paintf.network.BaseNetClient;
import com.jil.paintf.service.AppApiService;
import com.jil.paintf.service.AppPaintF;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.jil.paintf.viewmodel.MainFragmentViewModel.*;

public class RetrofitRepository {
    private BaseNetClient client;
    private AppApiService appApiService;
    private DataListRetryWithDelay dataListRetryWithDelay;
    private String token;
    private static RetrofitRepository retrofitRepository;
    public static RetrofitRepository getInstance(){
        if(retrofitRepository==null){
                synchronized (RetrofitRepository.class){
                    if(retrofitRepository==null){
                        return retrofitRepository =new RetrofitRepository();
                    }
                }
        }
        return retrofitRepository;
    }

    private RetrofitRepository() {
        client =new BaseNetClient();
        appApiService =client.getRetrofitAppApi().create(AppApiService.class);
    }

    public Observable<DocListRepository> getRecommend(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getRecommedIllust(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, RI));
    }

    public Observable<DocRepository> getDocDetail(final int id){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocRepository>>() {
            @Override
            public Observable<DocRepository> apply(Integer integer) throws Exception {
                return appApiService.getIllustDoc(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocRetryWithDelay(3,id));
    }

    public Observable<DocListRepository> getNewIllusts(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getNewIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, NI));
    }

    public Observable<DocListRepository> getHotIllusts(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getHotIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, HI));
    }

    public Observable<DocListRepository> getRecommendCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getRecommedCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, RC));
    }

    public Observable<DocListRepository> getHotCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getHotCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, HC));
    }

    public Observable<DocListRepository> getNewCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return appApiService.getNewCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, NC));
    }

    /**
     * 获取初始评论
     * @param pn
     * @param id
     * @return
     */
    public Observable<ReplyRepository> getDocReply(final int pn,final int id){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<ReplyRepository>>() {
            @Override
            public ObservableSource<ReplyRepository> apply(Integer integer) throws Exception {
                return client.getReplyRetrofitAppApi().create(AppApiService.class).getDocReply(pn,id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocReplyRetryWithDelay());
    }

    /**
     * 获取次级评论
     * @param oid
     * @param root
     * @return
     */
    public Observable<ReplyRepository> getDocNextReply(final int oid,final long root){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<ReplyRepository>>() {
            @Override
            public ObservableSource<ReplyRepository> apply(Integer integer) throws Exception {
                return client.getReplyRetrofitAppApi().create(AppApiService.class).getDoc2Reply(oid,root);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocReplyRetryWithDelay());
    }

    /**
     * 获取用户上传数量
     * @param uid
     * @return
     */
    public Observable<UserUpLoad> getUserUpLoad(final int uid){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<UserUpLoad>>() {
            @Override
            public ObservableSource<UserUpLoad> apply(Integer integer) throws Exception {
                return appApiService.getUserUp(uid);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new UserUpLoadRetry(uid));
    }

    /**
     *
     * 获取用户上传列表
     * @param uid
     * @param page
     * @param biz
     * @return
     */
    public Observable<UserDocListRep> getUserDocList(final int uid,final int page, final String biz){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<UserDocListRep>>() {
            @Override
            public ObservableSource<UserDocListRep> apply(Integer integer) throws Exception {
                return appApiService.getUserDocList(uid,page,biz);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new UserDocListRetry(uid,page,biz));
    }

    public Observable<UserInfo> getUserInfo(final int mid){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<UserInfo>>() {
            @Override
            public ObservableSource<UserInfo> apply(Integer integer) throws Exception {
                return client.getReplyRetrofitAppApi().create(AppApiService.class).getUserInfo(mid);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new UserInfoRetry(mid));
    }

    /**
     * 搜索
     * @param keyword
     * @param page
     * @param categid
     * @return
     */
    public Observable<SearchRepository> getSearchData(final String keyword,final int page,final int categid){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<SearchRepository>>() {
            @Override
            public ObservableSource<SearchRepository> apply(Integer integer){
                return client.getReplyRetrofitAppApi().create(AppApiService.class).getSearchData(page,keyword,categid);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new SearchRetry(page,keyword,categid));
    }

    /**
     * 发起请求前务必检查AppPaintF.getInstance().getCookie()是否为null
     * @param id
     * @param type
     * @return
     */
    public Observable<OperateResult> postVote(final int id, final int type){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<OperateResult>>() {
            @Override
            public ObservableSource<OperateResult> apply(Integer integer) throws Exception {
                assert AppPaintF.instance.getCookie()!=null;
                return appApiService.postVoteDoc(id, AppPaintF.instance.getCookie().bili_jct,type);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> exitLogin() {
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(Integer integer) throws Exception {
                return appApiService.exitLogin();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(5000);
    }
}
