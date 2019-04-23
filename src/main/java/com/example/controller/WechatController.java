package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.hibernate.persister.walking.spi.WalkingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    //注入配置的对象
    @Autowired
    private WxMpService wxMpService;

    /**
     * authorize：授权认证
     * 查看文档需要一个returnUrl参数
     * @return
     */
    @RequestMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl) throws UnsupportedEncodingException {

        //自己编写获得openid的路径 在下面定义方法getUserInfo
        String url = "http://xmccjyqs.natapp1.cc/sell/wechat/getUserInfo";
//       String url = "http://xinglin.natapp1.cc/sell/wechat/getUserInfo";

        // 构造微信授权的 URL
        /**
         * 参数1：获取授权码的地址
         * 参数2：授权的策略（简单授权、scope）
         * 参数3：自己携带的数据
         */
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                URLEncoder.encode(returnUrl, "UTF-8"));
        log.info("redirectUrl:{}",redirectUrl);

        // 重定向
        return "redirect:" + redirectUrl;
    }


    /**
     * code：是授权码
     * returnUrl：是刚才我们自己传递的参数  会传递到微信然后传回来
     */
    @RequestMapping("getUserInfo")
    public String getUserInfo(@RequestParam("code")String code, @RequestParam("state") String returnUrl) throws UnsupportedEncodingException {
        log.info("code:{}",returnUrl);
        log.info("state:{}",returnUrl);
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
        WxMpUser wxMpUser = null;

        // 根据 SDK 文档，获取令牌
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        // 获取用户信息
        try {
            wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken,null);
            log.info("微信昵称：{}",wxMpUser.getNickname());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        String openId = wxMpUser.getOpenId();
        log.info("微信 openId：{}",openId);
        return "redirect:" + URLDecoder.decode(returnUrl,"UTF-8") + "?openid=" + openId;
    }


    /**
     * 参数是否授权成功
     * @param openid
     */
    @RequestMapping("/testOpenid")
    public void testOpenId(@RequestParam("openid") String openid){
        log.info("用户获得的 openid：",openid);
    }
}
