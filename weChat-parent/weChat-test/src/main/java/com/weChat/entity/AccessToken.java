package com.weChat.entity;

import lombok.Data;

@Data
public class AccessToken {
    private String accessToken;
    private long expireTime;

    public AccessToken(String accessToken,String expireIn){
        super();
        this.accessToken=accessToken;
        this.expireTime=System.currentTimeMillis()+Integer.parseInt(expireIn)*1000;
    }

    /**
     * 判断token是否过期
     * @return
     */
    public boolean isExpire(){
        return System.currentTimeMillis()>expireTime;
    }
}
