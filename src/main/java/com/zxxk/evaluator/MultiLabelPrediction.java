package com.zxxk.evaluator;

import com.zxxk.domain.Label;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 多标签分类器的分类结果
 * Created by shiti on 17-4-20.
 */
public class MultiLabelPrediction {

    private List<String> allLabels;

    private double[][] scores;

    private List<String> predictedLabels = new ArrayList<>();

    public MultiLabelPrediction(List<String> allLabels, double[][] scores) {
        this.allLabels = allLabels;
        this.scores = scores;
        parsePredictedLabels();
    }

    private void parsePredictedLabels() {
        for (int i = 0; i < allLabels.size(); i++) {
            if (scores[i][0] / scores[i][1] > 4) {
                predictedLabels.add(allLabels.get(i));
            }
        }
        if (CollectionUtils.isEmpty(predictedLabels)) {
            predictedLabels.add(Label.LABEL_OTHER);
        }
    }

    public List<String> getPredictedLabels() {
        return predictedLabels;
    }

    @Override
    public String toString() {
        return String.join(",", predictedLabels);
    }
}
