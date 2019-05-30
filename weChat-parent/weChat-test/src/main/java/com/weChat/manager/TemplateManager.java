package com.weChat.manager;

import com.weChat.util.MyUtil;
import com.weChat.service.WxService;
import org.junit.Test;

public class TemplateManager {

    /**
     * 设置行业信息
     */
    @Test
    public void set(){
        String url="https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
        String accessToken = WxService.getAccessToken();
        url=url.replace("ACCESS_TOKEN",accessToken);
        String res="{\"industry_id1\":\"1\",\n" +
                "    \"industry_id2\":\"4\"}";
        MyUtil.post(url,res);
    }

    /**
     * 获取行业信息
     */
    @Test
    public void get(){
        String at=WxService.getAccessToken();
        String url="https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token="+at;
        String s = MyUtil.get(url);
        System.out.println(s);

    }
    @Test
    public void sendTemplateMessage(){
        String accessToken = WxService.getAccessToken();
        String url="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
        String data="{\n" +
                "           \"touser\":\"oLmhZ6Nn4aUSFcY7L4xwY_w1jFEM\",\n" +
                "           \"template_id\":\"ngM6vPz-cc9TWmMN8z7dClbbtDeisKWUCG89T4SVOaQ\",         \n" +
                "           \"data\":{\n" +
                "                   \"first\": {\n" +
                "                       \"value\":\"您有新的反馈信息啦！\",\n" +
                "                       \"color\":\"#abcdef\"\n" +
                "                   },\n" +
                "                   \"company\":{\n" +
                "                       \"value\":\"罗帅帅公司\",\n" +
                "                       \"color\":\"#fff000\"\n" +
                "                   },\n" +
                "                   \"time\": {\n" +
                "                       \"value\":\"2000年11月11日\",\n" +
                "                       \"color\":\"#1f1f1f\"\n" +
                "                   },\n" +
                "                   \"result\": {\n" +
                "                       \"value\":\"面试通过\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   },\n" +
                "                   \"remark\":{\n" +
                "                       \"value\":\"请和本公司人事专员联系！\",\n" +
                "                       \"color\":\"#173177\"\n" +
                "                   }\n" +
                "           }\n" +
                "       }";
        String post = MyUtil.post(url, data);
        System.out.println(post);
    }
}
