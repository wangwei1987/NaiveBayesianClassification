package com.zxxk.learner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei on 17-4-27.
 */
public class Result2 {

    private String id;

    private Map<String, double[]> labelScores;

    private List<String> selectedLabels;

    private double[][] scores;

    private List<String> allLabels;

    public Result2(String id, List<String> selectedLabels, double[][] scores, List<String> allLabels) {
        this.id = id;
        this.selectedLabels = selectedLabels;
        this.scores = scores;
        this.allLabels = allLabels;
    }

    public Result2() {

    }

    public void initLabelValues() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double[][] getScores() {
        return scores;
    }

    public void setScores(double[][] scores) {
        this.scores = scores;
    }

    public boolean getResult() {

        return matchResult(getPredictedLabels(), selectedLabels);
    }

    public List<String> getPredictedLabels() {

        List<String> predictedLabels = new ArrayList<>();

//        int otherLabelIndex = scores.length - 1;
//        if(scores[otherLabelIndex][0] / scores[otherLabelIndex][1] > 4) {
//            predictedLabels.add(Labels.LABEL_OTHER);
//        }
//        else {
        for (int i = 0; i < scores.length - 1; i++) {
            double[] score = scores[i];
            // 当标签选择和不选择的概率之商大于4，亦即选择的概率大于80%，不选择的概率小于20%时，此条数据才设置此标签
            if (score[0] / score[1] > 4) {
                predictedLabels.add(allLabels.get(i));
            }
        }
//        }

        if (predictedLabels.size() <= 0) {
            predictedLabels.add(Labels.LABEL_OTHER);
        }
        return predictedLabels;
    }

    private boolean matchResult(List<String> predictedLabels, List<String> labels) {
        boolean result = true;
        if (predictedLabels.size() != labels.size()) {
            return false;
        }
        for (int i = 0; i < labels.size(); i++) {
            if (!predictedLabels.contains(labels.get(i))) {
                result = false;
            }
        }
        return result;
    }

    public List<String> getSelectedLabels() {
        return selectedLabels;
    }

    public void setSelectedLabels(List<String> selectedLabels) {
        this.selectedLabels = selectedLabels;
    }
}
