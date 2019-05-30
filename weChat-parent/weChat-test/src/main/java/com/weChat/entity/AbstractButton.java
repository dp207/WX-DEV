package com.weChat.entity;

public abstract class AbstractButton {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AbstractButton(String name) {
        super();
        this.name = name;
    }
}
