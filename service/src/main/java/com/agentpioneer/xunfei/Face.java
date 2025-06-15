package com.agentpioneer.xunfei;

import com.agentpioneer.utils.HttpUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 人脸特征分析表情WebAPI接口调用示例接口文档(必看)：https://doc.xfyun.cn/rest_api/%E4%BA%BA%E8%84%B8%E7%89%B9%E5%BE%81%E5%88%86%E6%9E%90-%E8%A1%A8%E6%83%85.html
 * 图片属性：png、jpg、jpeg、bmp、tif图片大小不超过800k
 * (Very Important)创建完webapi应用添加服务之后一定要设置ip白名单，找到控制台--我的应用--设置ip白名单，如何设置参考：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=41891
 * 错误码链接：https://www.xfyun.cn/document/error-code (code返回错误码时必看)
 *
 * @author iflytek
 */
@Service
public class Face {
    // webapi 接口地址
    private static final String URL = "http://tupapi.xfyun.cn/v1/expression";
    // 应用ID(必须为webapi类型应用,并人脸特征分析服务,参考帖子如何创建一个webapi应用：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=36481)
    @Value("${xf.config.appId}")
    private String APPID = "。。。。。。。";
    // 接口密钥(webapi类型应用开通人脸特征分析服务后，控制台--我的应用---人脸特征分析---服务的apikey
    @Value("${xf.config.apiKey}")
    private String API_KEY = "。。。。。。。";
    // 图片数据可以通过两种方式上传，第一种在请求头设置image_url参数，第二种将图片二进制数据写入请求体中。若同时设置，以第一种为准。
    // 此demo使用第二种方式进行上传图片地址，如果想使用第一种方式，请求体为空即可。
    // 图片名称
    private static final String IMAGE_NAME = "1.jpg";
    // 图片url
    //private static final String IMAGE_URL = " ";

    // 图片地址
    private static final String PATH = "image/1.jpg";

    /**
     * WebAPI 调用示例程序
     *
     * @param
     * @throws IOException
     */
    public String getFaceExpression(String imageName, MultipartFile file) throws IOException {
        Map<String, String> header = buildHttpHeader(imageName);
        byte[] imageByteArray = file.getBytes();
        String result = HttpUtil.doPost1(URL, header, imageByteArray);
        System.out.println("接口调用结果：" + result);
        return result;
    }

    /**
     * 组装http请求头
     */
    private Map<String, String> buildHttpHeader(String imageName) throws UnsupportedEncodingException {
        String curTime = System.currentTimeMillis() / 1000L + "";
        String param = "{\"image_name\":\"" + imageName + "\"}";
        String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
        String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", paramBase64);
        header.put("X-CurTime", curTime);
        header.put("X-CheckSum", checkSum);
        header.put("X-Appid", APPID);
        return header;
    }
}
