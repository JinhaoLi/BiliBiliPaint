package com.jil.paintf.service;

import com.jil.paintf.repository.*;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.*;

/**
 * API
 */
public interface AppApiService {
    /**
     * * 5->加入黑名单
     * * 6->移除黑名单
     * * 2->取消关注
     * * 1->关注
     */
    int JOIN_BLACK_LIST = 5;
    int REMOVE_BLACK_LIST = 6;
    int REMOVE_ATTENTION_LIST = 2;
    int JOIN_ATTENTION_LIST = 1;

    /**
     *  退出登录
     * @return
     */
    @GET("/login?act=exit")
    Observable<ResponseBody> exitLogin();



    /**
     * 将用户 加入/移除 黑名单
     * fid=296427498
     * &act=5 //
     * 5->加入黑名单
     * 6->移除黑名单
     * 2->取消关注
     * 1->关注
     * &re_src=11
     * &jsonp=jsonp
     * &csrf=c0efc5ac59ec2b29668af384d22889ad
     * @return
     */
    @FormUrlEncoded
    @POST("/x/relation/modify")
    Observable<UserOperateResult> postUserOperate(@Field("fid")int fid,@Field("act")int act,@Field("re_src")int re_src,@Field("jsonp")String jsonp,@Field("csrf")String csrf);

    /**
     * 获取黑名单列表
     * @param pn 页码
     * @return
     */
    @GET("/x/relation/blacks?re_version=0&ps=20&jsonp=jsonp")
    Observable<BlackListRepository> getBlackList(@Query("pn")int pn);
    /**
     * 推荐插画漫画
     * @param page
     * @param size
     * @return
     */
    @GET("/link_draw/v2/Doc/index?type=recommend")
    Observable<DocListRepository> getRecommedIllust(@Query("page_num") int page, @Query("page_size") int size);

    /**
     * 文档细节
     * @param doc_id
     * @return
     */
    @GET("/link_draw/v1/doc/detail")
    Observable<DocRepository> getIllustDoc(@Query("doc_id") int doc_id);

    /**
     * 最新插画漫画
     * https://api.vc.bilibili.com/link_draw/v2/Doc/list?category=all&type=new&page_num=1&page_size=20
     */
    @GET("/link_draw/v2/Doc/list?category=all&type=new")
    Observable<DocListRepository> getNewIllusts(@Query("page_num") int page, @Query("page_size") int size);

    /**
     * 最热插画漫画
     * https://api.vc.bilibili.com/link_draw/v2/Doc/list?category=all&type=hot&page_num=0&page_size=20
     */
    @GET("/link_draw/v2/Doc/list?category=all&type=hot")
    Observable<DocListRepository> getHotIllusts(@Query("page_num") int page, @Query("page_size") int size);


    /**
     * 推荐摄影
     * https://api.vc.bilibili.com/link_draw/v2/Photo/index?type=recommend&page_num=0&page_size=45
     */
    @GET("/link_draw/v2/Photo/index?type=recommend")
    Observable<DocListRepository> getRecommedCosplay(@Query("page_num") int page, @Query("page_size") int size);


    /**
     * 最热cos
     * https://api.vc.bilibili.com/link_draw/v2/Photo/list?category=cos&type=hot&page_num=1&page_size=20
     */
    @GET("/link_draw/v2/Photo/list?category=cos&type=hot")
    Observable<DocListRepository> getHotCosplay(@Query("page_num") int page, @Query("page_size") int size);

    /**
     * 最新cos
     * https://api.vc.bilibili.com/link_draw/v2/Photo/list?category=cos&type=new&page_num=0&page_size=20
     */
    @GET("/link_draw/v2/Photo/list?category=cos&type=new")
    Observable<DocListRepository> getNewCosplay(@Query("page_num") int page, @Query("page_size") int size);

    /**
     * 私人服装模块
     * https://api.vc.bilibili.com/link_draw/v2/Photo/list?category=sifu&type=hot&page_num=0&page_size=20
     */
    @GET("/link_draw/v2/Photo/list?category=sifu")
    Observable<DocListRepository> getNewPersonalClothing(@Query("type")String type,@Query("page_num") int page, @Query("page_size") int size);


    /**
     * 一级-DOC回复
     * https://api.bilibili.com/x/v2/reply?jsonp=jsonp&pn=1&type=11&sort=2&oid=56836766
     *
     */
    @GET("/x/v2/reply?jsonp=jsonp&type=11&sort=2")
    Observable<ReplyRepository> getDocReply(@Query("pn")int pn,@Query("oid")int oid);

    /**
     * 二级-DOC回复
     * https://api.bilibili.com/x/v2/reply/reply?jsonp=jsonp&pn=1&type=11&oid=56836766&root=2395830881
     */
    @GET("/x/v2/reply/reply?jsonp=jsonp&pn=1&type=11")
    Observable<ReplyRepository> getDoc2Reply(@Query("oid")int oid,@Query("root")long root);


    /**
     * 用户上传相簿信息API
     * https://api.vc.bilibili.com/link_draw/v1/doc/upload_count?uid=21833522
     */
    @GET("/link_draw/v1/doc/upload_count")
    Observable<UserUpLoadInfo> getUserUp(@Query("uid")int uid);


    /**
     * 获取用户相簿
     * https://api.vc.bilibili.com/link_draw/v1/doc/doc_list?uid=21833522&page_num=0&page_size=30&biz=all
     */
    @GET("/link_draw/v1/doc/doc_list")
    Observable<UserDocListRep> getUserDocList(@Query("uid")int uid,@Query("page_num")int page,@Query("biz")String biz);

    /**
     * 获取用户详细信息
     * https://api.bilibili.com/x/space/acc/info?mid=3056970&jsonp=jsonp
     */
    @GET("/x/space/acc/info?&jsonp=jsonp")
    Observable<UserInfo> getUserInfo(@Query("mid")int mid);

    /**
     * 搜索
     * https://api.bilibili.com/x/web-interface/search/type?context=&search_type=photo&page=1&order=pubdate&keyword=%E9%9B%B7%E5%A7%86&category_id=2&__refresh__=true&highlight=1&single_column=0&jsonp=jsonp&callback=__jp4
     * 摄影
     * https://api.bilibili.com/x/web-interface/search/type?context=&search_type=photo&page=1&order=stow&keyword=%E9%9B%B7%E5%A7%86&category_id=2&__refresh__=true&highlight=1&single_column=0&jsonp=jsonp
     * 画友
     * https://api.bilibili.com/x/web-interface/search/type?context=&search_type=photo&page=1&order=stow&keyword=%E9%9B%B7%E5%A7%86&category_id=1&__refresh__=true
     */
    @GET("/x/web-interface/search/type?context=&search_type=photo&order=stow&highlight=1&single_column=0&jsonp=jsonp&__refresh__=true")
    Observable<SearchRepository> getSearchData(@Query("page")int page,@Query("keyword")String keyWord,@Query("category_id")int category_id);


    /**
     * 插画点赞功能
     * https://api.vc.bilibili.com/link_draw/v2/Vote/operate
     * @param type 1:点赞;2:取消点赞
     */
    @FormUrlEncoded
    @POST("/link_draw/v2/Vote/operate")
    Observable<OperateResult> postVoteDoc(@Field("doc_id")int doc_id, @Field("csrf_token")String csrf_token, @Field("type")int type );

}
