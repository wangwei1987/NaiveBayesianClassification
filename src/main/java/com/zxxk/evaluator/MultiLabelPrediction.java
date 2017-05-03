package com.zxxk.evaluator;

import java.util.List;

/**
 * 多标签分类器的分类结果
 * Created by shiti on 17-4-20.
 */
public class MultiLabelPrediction {

    private List<String> allLabels;

    private double[][] scores;

    private List<String> predictedLabels;

    public MultiLabelPrediction(List<String> allLabels, double[][] scores) {
        this.allLabels = allLabels;
        this.scores = scores;
    }

    public List<String> getPredictedLabels() {
        return predictedLabels;
    }
}
