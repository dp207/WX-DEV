package com.weChat.util;


import com.weChat.entity.*;
import com.weChat.service.WxService;
import net.sf.json.JSONObject;

public class CreateMenu {

	public static void main(String[] args) {

		Button bt=new Button();
//		bt.getButton().add(new AbstractButton("菜单一"));
//		bt.getButton().add(new AbstractButton("菜单二"));
		//一级菜单
		bt.getButton().add(new ClickButton("查询累计金","66"));
        //bt.getButton().add(new ViewButton("点击输码","http://9eed64c7.ngrok.io/test01/input"));
		//一级菜单
		bt.getButton().add(new ClickButton("点击获取","233"));
		//bt.getButton().add(new ViewButton("扫码领券","http://mmbiz.qpic.cn/mmbiz_png/TguEkOXR4a5rj9D1ZWfqQbCgxmgPDJYEkCvGTzWcK3htaAIgaq4mh5NrB2qVpOCNrwaX6em75JXNPg42McHdsQ/0"));
		//一级菜单
		SubButton sb=new SubButton("有子菜单");
		//一级菜单子菜单
		sb.getSub_button().add(new PhotoOrAlbumButton("传图识字","31"));
		sb.getSub_button().add(new ClickButton("点击","32"));
		sb.getSub_button().add(new ViewButton("跳转","http://news.163.com"));
		bt.getButton().add(sb);
		JSONObject jsonObject = JSONObject.fromObject(bt);
		System.out.println(jsonObject);
		//准备url
		String url=" https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
		url=url.replace("ACCESS_TOKEN", WxService.getAccessToken());
		System.out.println(WxService.getAccessToken()+"pp了");
		//发送请求
		String post = MyUtil.post(url, jsonObject.toString());
		System.out.println(post);
	}

	public void ttt(){
		Button bt=new Button();
		bt.getButton().add(new ViewButton("优惠券2","https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+WxService.getAccessToken()));

		JSONObject jsonObject = JSONObject.fromObject(bt);
		//准备url
		String url=" https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
		url=url.replace("ACCESS_TOKEN", WxService.getAccessToken());
		//发送请求
		String post = MyUtil.post(url, jsonObject.toString());
		System.out.println(post+"22222");
	}
	
}
