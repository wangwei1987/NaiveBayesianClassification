package com.zxxk.domain;

/**
 * Created by wangwei on 17-4-28.
 */
public class Feature {

    private int courseId;
    /**
     * 特征名
     */
    private String name;
    /**
     * 标签
     */
    private String label;
    /**
     * 该特征在此标签下的数量
     */
    private int count;

    public Feature(int courseId, String name, String label, int count) {
        this.courseId = courseId;
        this.name = name;
        this.label = label;
        this.count = count;
    }

    public Feature() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
