package com.zxxk.controller.vo;

import java.util.List;

/**
 * Created by wangwei on 17-5-4.
 */
public class KPointVO {
    /**
     * 试题真实的标签
     */
    private List<String> labels;
    /**
     * 预测出的标签
     */
    private List<String> predictedLabels;

    public KPointVO(List<String> labels, List<String> predictedLabels) {
        this.labels = labels;
        this.predictedLabels = predictedLabels;
    }

    public KPointVO() {
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getPredictedLabels() {
        return predictedLabels;
    }

    public void setPredictedLabels(List<String> predictedLabels) {
        this.predictedLabels = predictedLabels;
    }
}
