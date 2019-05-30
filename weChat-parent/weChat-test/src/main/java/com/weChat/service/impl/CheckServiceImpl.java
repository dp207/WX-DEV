package com.weChat.service.impl;

import com.weChat.util.IMoocJSONResult;
import com.weChat.service.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CheckServiceImpl implements CheckService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public IMoocJSONResult checkCode(String code) {

        String[] split = code.split("=");
        System.out.println(split[1]);
        String key="USERCODE_USE:"+split[1];
        if (redisTemplate.opsForValue().get(key)==null){
            return IMoocJSONResult.build(202,"失败",null);
        }else {
            return IMoocJSONResult.build(200,"成功",null);
        }

    }
}
