package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 知识库文件关联表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@TableName("knowledge_file")
public class KnowledgeFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID，主键
     */
    @TableId(value = "file_id", type = IdType.AUTO)
    private Long fileId;

    /**
     * 关联知识库ID
     */
    private Long knowledgeId;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件原名
     */
    private String fileName;

    /**
     * 文件类型（如PDF/WORD）
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 上传者ID（关联user表）
     */
    private Long uploader;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getUploader() {
        return uploader;
    }

    public void setUploader(Long uploader) {
        this.uploader = uploader;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "KnowledgeFile{" +
        "fileId = " + fileId +
        ", knowledgeId = " + knowledgeId +
        ", filePath = " + filePath +
        ", fileName = " + fileName +
        ", fileType = " + fileType +
        ", fileSize = " + fileSize +
        ", uploader = " + uploader +
        ", uploadTime = " + uploadTime +
        "}";
    }
}
