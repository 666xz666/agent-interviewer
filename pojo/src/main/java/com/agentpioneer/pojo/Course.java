package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程视频表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID，主键
     */
    @TableId(value = "course_id", type = IdType.AUTO)
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 视频链接
     */
    private String courseLink;

    /**
     * 课程描述
     */
    private String courseDescription;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 针对的岗位ID列表
     */
    private String targetJobs;

    /**
     * 上传者ID（关联user表）
     */
    private Long uploader;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 状态：1-启用，0-禁用
     */
    private Byte status;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseLink() {
        return courseLink;
    }

    public void setCourseLink(String courseLink) {
        this.courseLink = courseLink;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getTargetJobs() {
        return targetJobs;
    }

    public void setTargetJobs(String targetJobs) {
        this.targetJobs = targetJobs;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Course{" +
        "courseId = " + courseId +
        ", courseName = " + courseName +
        ", courseLink = " + courseLink +
        ", courseDescription = " + courseDescription +
        ", courseType = " + courseType +
        ", targetJobs = " + targetJobs +
        ", uploader = " + uploader +
        ", uploadTime = " + uploadTime +
        ", status = " + status +
        "}";
    }
}
