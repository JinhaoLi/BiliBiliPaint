package com.jil.paintf.repository;

import com.jil.paintf.network.BaseNetClient;
import com.jil.paintf.service.AppApiService;
import com.jil.paintf.service.AppPaintF;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.UByteArray;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;

import java.util.List;
import java.util.Map;

import static com.jil.paintf.viewmodel.MainViewModel.*;

public class RetrofitRepository {
    private BaseNetClient client;
    private AppApiService ApiVcBiliService;
    private AppApiService ApiBiliService;
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
        ApiVcBiliService =client.getApiVcBiliClient().create(AppApiService.class);
        ApiBiliService=client.getApiBiliClient().create(AppApiService.class);
    }

    public Observable<DocListRepository> getRecommend(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getRecommedIllust(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, RI));
    }

    public Observable<DocRepository> getDocDetail(final int id){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocRepository>>() {
            @Override
            public Observable<DocRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getIllustDoc(id);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocRetryWithDelay(3,id));
    }

    public Observable<DocListRepository> getNewIllusts(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getNewIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, NI));
    }

    public Observable<DocListRepository> getHotIllusts(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getHotIllusts(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, HI));
    }

    public Observable<DocListRepository> getRecommendCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getRecommedCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, RC));
    }

    public Observable<DocListRepository> getHotCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getHotCosplay(page,size);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DataListRetryWithDelay(3,page,size, HC));
    }

    public Observable<DocListRepository> getNewCosplay(final int page, final int size){
        return Observable.just(1).flatMap(new Function<Integer, Observable<DocListRepository>>() {
            @Override
            public Observable<DocListRepository> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getNewCosplay(page,size);
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
                return ApiBiliService.getDocReply(pn,id);
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
                return ApiBiliService.getDoc2Reply(oid,root);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new DocReplyRetryWithDelay());
    }

    /**
     * 获取用户上传数量
     * @param uid
     * @return
     */
    public Observable<UserUpLoadInfo> getUserUpLoad(final int uid){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<UserUpLoadInfo>>() {
            @Override
            public ObservableSource<UserUpLoadInfo> apply(Integer integer) throws Exception {
                return ApiVcBiliService.getUserUp(uid);
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
                return ApiVcBiliService.getUserDocList(uid,page,biz);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new UserDocListRetry(uid,page,biz));
    }

    public Observable<UserInfo> getUserInfo(final int mid){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<UserInfo>>() {
            @Override
            public ObservableSource<UserInfo> apply(Integer integer) throws Exception {
                return ApiBiliService.getUserInfo(mid);
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
                return ApiBiliService.getSearchData(page,keyword,categid);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retryWhen(new SearchRetry(page,keyword,categid));
    }

    /**
     * 点赞
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
                return ApiVcBiliService.postVoteDoc(id, AppPaintF.instance.getCookie().bili_jct,type);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(2);
    }

    /***
     * 退出登录
     * @return
     */
    public Observable<ResponseBody> exitLogin() {
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(Integer integer) throws Exception {
                return ApiVcBiliService.exitLogin();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(2);
    }

    public Observable<UserOperateResult> userOperate(int uid,final int act){
        return Observable.just(uid).flatMap(new Function<Integer, ObservableSource<UserOperateResult>>() {
            @Override
            public ObservableSource<UserOperateResult> apply(Integer integer) throws Exception {
                assert AppPaintF.instance.getCookie()!=null;
                return ApiBiliService.postUserOperate(integer,act,
                        11,"jsonp",AppPaintF.instance.getCookie().bili_jct);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).retry(2);
    }

    public Observable<BlackListRepository> getBlackList(int pn){
        return Observable.just(pn).flatMap(new Function<Integer, ObservableSource<BlackListRepository>>() {
            @Override
            public ObservableSource<BlackListRepository> apply(Integer integer) throws Exception {
                return ApiBiliService.getBlackList(integer);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MyInfo> getMyInfo(){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<MyInfo>>() {
            @Override
            public ObservableSource<MyInfo> apply(Integer integer) throws Exception {
                return ApiBiliService.getMyInfo();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UpLoadResult> postUpload(final List<MultipartBody.Part> body){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<UpLoadResult>>() {
            @Override
            public ObservableSource<UpLoadResult> apply(Integer integer) throws Exception {
                return ApiVcBiliService.postUploadImage(body);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> createDoc(final int biz, final int category, final int type
            , final String title, final String description, final int copy_forbidden
            , final String csrf_token, final Map<String, String> fields,final Map<String, String> imgs){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(Integer integer) throws Exception {
                return ApiVcBiliService.createDoc(biz,category,type
            ,title,description,copy_forbidden
            ,csrf_token,fields,imgs);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ResponseBody> createPhotoDoc(final int biz, final int category
            , final String title, final String description, final int copy_forbidden
            , final String csrf_token, final Map<String, String> fields,final Map<String, String> imgs){
        return Observable.just(1).flatMap(new Function<Integer, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(Integer integer) throws Exception {
                return ApiVcBiliService.createPhotoDoc(biz,category
                        ,title,description,copy_forbidden
                        ,csrf_token,fields,imgs);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<UserOperateResult> updateSign(String sign){
        return Observable.just(sign).flatMap(new Function<String, ObservableSource<UserOperateResult>>() {
            @Override
            public ObservableSource<UserOperateResult> apply(String s) throws Exception {
                return ApiBiliService.updateSign(s,"json",AppPaintF.instance.getCookie().bili_jct);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
