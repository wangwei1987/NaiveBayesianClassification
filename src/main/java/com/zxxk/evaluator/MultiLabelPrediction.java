package com.zxxk.evaluator;

import com.zxxk.domain.Label;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 多标签分类器的分类结果
 * Created by shiti on 17-4-20.
 */
public class MultiLabelPrediction {

    private List<String> allLabels;

//    private double[][] scores;

    private Map<String, double[]> labelScores;

    private List<String> predictedLabels = new ArrayList<>();

    public MultiLabelPrediction(List<String> allLabels, Map<String, double[]> labelScores) {
        this.allLabels = allLabels;
        this.labelScores = labelScores;
        parsePredictedLabels();
    }

    public MultiLabelPrediction() {
    }

    /**
     * 获取预测的label
     */
    private void parsePredictedLabels() {
        for (String labelName : allLabels) {
            double[] scores = labelScores.get(labelName);
            if (scores[0] / scores[1] > 4) {
                predictedLabels.add(labelName);
            }
        }
        if (CollectionUtils.isEmpty(predictedLabels)) {
            predictedLabels.add(Label.LABEL_OTHER);
        }
    }

    public List<String> getPredictedLabels() {
        return predictedLabels;
    }

    public Map<String, double[]> getLabelScores() {
        return labelScores;
    }

    @Override
    public String toString() {
        return String.join(",", predictedLabels);
    }
}
