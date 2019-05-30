package com.weChat.service;

import com.google.zxing.qrcode.encoder.QRCode;
import com.weChat.util.NewImageUtils;
import com.weChat.util.QrcodeUtil;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DevService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    /**
     * 根据用户openId设置属于该用户唯一二维码,并且设置OuterStr的唯一token
     * @param fromUserName
     * @return
     */
    public String onlyInfoSet(String fromUserName){

        String url=null;

        System.out.println(fromUserName+">>>>>>fromUserName");
        System.out.println(redisTemplate.opsForValue().get("CARD_SING:"+fromUserName)+"opopopo");
        if (redisTemplate.opsForValue().get("CARD_SING:"+fromUserName)==null){
            String onlyToken= UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("CARD_SING:"+fromUserName,onlyToken);
            redisTemplate.opsForValue().set("TIMES:"+fromUserName,0+"");
            String qrCodeTicket = WxService.getQrCodeTicket(onlyToken,"");
            url="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+qrCodeTicket;
            return url;

        }
        return "did";
    }
    public void countGetTiket(String fromUserName){
            String count = redisTemplate.opsForValue().get("TIMES:" + fromUserName);
            int newCount=Integer.parseInt(count)+ 1;
            redisTemplate.opsForValue().set("TIMES:"+fromUserName,newCount+"");

    }

    public String getCodeUrl(String fromUserName){

            if (redisTemplate.opsForValue().get("WX:" + fromUserName)==null) {
                String code = UUID.randomUUID().toString().substring(0, 20);
                String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + WxService.getQrCodeTicket(fromUserName, code);
                redisTemplate.opsForValue().set("WX:" + fromUserName, code);
                redisTemplate.opsForValue().set("COUNTS_CODE_USE:" + code, 0 + "");
                return url;
            }else {
                String code=redisTemplate.opsForValue().get("WX:" + fromUserName);
                String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + WxService.getQrCodeTicket(fromUserName, code);

                return url;

            }
    }

    /**
     * 用户领取唯一卡券时候将其openid和对应code保存
     * @param fromUserName
     * @param userCardCode
     */
    public void saveInfo(String fromUserName,String userCardCode){
        redisTemplate.opsForValue().set("ONLYINFO:"+fromUserName,userCardCode);
        redisTemplate.opsForValue().set("USERCODE_USE:"+userCardCode,0+"");
    }

    /**
     * 销售人员校验卡券二维码
     * @param code
     * @return
     */
    public ResponseEntity<String> checkScanCode(String code){

        if (redisTemplate.opsForValue().get("USERCODE_USE:"+code)==null){
            return ResponseEntity.status(202).body("该码不合法");
        }else {
            String counts = redisTemplate.opsForValue().get("USERCODE_USE:" + code);
            System.out.println(counts+"counts----");
           // int newConuts = Integer.parseInt(counts)+1;
            //redisTemplate.opsForValue().set("USERCODE_USE:" + code,newConuts+"");
            return ResponseEntity.status(200).body("校验成功");
        }

    }

    /**
     * 用户查询累计金
     * @param fromUserName
     * @return
     */
    public String getCountsMoney(String fromUserName){
        String saveTokenName = "TOKEN_INFO:" + fromUserName;
        String token = redisTemplate.opsForValue().get(saveTokenName);
        if (token==null){
            return "请获取卡券参与活动!";
        }
        String saveCountName="COUNTS_INFO:"+token;
        String counts=redisTemplate.opsForValue().get(saveCountName);
        int countsMoney=Integer.parseInt(counts)*6;
        return "您现在的累计金为: "+countsMoney+"元人民币";
    }


    public String getImageMediaId(String fromUserName) {
        String backGroundPath = "weChat-test/src/main/resources/static/background.jpg";
        String token = UUID.randomUUID().toString();
        String saveTokenName = "TOKEN_INFO:" + fromUserName;
        String saveMediaIdName = "MEDIAID_INFO:" + fromUserName;
        String saveCountName="COUNTS_INFO:"+token;
        //判断有保存唯一token的key，有说明之前生成过二维码
        if (redisTemplate.hasKey(saveTokenName)) {
            System.out.println("1---------");
            //判断临时素材是否存在
            if (redisTemplate.hasKey(saveMediaIdName)) {
                String mediaId = redisTemplate.opsForValue().get(saveMediaIdName);
                System.out.println(mediaId);
                return mediaId;
            }
            //利用之前合成的图片在上传到临时素材
            else {
                token = redisTemplate.opsForValue().get(saveTokenName);
                String newImagePath = "weChat-test/src/main/resources/static/newImage/" + token + ".jpg";
                //QrcodeUtil.getImage(token,qrCodeImagePath,300,300);
                String msg = WxService.upload(newImagePath, "image");
                JSONObject jsonObject = JSONObject.fromObject(msg);
                String mediaId=jsonObject.getString("media_id");
                redisTemplate.opsForValue().set(saveMediaIdName, mediaId);
                redisTemplate.expire(saveMediaIdName,60,TimeUnit.HOURS
                );
                return mediaId;
            }
        } else {
            System.out.println("2---------");
            String mediaId=null;

            System.out.println(token+"----token");
            String qrCodeImagePath = "weChat-test/src/main/resources/static/qrcodeImage/"+token+".jpg";
            QrcodeUtil.getImage(token, qrCodeImagePath, 300, 300);
            String newImagePath = "weChat-test/src/main/resources/static/newImage/"+token+".jpg";
            try {

                NewImageUtils newImageUtils = new NewImageUtils();
                // 构建叠加层
                BufferedImage buffImg = NewImageUtils.watermark(new File(backGroundPath), new File(qrCodeImagePath), 110, 400, 1.0f);

                // 输出水印图片
                newImageUtils.generateWaterFile(buffImg, newImagePath);

                String msg = WxService.upload(newImagePath, "image");
                JSONObject jsonObject = JSONObject.fromObject(msg);
                mediaId=jsonObject.getString("media_id");
                System.out.println(mediaId+"-----mediaId");
                redisTemplate.opsForValue().set(saveTokenName, token);
                redisTemplate.opsForValue().set(saveMediaIdName, mediaId);
                redisTemplate.opsForValue().set(saveCountName,0+"");
                redisTemplate.expire(saveMediaIdName,60, TimeUnit.HOURS);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return mediaId;
        }

    }
    public String getQrcodeImage(){

        String num= Math.random()+"";
        String[] split = num.split("\\.");
        String token1=split[1].substring(0,4);
        String token2=split[1].substring(5,9);
        String token=token1+token2;
        System.out.println((token));
        String qrCodeImagePath = "weChat-test/src/main/resources/static/qrcodeImage/"+token+".jpg";
        QrcodeUtil.getImage(token,qrCodeImagePath,300,300);
        String msg = WxService.upload(qrCodeImagePath, "image");
        JSONObject jsonObject = JSONObject.fromObject(msg);
        String mediaId=jsonObject.getString("media_id");
        redisTemplate.opsForValue().set("USERCODE_USE:"+token,mediaId);
        return mediaId;
    }

}
