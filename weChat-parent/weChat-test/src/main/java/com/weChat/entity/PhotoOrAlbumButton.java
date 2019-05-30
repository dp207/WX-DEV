package com.weChat.entity;

import java.util.ArrayList;
import java.util.List;

public class PhotoOrAlbumButton extends AbstractButton {

	private String type="pic_photo_or_album";
	private String key;
	private List<AbstractButton> sub_button = new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<AbstractButton> getSub_button() {
		return sub_button;
	}

	public void setSub_button(List<AbstractButton> sub_button) {
		this.sub_button = sub_button;
	}

	public PhotoOrAlbumButton(String name,String key) {
		super(name);
		this.key=key;
	}

}
