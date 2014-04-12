package com.newer.sina.weibo.dao;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 关注的用户结构体
 * @author Administrator
 *
 */
public class Attention {

	/** 用户UID（int64） */
    public String id;
    /** 字符串型的用户 UID */
    public String idstr;
    /** 用户昵称 */
    public String screen_name;
    /** 友好显示名称 */
    public String name;
    /** 用户所在省级ID */
    public int province;
    /** 用户所在城市ID */
    public int city;
    /** 用户所在地 */
    public String location;
    /** 用户个人描述 */
    public String description;
    /** 用户博客地址 */
    public String url;
    /** 用户头像地址，50×50像素 */
    public String profile_image_url;
    /** 用户的微博统一URL地址 */
    public String profile_url;
    /** 用户的个性化域名 */
    public String domain;
    /** 用户的微号 */
    public String weihao;
    /** 性别，m：男、f：女、n：未知 */
    public String gender;
    /** 粉丝数 */
    public int followers_count;
    /** 关注数 */
    public int friends_count;
    /** 微博数 */
    public int statuses_count;
    /** 收藏数 */
    public int favourites_count;
    /** 用户创建（注册）时间 */
    public String created_at;
    /** 是否关注 */
    public boolean following;
    /** 是否允许所有人给我发私信，true：是，false：否 */
    public boolean allow_all_act_msg;
    /** 是否允许标识用户的地理位置，true：是，false：否 */
    public boolean geo_enabled;
    /** 是否是微博认证用户，即加V用户，true：是，false：否 */
    public boolean verified;
    /**  */
    public int verified_type;
    /** 用户备注信息，只有在查询用户关系时才返回此字段 */
    public String remark;
    /** 用户的最近一条微博信息id*/
    public String status_id;
    //
    public int ptype;
    /** 是否允许所有人对我的微博进行评论，true：是，false：否 */
    public boolean allow_all_comment;
    /** 用户大头像地址 */
    public String avatar_large;
    /** 用户高清大头像地址 */
    public String avatar_hd;
    /** 认证原因 */
    public String verified_reason;
    /** 该用户是否关注当前登录用户，true：是，false：否 */
    public boolean follow_me;
    /** 用户的互粉数 */
    public int bi_followers_count;
    /** 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语 */
    public String lang;
    public int online_status;
    //
    public int star;
    //
    public int mbtype;
    //
    public int mbrank;
    //
    public int block_word;
    //
    public int block_app;
    
    public static Attention parse(String jsonString) {
    	try {
			JSONObject jsonObject = new JSONObject(jsonString);
			return Attention.parse(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static Attention parse(JSONObject jsonObject) {
    	if (null == jsonObject) {
			return null;
		}
    	
    	Attention attention = new Attention();
    	attention.id = jsonObject.optString("id", "");
    	attention.idstr              = jsonObject.optString("idstr", "");
    	attention.screen_name        = jsonObject.optString("screen_name", "");
    	attention.name               = jsonObject.optString("name", "");
    	attention.province           = jsonObject.optInt("province", -1);
    	attention.city               = jsonObject.optInt("city", -1);
    	attention.location           = jsonObject.optString("location", "");
    	attention.description        = jsonObject.optString("description", "");
    	attention.url                = jsonObject.optString("url", "");
    	attention.profile_image_url  = jsonObject.optString("profile_image_url", "");
    	attention.profile_url        = jsonObject.optString("profile_url", "");
    	attention.domain             = jsonObject.optString("domain", "");
    	attention.weihao             = jsonObject.optString("weihao", "");
    	attention.gender             = jsonObject.optString("gender", "");
    	attention.followers_count    = jsonObject.optInt("followers_count", 0);
    	attention.friends_count      = jsonObject.optInt("friends_count", 0);
    	attention.statuses_count     = jsonObject.optInt("statuses_count", 0);
    	attention.favourites_count   = jsonObject.optInt("favourites_count", 0);
    	attention.created_at         = jsonObject.optString("created_at", "");
    	attention.following          = jsonObject.optBoolean("following", false);
    	attention.allow_all_act_msg  = jsonObject.optBoolean("allow_all_act_msg", false);
    	attention.geo_enabled        = jsonObject.optBoolean("geo_enabled", false);
    	attention.verified           = jsonObject.optBoolean("verified", false);
    	attention.verified_type      = jsonObject.optInt("verified_type", -1);
    	attention.remark             = jsonObject.optString("remark", "");
    	attention.status_id             = jsonObject.optString("status_id", ""); 
    	attention.allow_all_comment  = jsonObject.optBoolean("allow_all_comment", true);
    	attention.avatar_large       = jsonObject.optString("avatar_large", "");
    	attention.avatar_hd          = jsonObject.optString("avatar_hd", "");
    	attention.verified_reason    = jsonObject.optString("verified_reason", "");
    	attention.follow_me          = jsonObject.optBoolean("follow_me", false);
        attention.bi_followers_count = jsonObject.optInt("bi_followers_count", 0);
        attention.lang               = jsonObject.optString("lang", "");
        attention.star				= jsonObject.optInt("star", 0);
        attention.mbtype				= jsonObject.optInt("mbtype", 0);
        attention.mbrank				= jsonObject.optInt("mbrank", 0);
        attention.block_word				= jsonObject.optInt("block_word", 0);
        attention.block_app				= jsonObject.optInt("block_app", 0);
        attention.online_status				= jsonObject.optInt("online_status", 0);
    	
		return attention;
    }
}
