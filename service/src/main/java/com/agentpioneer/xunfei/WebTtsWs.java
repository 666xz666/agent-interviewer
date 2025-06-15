package com.agentpioneer.xunfei;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.URI;

import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.utils.OssUtils;
import jakarta.annotation.Resource;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import com.google.gson.Gson;


import java.net.URL;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class WebTtsWs {
    @Resource
    private OssUtils ossUtils;
    // 地址与鉴权信息
    public static final String hostUrl = "https://tts-api.xfyun.cn/v2/tts";
    // 均到控制台-语音合成页面获取
    @Value("${xf.config.appId}")
    public String appid;
    @Value("${xf.config.apiSecret}")
    public String apiSecret;
    @Value("${xf.config.apiKey}")
    public String apiKey;
    // 合成文本
    public static final String TEXT = "讯飞的文字合成语音功能，测试成功";
    // 合成文本编码格式
    public static final String TTE = "UTF8"; // 小语种必须使用UNICODE编码作为值
    // 发音人参数。到控制台-我的应用-语音合成-添加试用或购买发音人，添加后即显示该发音人参数值，若试用未添加的发音人会报错11200
    public String vcn = "aisjiuxu";

    // json
    public static final Gson gson = new Gson();
    public static boolean wsCloseFlag = false;

    public void setWsCloseFlag(boolean wsCloseFlag) {
        WebTtsWs.wsCloseFlag = wsCloseFlag;
    }

    public String createVoice(String text, String vcn) throws Exception {
        ossUtils.init();
        String wsUrl = getAuthUrl(hostUrl, apiKey, apiSecret).replace("https://", "wss://");
        String basePath = System.getProperty("user.dir") + System.currentTimeMillis() + ".mp3";
        File file = new File(basePath);
        OutputStream outputStream = new FileOutputStream(file);
        websocketWork(wsUrl, outputStream, text, appid, vcn);
        Thread.sleep(2500);

        if (file.exists() && file.isFile() && file.length() > 0) {
            long fileSizeInBytes = file.length();
            System.out.println("文件的字节大小为: " + fileSizeInBytes + " 字节");
        } else {
            System.out.println("文件不存在或不是一个常规文件。");
            throw new BusinessException(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        String fileString = null;
        try {
            fileString = ossUtils.uploadFile(file, "audio/mpeg");
        } catch (Exception e) {
            throw new BusinessException(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        } finally {
            ossUtils.shutdown();
        }
        return fileString;
    }

    // Websocket方法，为流式文件生成服务
    public static void websocketWork(
            String wsUrl, OutputStream outputStream, String text, String appid, String vcn
    ) {
        try {
            URI uri = new URI(wsUrl);
            WebSocketClient webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("ws建立连接成功...");
                }

                @Override
                public void onMessage(String text) {
                    // System.out.println(text);
                    JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
                    System.out.println("---------------" + myJsonParse + "数据的值是这个");
                    if (myJsonParse.code != 0) {
                        System.out.println("发生错误，错误码为：" + myJsonParse.code);
                        System.out.println("本次请求的sid为：" + myJsonParse.sid);
                    }
                    if (myJsonParse.data != null) {
                        try {
                            byte[] textBase64Decode = Base64.getDecoder().decode(myJsonParse.data.audio);
                            outputStream.write(textBase64Decode);
                            outputStream.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (myJsonParse.data.status == 2) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println("本次请求的sid==>" + myJsonParse.sid);
                            // 可以关闭连接，释放资源
//                            wsCloseFlag = true;
                        }
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("ws链接已关闭，本次请求完成...");
                }

                @Override
                public void onError(Exception e) {
                    System.out.println("发生错误 " + e.getMessage());
                }
            };
            // 建立连接
            webSocketClient.connect();
            while (!webSocketClient.getReadyState().equals(ReadyState.OPEN)) {
                //System.out.println("正在连接...");
                Thread.sleep(100);
            }
            MyThread webSocketThread = new MyThread(webSocketClient, text, appid, vcn);
            webSocketThread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // 线程来发送音频与参数
    static class MyThread extends Thread {
        WebSocketClient webSocketClient;

        String text;
        String appid;
        String VCN;

        public MyThread(WebSocketClient webSocketClient, String text, String appid, String VCN) {
            this.webSocketClient = webSocketClient;
            this.text = text;
            this.appid = appid;
            this.VCN = VCN;
        }

        public void run() {
            String requestJson;//请求参数json串
            try {
                requestJson = "{\n" +
                        "  \"common\": {\n" +
                        "    \"app_id\": \"" + appid + "\"\n" +
                        "  },\n" +
                        "  \"business\": {\n" +
                        "    \"aue\": \"lame\",\n" +
                        "    \"sfl\": 1,\n" +
                        "    \"tte\": \"" + TTE + "\",\n" +
                        "    \"ent\": \"intp65\",\n" +
                        "    \"vcn\": \"" + VCN + "\",\n" +
                        "    \"pitch\": 50,\n" +
                        "    \"volume\": 100,\n" +
                        "    \"speed\": 50\n" +
                        "  },\n" +
                        "  \"data\": {\n" +
                        "    \"status\": 2,\n" +
                        "    \"text\": \"" + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)) + "\"\n" +
                        //"    \"text\": \"" + Base64.getEncoder().encodeToString(TEXT.getBytes("UTF-16LE")) + "\"\n" +
                        "  }\n" +
                        "}";
                webSocketClient.send(requestJson);
                // 等待服务端返回完毕后关闭
                while (!wsCloseFlag) {
                    Thread.sleep(200);
                }
                webSocketClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        //System.out.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }

    //返回的json结果拆解
    class JsonParse {
        int code;
        String sid;
        Data data;
    }

    class Data {
        int status;
        String audio;
    }
}