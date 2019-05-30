package com.weChat.service;

import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;
import com.weChat.entity.*;
import com.weChat.util.MyUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.util.*;

@Service
public class WxService {
    @Autowired
    private DevService devService;

    private static final String APPKEY="1fec136dbd19f44743803f89bd55ca62";

    public static final String GET_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRE";
    public static final String APPID="wx9f3cf46b0e0a2cc5";
    public static final String APPSECRET="f60f20762f532009454edb4f426176e3";

    private static AccessToken at;

    //设置APPID/AK/SK
    public static final String APP_ID = "16064291";
    public static final String API_KEY = "IafHXwaKULPsiweAFYcpFD7k";
    public static final String SECRET_KEY = "92lOOFhek0AGkGQXge26poAqE5GdMd9Y";

    /**
     * 获取Token
     */
    private static void getToken(){
        String url=GET_TOKEN_URL.replace("APPID",APPID).replace("APPSECRE",APPSECRET);
        String token = MyUtil.get(url);
        System.out.println(token);
        JSONObject jsonObject=JSONObject.fromObject(token);
        String accessToken=jsonObject.getString("access_token");
        String expireIn=jsonObject.getString("expires_in");
        //创建Token对象并存储
        at=new AccessToken(accessToken,expireIn);
    }

    /**
     * 向外暴露获取token的方法
     * @return
     */
    public static String getAccessToken(){
        if (at==null||at.isExpire()){
            getToken();
        }
        return at.getAccessToken();
    }
    /**
     * 解析xml数据包
     * @param request
     * @return
     * @throws IOException
     */
    public Map<String, String> parseRequest(HttpServletRequest request){
        Map<String,String> requestMap=new HashMap<>();
        InputStream is= null;
        try {
            is = request.getInputStream();
            SAXReader reader=new SAXReader();
            //根据输入流，获取文档对象
            Document document = reader.read(is);
            //根据文档获取根节点
            Element root = document.getRootElement();
            //获取根节点所有子节点
           List<Element> elements=root.elements();
            for (Element element : elements) {
                requestMap.put(element.getName(),element.getStringValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

            return requestMap;
    }

    /**
     * 用于处理所有的事件和消息回复
     * @param requestMap
     * @return
     */
    public String getResponse(Map<String, String> requestMap) {

        BaseMessage msg=null;
        String msgType=requestMap.get("MsgType");
        switch (msgType){
            case "text":
                msg=dealTextMessage(requestMap);
                break;
            case "image":
                msg=dealImageMessage(requestMap);
                break;
            case "voice":

                break;
            case "video":

                break;
            case "shortvideo":

                break;
            case "location":

                break;
            case "link":

                break;
            case "event":
                    msg=dealEvent(requestMap);
                System.out.println("event=======");
                break;
            default:
                break;
        }
        //System.out.println(msg.getFromUserName()+msg);
        //把消息对象处理为xml数据包
        if (msg!=null) {
            return beanToXml(msg);
        }
        return null;
    }

    /**、
     * 文字识别服务
     * @param requestMap
     * @return
     */
    private BaseMessage dealImageMessage(Map<String, String> requestMap) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        String path =requestMap.get("PicUrl");
        //org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        org.json.JSONObject json = client.generalUrl(path, new HashMap<String, String>());
        String res=json.toString();
        JSONObject jsonObject=JSONObject.fromObject(res);
        JSONArray jsonArray = jsonObject.getJSONArray("words_result");
        Iterator<JSONObject> it = jsonArray.iterator();
        StringBuilder sb=new StringBuilder();
        while (it.hasNext()){
            JSONObject next = it.next();
            sb.append(next.getString("words"));
        }

        System.out.println(sb);
        return new TextMessage(requestMap,sb.toString());
    }

    private BaseMessage dealEvent(Map<String, String> requestMap) {

        String event = requestMap.get("Event");
        switch (event){
            case"CLICK":
                return dealClick(requestMap);
            case"VIEW":
                return dealView(requestMap);
            case"user_get_card":
                devService.saveInfo(requestMap.get("FromUserName"),requestMap.get("UserCardCode"));
                break;
            case"subscribe":
                return new TextMessage(requestMap,"欢迎体验特斯威美好生活");
            default:
                break;
        }
        return null;
    }

    /**
     * 处理View菜单
     * @param requestMap
     * @return
     */
    private static BaseMessage dealView(Map<String, String> requestMap) {
        return null;
    }

    /**
     * 处理click菜单
     * @param requestMap
     * @return
     */
    private BaseMessage dealClick(Map<String, String> requestMap) {
        String eventKey = requestMap.get("EventKey");
        switch (eventKey){
            case "66":
                return new TextMessage(requestMap,devService.getCountsMoney(requestMap.get("FromUserName")));
            case"32":
                String mediaId=devService.getQrcodeImage();
                List<Image> images=new ArrayList<>();
                images.add(new Image(mediaId));
                return new ImageMessage(requestMap,images);
            case"233":
                String mediaId2=devService.getImageMediaId(requestMap.get("FromUserName"));
                List<Image> image=new ArrayList<>();
                image.add(new Image(mediaId2));
                return new ImageMessage(requestMap,image);
                default:
                    break;
        }

        return null;
    }

    public static String beanToXml(BaseMessage msg) {
        XStream stream = new XStream();
        // 设置需要处理XStreamAlias("xml")注释的类
        stream.processAnnotations(TextMessage.class);
        stream.processAnnotations(ImageMessage.class);
        stream.processAnnotations(MusicMessage.class);
        stream.processAnnotations(NewsMessage.class);
        stream.processAnnotations(VideoMessage.class);
        stream.processAnnotations(VoiceMessage.class);
        String xml = stream.toXML(msg);
        return xml;

    }


    public  BaseMessage dealTextMessage(Map<String, String> requestMap) {

        //用户发来的内容
        String content = requestMap.get("Content");
        if (content.equals("图文"))
        {
            List<Article> articles=new ArrayList<>();
            articles.add(new Article("标题","hhhh","https://juheimg.oss-cn-hangzhou.aliyuncs.com/www/ucenter/cardId.jpg","http://www.baidu.com"));
            NewsMessage newsMessage=new NewsMessage(requestMap,articles);
            return newsMessage;
        }
        if (content.equals("登录"))
        {
            String appId="wx9f3cf46b0e0a2cc5";
            String redirectUrl="http://ac454a5c.ngrok.io/test01/weixin/GetInfoServlet";
            String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+redirectUrl+"&response_type=code&scope=snsapi_userinfo#wechat_redirect";
            TextMessage tm=new TextMessage(requestMap,"<a href=\""+url+"\">这里</a>");
            return tm;
        }
        if (content.equals("优惠券"))
        {
            //ImageMessage imageMessage=new ImageMessage(requestMap,"BH7or1C2dEl1W21nsIDpDBYdQfvoWspByRFVCeQ8nC6aSvHxL3I_DE5kjYWCxIeT");
           // return imageMessage;
//            String url=devService.getCodeUrl(requestMap.get("FromUserName"));
//            if (url.equals("did")){
//                TextMessage tm=new TextMessage(requestMap,"您已经获取过二维码");
//                return tm;
//            }else {
//                TextMessage tm = new TextMessage(requestMap, "<a href=\""+url+"\">这里</a>");
//                return tm;
//            }
        }
        String res=chat(content);
        JSONObject jsonObject = JSONObject.fromObject(res);
        String result = jsonObject.getString("result");
        JSONObject jsonObject1 = JSONObject.fromObject(result);
        String text = jsonObject1.getString("text");
        TextMessage textMessage=new TextMessage(requestMap,text);
        return textMessage;

    }
    /**
     * 调用图灵机器人聊天
     * @param msg 	发送的消息
     * @return
     * by 罗召勇 Q群193557337
     */
    private static String chat(String msg) {
        String result =null;
        String url ="http://op.juhe.cn/robot/index";//请求接口地址
        Map params = new HashMap();//请求参数
        params.put("key",APPKEY);//您申请到的本接口专用的APPKEY
        params.put("info",msg);//要发送给机器人的内容，不要超过30个字符
        params.put("dtype","");//返回的数据的格式，json或xml，默认为json
        params.put("loc","");//地点，如北京中关村
        params.put("lon","");//经度，东经116.234632（小数点后保留6位），需要写为116234632
        params.put("lat","");//纬度，北纬40.234632（小数点后保留6位），需要写为40234632
        params.put("userid","");//1~32位，此userid针对您自己的每一个用户，用于上下文的关联
        try {
            result = MyUtil.net(url, params, "GET");
            System.out.println(result);
            JSONObject jsonObject = JSONObject.fromObject(result);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传临时素材
     * @param path 上传的文件的路径
     * @param type 上传的文件的类型
     * @return
     */
    public static String upload(String path,String type){
        File file=new File(path);
        String url="https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
        url=url.replace("ACCESS_TOKEN",getAccessToken()).replace("TYPE",type);
        try {
            URL urlObj=new URL(url);
            //强转为安全连接
            HttpsURLConnection connection =(HttpsURLConnection)urlObj.openConnection();
            //设置连接信息
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            //设置请求头信息
            connection.setRequestProperty("Connection","Keep-Alive");
            connection.setRequestProperty("Charset","utf-8");
            //数据的边界
            String boundary="-----"+System.currentTimeMillis();
            connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            //获取输出流
            OutputStream out = connection.getOutputStream();
            //创建文件输入流
            InputStream is=new FileInputStream(file);
            //第一部分：头部信息
            //准备头部信息
            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(boundary);
            sb.append("\r\n");
            sb.append("Content-Disposition:form-data;name=\"media\";filename=\""+file.getName()+"\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");
            out.write(sb.toString().getBytes());
            System.out.println(sb.toString());
            //第二部分：文件内容
            byte[] b = new byte[1024];
            int len;
            while((len=is.read(b))!=-1) {
                out.write(b, 0, len);
            }
            is.close();
            //第三部分：尾部信息
            String foot = "\r\n--"+boundary+"--\r\n";
            out.write(foot.getBytes());
            out.flush();
            out.close();
            //读取数据
            InputStream is2 = connection.getInputStream();
            StringBuilder resp = new StringBuilder();
            while((len=is2.read(b))!=-1) {
                resp.append(new String(b,0,len));
            }
            return resp.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获取带参数的二维码ticket
     * @return
     */
    public static String getQrCodeTicket(String fromUserName,String code){
        //String myToken=UUID.randomUUID().toString();
        String accessToken = WxService.getAccessToken();
        String url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+accessToken;
        //String data="{\"expire_seconds\": 2592000, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+fromUserName+"\"}}}";
        String data="{\n" +
                "    \"action_name\": \"QR_CARD\",\n" +
                "    \"action_info\": {\n" +
                "    \"card\": {\n" +
                "    \"card_id\": \"pLmhZ6CycVZ_pYRuY8ze79Ir2Ysk\",\n" +
                "     \"code\": \""+code+"\",\n" +
                "    \"is_unique_code\": false ,\n" +
                "    \"outer_str\":\""+fromUserName+"\"\n" +
                "  }\n" +
                " }\n" +
                "}";
        System.out.println(fromUserName+"fromUserName--------");
        String post = MyUtil.post(url, data);
        JSONObject jsonObject = JSONObject.fromObject(post);
        System.out.println(jsonObject);
        String ticket = jsonObject.getString("ticket");
        return ticket;

    }

    /**
     * 获取用户基本信息
     * @param openId
     * @return
     */
    public static String getUserInfo(String openId)
    {
        String url="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        url=url.replace("ACCESS_TOKEN",WxService.getAccessToken()).replace("OPENID",openId);
        String s = MyUtil.get(url);
        return s;
    }

    public static String createCard(String data){
        String url="https://api.weixin.qq.com/card/create?access_token="+getAccessToken();
        String post = MyUtil.post(url, data);
        return post;
    }

}
