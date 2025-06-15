package com.agentpioneer.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class OssUtils {
    //读取配置文件的内容
    @Value("${aliyun.oss.file.endpoint}")
    private String ENDPOINT;
    @Value("${aliyun.oss.file.access-key-id}")
    private String ACCESS_KEY_ID;
    @Value("${aliyun.oss.file.secret-access-key}")
    private String ACCESS_KEY_SECRET;
    @Value("${aliyun.oss.file.bucket}")
    private String bucketName;

    private OSS ossClient;

    //初始化oss服务
    public void init() {
        ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    //上传文件
    // file:文件
    public String uploadFile(MultipartFile file) throws IOException {
        //获取文件的输入流
        InputStream inputstream = file.getInputStream();
        String filename = file.getOriginalFilename();
        //保证文件唯一性
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        filename = uuid + filename;

        //按照类别进行分类
        // 判断文件类型
        String contentType = file.getContentType();
        String fileType = "unknown";
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                fileType = "image";    //图像文件
            } else if (contentType.startsWith("application/")) {
                fileType = "application"; //应用程序文件
            } else if (contentType.startsWith("text/")) {
                fileType = "text"; //文本文件 (包括 HTML、CSS、JavaScript)
            } else if (contentType.startsWith("video/")) {
                fileType = "video"; //视频文件
            } else if (contentType.startsWith("audio/")) {
                fileType = "audio"; //音频文件
            }
        } else {
            fileType = "other"; //其他文件
        }
        //文件路劲
        filename = fileType + "/" + filename;
        try {

            ossClient.putObject(bucketName, filename, inputstream);
//            // 设置 URL 过期时间
//            Date expiration = new Date(System.currentTimeMillis() + 60 * 1000 * 60);
//            URL url = ossClient.generatePresignedUrl(bucketName, filename, expiration);
//            return url.toString();
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        String url = "https://" + bucketName + "." + ENDPOINT + "/" + filename;

        //   return generatePresignedUrl(filename, 60 * 60);
        return url;
    }

    public String uploadFile(File file, String type) throws IOException {
        //获取文件的输入流
        InputStream inputstream = new FileInputStream(file);
        String filename = file.getName();

        if (file.exists() && file.isFile()) {
            long fileSizeInBytes = file.length();
            System.out.println("文件的字节大小为: " + fileSizeInBytes + " 字节");
        } else {
            System.out.println("文件不存在或不是一个常规文件。");
        }
        //保证文件唯一性
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        filename = uuid + filename;

        //按照类别进行分类
        // 判断文件类型
        String contentType = type;
        String fileType = "unknown";
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                fileType = "image";    //图像文件
            } else if (contentType.startsWith("application/")) {
                fileType = "application"; //应用程序文件
            } else if (contentType.startsWith("text/")) {
                fileType = "text"; //文本文件 (包括 HTML、CSS、JavaScript)
            } else if (contentType.startsWith("video/")) {
                fileType = "video"; //视频文件
            } else if (contentType.startsWith("audio/")) {
                fileType = "audio"; //音频文件
            }
        } else {
            fileType = "other"; //其他文件
        }
        //文件路劲
        filename = fileType + "/" + filename;
        try {

            ossClient.putObject(bucketName, filename, inputstream);
//            // 设置 URL 过期时间
//            Date expiration = new Date(System.currentTimeMillis() + 60 * 1000 * 60);
//            URL url = ossClient.generatePresignedUrl(bucketName, filename, expiration);
//            return url.toString();
            System.out.println("上传成功，文件名为：" + filename);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        String url = "https://" + bucketName + "." + ENDPOINT + "/" + filename;

        //   return generatePresignedUrl(filename, 60 * 60);
        return url;
    }

    //下载文件
    // objectName:oss中的相对路径
    // localPath:本地文件路径
    public void downloadFile(String objectName, String localPath) throws Exception {
        try {
            //获取文件的名称
            String fileName = new File(objectName).getName();
            // 调用ossClient.getObject返回一个OSSObject实例，该实例包含文件内容及文件元数据。
//            OSSObject ossObject = ossClient.getObject(bucketName, objectName);
//             调用ossObject.getObjectContent获取文件输入流，可读取此输入流获取其内容。
//            InputStream content = ossObject.getObjectContent();
            // 构建完整的文件路径
            File path = new File(localPath, fileName);
            // 检查并创建目录
            File parentDir = path.getParentFile();
            if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("无法创建目录: " + parentDir.getAbsolutePath());
            }

            // 检查文件是否可以创建
            if (!path.exists() && !path.createNewFile()) {
                throw new IOException("无法创建文件: " + path.getAbsolutePath());
            }
            ossClient.getObject(new GetObjectRequest(bucketName, objectName), path);
            //流式下载
//            if (content != null) {
//                try (InputStream inputStream = content;
//                     OutputStream outputStream = new FileOutputStream(path)) {
//                    byte[] buffer = new byte[1024];
//                    int length;
//                     while ((length = inputStream.read(buffer)) > 0) {
//                        outputStream.write(buffer, 0, length);
//                    }
//                }
//            }

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
    }

    //列举指定目录的所有的文件

    /**
     * @param folderPath:文件夹路径
     * @return java.util.List<com.aliyun.oss.model.OSSObjectSummary>
     * @author zhang
     * @create 2024/10/31
     **/
    //OSSobjectSummary存储了元数据
    public List<OSSObjectSummary> listAllObjects(String folderPath) {
        List<OSSObjectSummary> objectSummaries = null;
        try {
            // 创建 ListObjectsRequest 对象并设置前缀
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix(folderPath);
            // ossClient.listObjects返回ObjectListing实例，包含此次listObject请求的返回结果。
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
            objectSummaries = objectListing.getObjectSummaries();
            //文件对应的相对路劲打印出来
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + objectSummary.getSize() + ")");
            }
            while (true) {
                // 如果有下一批，继续获取
                if (objectListing.isTruncated()) {
                    objectListing = ossClient.listObjects(String.valueOf(objectListing));
                    objectSummaries.addAll(objectListing.getObjectSummaries());
                } else {
                    break;
                }
            }
            return objectSummaries;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
        return objectSummaries;
    }

    public List<String> getListUrl(String folderPath) {
        ArrayList<String> listUrl = new ArrayList<>();
        List<OSSObjectSummary> summaries = listAllObjects(folderPath);
        for (OSSObjectSummary summary : summaries) {
            String fileName = summary.getKey();
            String url = "https://" + bucketName + "." + ENDPOINT + "/" + fileName;
            System.out.println(url);
            listUrl.add(url);
        }
        return listUrl;
    }

    //删除文件
    //objectName:oss中的相对路径
    public void deleteFile(String objectName) {
        try {
            // 删除文件。
            ossClient.deleteObject(bucketName, objectName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
    }

    //查看文件是否已经存在:默认不存在  没怎么必要,上传是写入了uuid唯一标识

    /**
     * @param objectName:文件的相对路径
     * @return boolean
     * @author xinggang
     * @create 2024/10/31
     **/
    public boolean isExist(String objectName) {

        boolean found = false;
        try {
            // 判断文件是否存在。如果返回值为true，则文件存在，否则存储空间或者文件不存在。
            // 设置是否进行重定向或者镜像回源。默认值为true，表示忽略302重定向和镜像回源；如果设置isINoss为false，则进行302重定向或者镜像回源。
            //boolean isINoss = true;
            found = ossClient.doesObjectExist(bucketName, objectName);
            //boolean found = ossClient.doesObjectExist(bucketName, objectName, isINoss);
            return found;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
        }
        return found;
    }

    // 生成带签名的临时访问 URL,用于URL安全
    // filename:oss中的相对路径
    // expires:过期时间(分钟)

    public String generatePresignedUrl(String filename, long expirationInSeconds) {
        // 设置 URL 过期时间
        Date expiration = new Date(System.currentTimeMillis() + expirationInSeconds * 1000);

        // 生成带签名的 URL
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filename);
        request.setExpiration(expiration);

        URL url = ossClient.generatePresignedUrl(request);
        // 返回 URL 字符串
        return url.toString();
    }

    public byte[] getObject(String pathUrl) {
        //初始化
        ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        String key = pathUrl.replace(ENDPOINT + "", "").replaceAll(bucketName + ".", "").replaceAll("https://", "");

        int index = key.indexOf("/");
//        String bucketName = key.substring(0, index);
        String filePath = key.substring(index + 1);
        InputStream inputStream = null;
        try {
            // 获取文件输入流
            inputStream = ossClient.getObject(bucketName, filePath).getObjectContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 读取文件内容
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while (true) {
            try {
                if (!((len = inputStream.read(buffer)) != -1)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            outputStream.write(buffer, 0, len);
        }

        return outputStream.toByteArray();
    }

    //关闭oss服务
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

}
