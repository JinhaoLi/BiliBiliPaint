package com.jil.paintf.service;

import com.jil.paintf.repository.*;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

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
     */
    @FormUrlEncoded
    @POST("/x/relation/modify")
    Observable<UserOperateResult> postUserOperate(@Field("fid")int fid,@Field("act")int act,@Field("re_src")int re_src,@Field("jsonp")String jsonp,@Field("csrf")String csrf);

    /**
     * 获取黑名单列表
     * @param pn 页码
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
     */
    @GET("/x/v2/reply?jsonp=jsonp&type=11&sort=2")
    Observable<ReplyRepository> getDocReply(@Query("pn")int pn,@Query("oid")int oid);

    /**
     * 二级-DOC回复
     * https://api.bilibili.com/x/v2/reply/reply?jsonp=jsonp&pn=1&type=11&oid=56836766&root=2395830881
     */
    @GET("/x/v2/reply/reply?jsonp=jsonp&type=11")
    Observable<ReplyNextRespository> getDoc2Reply(@Query("oid")int oid,@Query("root")long root,@Query("pn")int pn);

    /**
     * 评论
     * oid: 915467
     * type: 11
     * root: 597810098
     * parent: 597810098
     * message: 哈哈，我也是[呲牙]
     * plat: 1
     * jsonp: jsonp
     * csrf: f4137821b428fbaa33de8efbe83381a3
     * {"code":0,"message":"0","ttl":1,"data":{"success_action":0,"success_toast":"发送成功","need_captcha":false,"url":"","rpid":3145948149,"rpid_str":"3145948149","dialog":3145948149,"dialog_str":"3145948149","root":597810098,"root_str":"597810098","parent":597810098,"parent_str":"597810098","emote":{"[呲牙]":{"id":1902,"package_id":1,"state":0,"type":1,"attr":0,"text":"[呲牙]","url":"http://i0.hdslb.com/bfs/emote/b5a5898491944a4268360f2e7a84623149672eb6.png","meta":{"size":1},"mtime":1594022109}},"reply":{"rpid":3145948149,"oid":915467,"type":11,"mid":75965179,"root":597810098,"parent":597810098,"dialog":3145948149,"count":0,"rcount":0,"state":0,"fansgrade":0,"attr":0,"ctime":1594169030,"rpid_str":"3145948149","root_str":"597810098","parent_str":"597810098","like":0,"action":0,"member":{"mid":"75965179","uname":"JIL丷","sex":"保密","sign":"未来，永远，笑颜，希望","avatar":"http://i0.hdslb.com/bfs/face/b6e645970e5b8417bcd671753e2d4a0021671cb6.jpg","rank":"10000","DisplayRank":"0","level_info":{"current_level":5,"current_min":0,"current_exp":0,"next_exp":0},"pendant":{"pid":0,"name":"","image":"","expire":0,"image_enhance":""},"nameplate":{"nid":70,"name":"风纪精英","image":"http://i0.hdslb.com/bfs/face/cb3889a15126ed1a1aac024102e3f828ebd8926a.png","image_small":"http://i2.hdslb.com/bfs/face/86ad99140085962e0df02d08794b1de56b0f54f4.png","level":"普通勋章","condition":"风纪委员连任期数 \u003e= 3"},"official_verify":{"type":-1,"desc":""},"vip":{"vipType":1,"vipDueDate":1585065600000,"dueRemark":"","accessStatus":0,"vipStatus":0,"vipStatusWarn":"","themeType":0,"label":{"path":"","text":"","label_theme":""}},"fans_detail":null,"following":0,"is_followed":0,"user_sailing":{"pendant":null,"cardbg":null,"cardbg_with_focus":null}},"content":{"message":"哈哈，我也是[呲牙]","plat":1,"device":"","members":[],"emote":{"[呲牙]":{"id":1902,"package_id":1,"state":0,"type":1,"attr":0,"text":"[呲牙]","url":"http://i0.hdslb.com/bfs/emote/b5a5898491944a4268360f2e7a84623149672eb6.png","meta":{"size":1},"mtime":1594022109}},"jump_url":{},"max_line":999},"replies":null,"assist":0,"folder":{"has_folded":false,"is_folded":false,"rule":""},"up_action":{"like":false,"reply":false},"show_follow":false}}}
     */
    @FormUrlEncoded
    @POST("https://api.bilibili.com/x/v2/reply/add")
    Observable<AfterReplyResult> postReply(@Field("oid")int oid,@Field("type")int type,@Field("root")long root
            ,@Field("parent")long parent,@Field("message")String message,@Field("plat")int plat
            ,@Field("jsonp")String jsonp,@Field("csrf")String csrf);

    /**
     * 回复作品
     * @param oid
     * @param type
     * @param message
     * @param plat
     * @param jsonp
     * @param csrf
     * @return
     */
    @FormUrlEncoded
    @POST("/x/v2/reply/add")
    Observable<AfterReplyResult> postReplyArt(@Field("oid")int oid,@Field("type")int type
            ,@Field("message")String message,@Field("plat")int plat
            ,@Field("jsonp")String jsonp,@Field("csrf")String csrf);

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
     * 点赞功能
     * https://api.vc.bilibili.com/link_draw/v2/Vote/operate
     * @param type 1:点赞;2:取消点赞
     */
    @FormUrlEncoded
    @POST("/link_draw/v2/Vote/operate")
    Observable<OperateResult> postVoteDoc(@Field("doc_id")int doc_id, @Field("csrf_token")String csrf_token, @Field("type")int type );

    /***
     * 收藏
     * @param biz_type 2
     * @param fav_id
     * @param csrf_token
     * @return
     */
    @FormUrlEncoded
    @POST("/user_plus/v1/Fav/add")
    Observable<FavOperateResult> postFavAddDoc(@Field("biz_type")int biz_type, @Field("fav_id")int fav_id, @Field("csrf_token")String csrf_token);

    /**
     * 取消收藏
     * @param biz_type
     * @param fav_id
     * @param csrf_token
     * @return
     */
    @FormUrlEncoded
    @POST("/user_plus/v1/Fav/delete")
    Observable<FavOperateResult> postFavDeleteDoc(@Field("biz_type")int biz_type, @Field("fav_id")int fav_id, @Field("csrf_token")String csrf_token);

    @GET("/x/web-interface/nav")
    Observable<MyInfo> getMyInfo();

    @Multipart //请求体有多部分，使用@MultiPart上传
    @POST("/api/v1/drawImage/upload") //URL，可以为空
    Observable<UpLoadResult> postUploadImage(@Part List<MultipartBody.Part> request_img_part);

    /**
     * 更新签名
     * @param sign
     * @param jsonp
     * @param csrf
     * @return
     */
    @FormUrlEncoded
    @POST("/x/member/web/sign/update")
    Observable<UserOperateResult> updateSign(@Field("user_sign")String sign,@Field("jsonp")String jsonp,@Field("csrf")String csrf);


    /**
     *  创建绘画
     * @param biz 1绘画 2 photo
     * @param category 漫画 5 插画 4 其他 1  cos 2 私服 6
     * @param type 原创 0 同人1
     * @param title
     * @param description
     * @param copy_forbidden
     * @param csrf_token
     * @param tags
     * @param imgs
     * @return
     */
    @FormUrlEncoded
    @POST("https://api.vc.bilibili.com/link_draw/v1/doc/create")
    Observable<ResponseBody> createDoc(@Field("biz")int biz,@Field("category")int category,@Field("type")int type
                                       ,@Field("title")String title,@Field("description")String description,@Field("setting[copy_forbidden]")int copy_forbidden
                                       ,@Field("csrf_token")String csrf_token,@FieldMap() Map<String, String> tags,@FieldMap() Map<String, String> imgs);

    /**
     * 创建摄影
     * @param biz
     * @param category
     * @param title
     * @param description
     * @param copy_forbidden
     * @param csrf_token
     * @param tags
     * @param imgs
     * @return
     */
    @FormUrlEncoded
    @POST("https://api.vc.bilibili.com/link_draw/v1/doc/create")
    Observable<ResponseBody> createPhotoDoc(@Field("biz")int biz,@Field("category")int category
            ,@Field("title")String title,@Field("description")String description,@Field("setting[copy_forbidden]")int copy_forbidden
            ,@Field("csrf_token")String csrf_token,@FieldMap() Map<String, String> tags,@FieldMap() Map<String, String> imgs);


    /**
     *
     * @param biz_type 2
     * @param page 1
     * @param pagesize 30
     * @return
     */
    @GET("https://api.vc.bilibili.com/user_plus/v1/Fav/getMyFav")
    Observable<CollectionResult> getMCollection(@Query("biz_type")int biz_type,@Query("page")int page,@Query("pagesize")int pagesize);

    /**
     * 评论点赞
     * oid: 842217
     * type: 11
     * rpid: 468257951
     * action: 1   点赞/ 0   取消点赞
     * jsonp: jsonp
     * csrf: f4137821b428fbaa33de8efbe83381a3
     *
     * @return  {"code":0,"message":"0","ttl":1}
     */
    @FormUrlEncoded
    @POST("/x/v2/reply/action")
    Observable<UserOperateResult> postOperateVoteReply(@Field("oid")long oid,@Field("type")int type,@Field("rpid")long rpid
                                    ,@Field("action")int action,@Field("jsonp")String jsonp,@Field("csrf")String csrf);



}
