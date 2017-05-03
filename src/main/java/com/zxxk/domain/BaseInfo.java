package com.zxxk.domain;

/**
 * Created by wangwei on 17-4-29.
 */
public class BaseInfo {

    private long dataSize;

    private int courseId;

    public BaseInfo(int courseId, long dataSize) {
        this.courseId = courseId;
        this.dataSize = dataSize;
    }

    public BaseInfo() {
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
