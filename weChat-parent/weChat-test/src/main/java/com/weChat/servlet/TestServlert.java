package com.weChat.servlet;

import com.weChat.common.SignUtil;
import com.weChat.service.DevService;
import com.weChat.service.WxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Controller
@RequestMapping("test01")
public class TestServlert {

    @Autowired
    private DevService devService;
    /**
     * 确认请求来自微信服务器
     */
    @RequestMapping(value="weixin",method= RequestMethod.GET)  // weixin/weixinOpe
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("get");
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
        out.close();
        out = null;
    }

    /**
     * 处理微信服务器发来的消息
     * doPost方法有两个参数，request中封装了请求相关的所有内容，可以从request中取出微信服务器发来的消息；
     * 而通过response我们可以对接收到的消息进行响应，即发送消息。
     * 那么如何解析请求消息的问题也就转化为如何从request中得到微信服务器发送给我们的xml格式的消息了。
     * 这里我们借助于开源框架dom4j去解析xml（这里使用的是dom4j-1.6.1.jar），然后将解析得到的结果存入HashMap，解析请求消息的方法如下：
     */
    @Autowired
    WxService wxService;
    @RequestMapping(value="weixin",method=RequestMethod.POST)
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println(request);
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        //微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        //在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        //解析xml数据包
        /*ServletInputStream is = request.getInputStream();
        byte[] b=new byte[1024];
        int len;
        StringBuilder sb=new StringBuilder();
        while ((len=is.read(b))!=-1){
                sb.append(new String(b,0,len));
        }
        System.out.println(sb.toString());
        */

        // 调用核心业务类接收消息、处理消息
        Map<String,String> requestMap = wxService.parseRequest(request);
        System.out.println(requestMap+"656565");

       /* if (requestMap.get("Event").equals("user_get_card")){
            String url="https://api.weixin.qq.com/card/code/update?access_token="+WxService.getAccessToken();
            System.out.println(requestMap.get("UserCardCode"));
            String data="{\n" +
                    "  \"code\": \""+requestMap.get("UserCardCode")+"\",\n" +
                    "  \"card_id\": \"pLmhZ6INUONuwYO_LHHia6ink0aM\",\n" +
                    "  \"new_code\": \"3495739475\"\n" +
                    "}";
            String post = MyUtil.post(url, data);
            System.out.println(post);
        }*/

//        String resXml="<xml>\n" +
//                "  <ToUserName><![CDATA["+requestMap.get("FromUserName")+"]]></ToUserName>\n" +
//                "  <FromUserName><![CDATA["+requestMap.get("ToUserName")+"]]></FromUserName>\n" +
//                "  <CreateTime>"+System.currentTimeMillis()/1000+"</CreateTime>\n" +
//                "  <MsgType><![CDATA[text]]></MsgType>\n" +
//                "  <Content><![CDATA[欢迎]]></Content>\n" +
//                "  <MsgId>1234567890123456</MsgId>\n" +
//                "</xml>";
        String resXml=wxService.getResponse(requestMap);
        System.out.println(resXml+"565656");

        // 响应消息
        PrintWriter out = response.getWriter();
        out.print(resXml);
        out.flush();
        out.close();
    }

    @RequestMapping("testUrl")
    public String testUrl(){
        return "redirect:/http://www.baidu.com";
    }

    @RequestMapping("scan")
    public ResponseEntity<String> checkScanCode(String code){
        System.out.println(code);
        return devService.checkScanCode(code);
    }

    @RequestMapping("input")
    public String jumpInput(){
        return "input";
    }
}
