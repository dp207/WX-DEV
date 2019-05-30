package com.weChat.entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class VideoMessage extends BaseMessage {
	@XStreamAlias("MediaId")
	private String mediaId;
	@XStreamAlias("Title")
	private String title;
	@XStreamAlias("Description")
	private String description;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public VideoMessage(Map<String, String> requestMap, String mediaId, String title, String description) {
		super(requestMap);
		setMsgType("video");
		this.mediaId = mediaId;
		this.title = title;
		this.description = description;
	}

}
