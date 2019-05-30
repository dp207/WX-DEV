package com.weChat.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.baidu.aip.ocr.AipOcr;
import com.weChat.service.DevService;
import com.weChat.util.MyUtil;
import com.weChat.entity.*;
import com.weChat.service.WxService;
import com.weChat.util.NewImageUtils;
import com.weChat.util.QrcodeUtil;
import org.junit.Test;


import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class TestWx {
	//设置APPID/AK/SK
	public static final String APP_ID = "16064291";
	public static final String API_KEY = "IafHXwaKULPsiweAFYcpFD7k";
	public static final String SECRET_KEY = "92lOOFhek0AGkGQXge26poAqE5GdMd9Y";
	@Autowired
	private DevService devService;
	@Test
	public void getQrcodeImage(){

		devService.getQrcodeImage();

	}

	@Test
	public void testUpdate(){
		String url="https://api.weixin.qq.com/card/code/update?access_token="+WxService.getAccessToken();

		String data="{\n" +
				"  \"code\": \"505760904007\",\n" +
				"  \"card_id\": \"pLmhZ6INUONuwYO_LHHia6ink0aM\",\n" +
				"  \"new_code\": \"505760904005\"\n" +
				"}";
		String post = MyUtil.post(url, data);
		System.out.println(post);
	}
	@Test
	public void cheackTest(){
		String url="https://api.weixin.qq.com/card/code/get?access_token="+WxService.getAccessToken();
		String data="{\n" +

				"   \"code\" : \"631648438001\",\n" +
				"   \"check_consume\" : true\n" +
				"}";
		String post = MyUtil.post(url, data);
		JSONObject jsonObject = JSONObject.fromObject(post);
		System.out.println(post);
	}
	@Test
	public void whiteTest(){
		String url="https://api.weixin.qq.com/card/testwhitelist/set?access_token="+WxService.getAccessToken();
		String data="{\n" +
				"    \"openid\": [\n" +
				"      \"oLmhZ6Nn4aUSFcY7L4xwY_w1jFEM\",\n" +
				"      \"oLmhZ6PK54dGjwcAj77wYRHqhHKc\",\n" +
				"      \"oLmhZ6BNOgtW1b65g3_GS0JEkaJk\",\n" +
				"       \"oLmhZ6GaAQlijDwhIY9I4bzpJcc8\"\n" +
				"    ],\n" +
				"    \"username\": [\n" +
				"      \"L563282025\",\n" +
				"      \"k672512292\",\n" +
				"      \"xp1435717914\",\n" +
				"      \"like1024666\"\n" +
				"     ]\n" +
				" }\n";
		String post = MyUtil.post(url, data);
		System.out.println(post);
	}
	@Test
	public void testCardInfo(){
		String url="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQHR8DwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyRElLS2dMS0VkcEUxSXJlVzFzNFoAAgQTx7pcAwQIBwAA";
		String s = MyUtil.get(url);
		System.out.println(s);
	}

	@Test
	public void getCardTiket(){
//		String data="{\n" +
//				"    \"action_name\": \"QR_CARD\",\n" +
//				"    \"expire_seconds\": 1800,\n" +
//				"    \"action_info\": {\n" +
//				"    \"card\": {\n" +
//				"    \"card_id\": \"pLmhZ6INUONuwYO_LHHia6ink0aM\",\n" +
//				"     \"code\": \"005\",\n" +
//				"    \"is_unique_code\": false ,\n" +
//				"    \"outer_str\":\"12b\"\n" +
//				"  }\n" +
//				" }\n" +
//				"}";
		String qrCodeTicket = WxService.getQrCodeTicket(UUID.randomUUID().toString(),"");
		System.out.println(qrCodeTicket);
	}
	@Test
	public void createTest(){
		String data="{ \"card\": {\n" +
				"  \"card_type\": \"GIFT\",\n" +
				"  \"gift\": {\n" +
				"      \"base_info\": {\n" +
				"          \"logo_url\": \"http://mmbiz.qpic.cn/mmbiz_jpg/TguEkOXR4a6ib4COjju3NiaGwibe5AwA38ZZic5DicLD48Nb2kMFDyBzMJiaz70eBNmXNLt3VCZMGPAp7mMIB4iblMhKg/0\",\n" +
				"          \"brand_name\":\"特斯威\",\n" +
				"          \"code_type\":\"CODE_TYPE_ONLY_QRCODE\",\n" +
				"          \"title\": \"6元人民币兑换券\",\n" +
				"          \"sub_title\": \"立即使用后，可截图分享\",\n" +
				"          \"color\": \"Color050\",\n" +
				"          \"notice\": \"使用时向销售人员出示此券\",\n" +
				"          \"service_phone\": \"020-88888888\",\n" +
				"          \"description\": \"您可以截图分享此优惠券二维码给您的朋友\\n当您的朋友到指定地点使用该优惠券之后，您累计金可以增加6元，" +
				"			满30元后可发起提现\\n\",\n" +
				"          \"date_info\": {\n" +
				"              \"type\": \"DATE_TYPE_FIX_TERM\",\n" +
				"              \"fixed_term\": 15 ,\n" +
				"              \"fixed_begin_term\": 0\n" +
				"          },\n" +
				"          \"sku\": {\n" +
				"              \"quantity\": 500000\n" +
				"          },\n" +
				"          \"get_limit\": 10,\n" +
				"          \"use_custom_code\": false,\n" +
				"          \"bind_openid\": false,\n" +
				"          \"can_share\": false,\n" +
				"        \"can_give_friend\": false,\n" +
				"          \"location_id_list\" : [123, 12321, 345345],\n" +

				"        \"promotion_url\": \"http://www.qq.com\"\n" +
				"      },\n" +
				"      \"gift\": \"凭此券兑换现金人民币6元\"}\n" +
				"      \n" +
				"}}";
		String card = WxService.createCard(data);
		System.out.println(card);
	}

	@Test
	public void infoTest(){
		String userInfo = WxService.getUserInfo("oLmhZ6Nn4aUSFcY7L4xwY_w1jFEM");
		System.out.println(userInfo);
	}

	@Test
	public void ticketTest(){
		String qrCodeTicket = WxService.getQrCodeTicket(UUID.randomUUID().toString(),"");
		System.out.println(qrCodeTicket);
	}
	@Test
	public void testGetUpload(){
		String media_id="KNSBL-MmHE9Awzrl7rRLEPlGSxzakOV8zlw7h-HSWA6BxMAkquWmYCa9M3gX1jih";
		String url="https://api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
		url=url.replace("ACCESS_TOKEN",WxService.getAccessToken()).replace("MEDIA_ID",media_id);
		System.out.println(url);
		MyUtil.get(url);

	}
	@Test
	public void uploadTest(){
		String url= "src/main/resources/static/newImage/new97.png";
		String iamge = WxService.upload(url, "image");
        JSONObject jsonObject = JSONObject.fromObject(iamge);

        System.out.println(jsonObject.getString("media_id"));
	}
	@Test
	public void testPic() {
		// 初始化一个AipOcr
		AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

		// 可选：设置代理服务器地址, http和socket二选一，或者均不设置
		//client.setHttpProxy("proxy_host", proxy_port); // 设置http代理
		//client.setSocketProxy("proxy_host", proxy_port); // 设置socket代理

		// 可选：设置log4j日志输出格式，若不设置，则使用默认配置
		// 也可以直接通过jvm启动参数设置此环境变量
		//System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

		// 调用接口
		String path = "http://mmbiz.qpic.cn/mmbiz_jpg/Pb66HJxwrkNvYXVF6QMMReP0mQH5BxenicJVLl8RZyDq0JXic3pyB05bsFgJW9FvshYgicviab11dgvibrPMSicevwkg/0";
		//org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
		org.json.JSONObject res = client.generalUrl(path, new HashMap<String, String>());
		System.out.println(res.toString(2));
	}
	@Test
	public void testButton(){
		Button bt=new Button();
//		bt.getButton().add(new AbstractButton("菜单一"));
//		bt.getButton().add(new AbstractButton("菜单二"));
		//一级菜单
		bt.getButton().add(new ClickButton("一级点击",""));
		//一级菜单
		bt.getButton().add(new ViewButton("一级跳转","http://www.baidu.com"));
		//一级菜单
		SubButton sb=new SubButton("有子菜单");
		//一级菜单子菜单
		sb.getSub_button().add(new PhotoOrAlbumButton("传图","31"));
		sb.getSub_button().add(new ClickButton("点击","32"));
		sb.getSub_button().add(new ViewButton("跳转","33"));
		bt.getButton().add(sb);
		JSONObject jsonObject = JSONObject.fromObject(bt);
		System.out.println(jsonObject.toString());

	}
	@Test
	public void testGetToken(){
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
	}
	@Test
	public void testMsg() {
		Map<String, String> map = new HashMap<>();
		map.put("ToUserName", "to");
		map.put("FromUserName", "from");
		map.put("MsgType", "type");
		TextMessage tm = new TextMessage(map, "还好");
		XStream stream = new XStream();
		// 设置需要处理XStreamAlias("xml")注释的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml = stream.toXML(tm);
		System.out.println(xml);

	}
	@Test
	public void testSavePath(){



        //NewImageUtils
	    //QrcodeUtil.getImage("6666","src/main/resources/static/qrcodeImage/"+UUID.randomUUID().toString()+".jpg",300,300);
    }

}
