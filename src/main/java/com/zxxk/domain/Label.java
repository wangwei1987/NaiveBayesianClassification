package com.zxxk.domain;

/**
 * Created by wangwei on 17-4-28.
 */
public class Label {
    /**
     * 标签名
     */
    private String name;
    /**
     * 标签的数量
     */
    private int count;
    /**
     * 课程
     */
    private int courseId;

    public Label(int courseId, String name, int count) {
        this.courseId = courseId;
        this.name = name;
        this.count = count;
    }

    public Label() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
