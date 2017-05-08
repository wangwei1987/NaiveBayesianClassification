package com.zxxk.trainer;

import com.zxxk.data.Data;
import com.zxxk.domain.Label;
import com.zxxk.exception.ClassificationException;
import com.zxxk.learner.Labels;
import com.zxxk.util.LabelUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * naive bayesian com.zxxk.trainer
 * Created by wangwei on 17-5-2.
 */
public abstract class NaiveBayesianTrainer implements Trainer {

    // seperator between feature and labels
    public static String SEPERATOR = "###";

    private Map<String, Integer> featuresToRestore = new HashMap<>();

    private Map<String, Integer> labelsToRestore = new HashMap<>();

    // TODO: 此属性完全可以从labelsToRestore中提取，
    // 但是如果用户指定了训练的label，它到有一定的保留价值
    // 先留着，以后再作评估
    private List<String> allLabels;

    private boolean specifiedLabels = false;

    // 当features的数量达到此阈值时，将触发保存操作,不需要此功能的话，可以将此值设为0
    private int featureThreshold = 0;

    // 保存学习成果
    public abstract void save(int trainingDataSize, Map<String, Integer> featuresToRestore,
                              List<String> allLabels, Map<String, Integer> labelsToRestore);

    @Override
    public void train(List<Data> trainingData) {

        for (Data data : trainingData) {

            List<String> featuresInData = data.getFeatures();
            List<String> labelsOfData = data.getLabels();

            // 如果用户指定只训练某些标签，则要先滤掉无效的标签
            if (specifiedLabels) {
                data.setLabels(LabelUtils.filterValidLabels(data.getLabels(), allLabels));
                labelsOfData = data.getLabels();
            } else {
                collectLabels(data.getLabels());
            }

            // 如果此数据没有有效的label，则将它的label设置为LABEL_OTHER
            if (CollectionUtils.isEmpty(labelsOfData)) {
                labelsOfData = new ArrayList<>();
                labelsOfData.add(Labels.LABEL_OTHER);
            }

            data.getLabels().stream().forEach(label -> {
                if (labelsToRestore.containsKey(label)) {
                    labelsToRestore.put(label, labelsToRestore.get(label) + 1);
                } else {
                    labelsToRestore.put(label, 1);
                }
            });

            for (String feature : featuresInData) {
                addFeatureCount(feature + SEPERATOR + "_total");
                for (String label : labelsOfData) {
                    addFeatureCount(feature + SEPERATOR + label);
                }
            }

            // 待保存的feature的数量超出阈值时，触发保存操作
            if (featureThreshold > 0 && featuresToRestore.size() > featureThreshold) {
                // TODO: 17-5-6
            }
        }
        save(trainingData.size(), featuresToRestore, allLabels, labelsToRestore);
    }


    public void train(List<Data> trainingData, List<String> allLabels) {
        this.allLabels = allLabels;
        this.specifiedLabels = true;

        this.train(trainingData);
    }


    private void collectLabels(List<String> labels) {
        if (CollectionUtils.isEmpty(labels))
            return;
        if (CollectionUtils.isEmpty(allLabels)) {
            allLabels = new ArrayList<>();
            allLabels.add(Label.LABEL_OTHER);
        }
        labels.stream().forEach(label -> {
            if (!allLabels.contains(label)) {
                allLabels.add(label);
            }
        });
    }

    private void addFeatureCount(String key) {
        if (featuresToRestore.containsKey(key) && !featuresToRestore.containsKey(key.substring(0, key.lastIndexOf("#") + 1) + "_total")) {
            System.out.println(key);
        }
        if (featuresToRestore.containsKey(key)) {
            featuresToRestore.put(key, featuresToRestore.get(key) + 1);
        } else {
            featuresToRestore.put(key, 1);
        }
    }

    private void checkLabels() {
        if (CollectionUtils.isEmpty(allLabels)) {
            throw new ClassificationException("labels can not be empty!");
        }
    }

    /**
     * 重置变量
     */
    public void clean() {
    }

    public void setFeatureThreshold(int featureThreshold) {
        this.featureThreshold = featureThreshold;
    }

}
