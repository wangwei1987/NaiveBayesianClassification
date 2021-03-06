package com.zxxk.data;

import com.zxxk.exception.ClassificationException;
import com.zxxk.util.SentenceAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
public class Data {

    private String id;

    private String stem;

    private String pointIds;

    private List<String> features;

//    private String label;

    private List<String> labels = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        if (labels.size() <= 0) {
            throw new ClassificationException("this data has no labes!");
        }
        return labels.get(0);
    }

    public void setLabel(String label) {
        labels = new ArrayList<>();
        this.labels.add(label);
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getPointIds() {
        return pointIds;
    }

    public void setPointIds(String pointIds) {
        this.pointIds = pointIds;
    }

    public void buildLabelsAndFeatures() {
        if (pointIds != null) this.setLabels(Arrays.asList(pointIds.split(",")));
        if (stem != null) this.setFeatures(SentenceAnalyzer.analyze(stem));
    }
}
