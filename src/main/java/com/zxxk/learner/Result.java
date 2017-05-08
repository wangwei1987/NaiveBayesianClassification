package com.zxxk.learner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by wangwei.
 */
public class Result {

    private String id;

    private Map<String, double[]> labelScores;

    private List<String> labels;

    public Result(String id, List<String> labels, Map<String, double[]> labelScores) {
        this.id = id;
        this.labels = labels;
        this.labelScores = labelScores;
    }

    public Result(String id, List<String> labels) {
        this.id = id;
        this.labels = labels;
    }

    public Result() {

    }

    public List<String> getPredictedLabels() {

        List<String> predictedLabels = new ArrayList<>();

        for (Map.Entry<String, double[]> entry : labelScores.entrySet()) {
            double[] values = entry.getValue();
            // 当标签选择和不选择的概率之商大于4，亦即选择的概率大于80%，不选择的概率小于20%时，此条数据才设置此标签
            if (values[0] / values[1] > 4) {
                predictedLabels.add(entry.getKey());
            }
        }

        if (predictedLabels.size() <= 0) {
            predictedLabels.add(Labels.LABEL_OTHER);
        }
        return predictedLabels.stream().sorted().collect(Collectors.toList());
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

    public boolean getResult() {

        return matchResult(getPredictedLabels(), labels);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public Map<String, double[]> getLabelScores() {
        return labelScores;
    }

    public void setLabelScores(Map<String, double[]> labelScores) {
        this.labelScores = labelScores;
    }

    public double[] getLabelScore(String labelName) {
        return labelScores.get(labelName);
    }
}
