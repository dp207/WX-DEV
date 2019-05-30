package com.weChat.entity;

import java.util.ArrayList;
import java.util.List;

public class Button {

	private List<AbstractButton> button = new ArrayList<>();


	public List<AbstractButton> getButton() {
		return button;
	}

	public void setButton(List<AbstractButton> button) {
		this.button = button;
	}

}
