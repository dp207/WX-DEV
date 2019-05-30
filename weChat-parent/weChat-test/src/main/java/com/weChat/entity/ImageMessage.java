package com.weChat.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("xml")
public class ImageMessage extends BaseMessage{
	@XStreamAlias("Image")
	private List<Image> image = new ArrayList<>();

	public List<Image> getImage() {
		return image;
	}

	public void setImage(String mediaId) {
		this.image = image;
	}

	public ImageMessage(Map<String, String> requestMap,List<Image> image) {
		super(requestMap);
		this.setMsgType("image");
		this.image=image;
	}
	
}
