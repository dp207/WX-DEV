package com.weChat.controller;

import com.weChat.service.CheckService;
import com.weChat.util.IMoocJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {
    @Autowired
    private CheckService checkService;
    @RequestMapping("checkCode")
    public IMoocJSONResult checkCode(@RequestParam("code") String code){
        return checkService.checkCode(code);
    }
}
