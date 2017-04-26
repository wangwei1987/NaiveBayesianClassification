package com.zxxk.learner;

import com.zxxk.data.Data;
import com.zxxk.data.Features;
import com.zxxk.data.Labels;
import com.zxxk.exception.ClassificationException;
import com.zxxk.util.MaxValuedLabel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
public class Learner {

    // 所有的feature
//    private List<String> allFeatures = new ArrayList<>();

    // 各个label下feature的数量,其中第一个list保存的是出现的总次数
//    private List<List<Integer>> counts = new ArrayList<>();

    private Features features;

    // 标签
    private Labels labels;

    private List<Data> trainingData;

    private List<Data> testingData;

    private void init() {
        features = new Features(labels.size());
    }

    public Learner(List<Data> trainingData, List<Data> testingData, Labels labels) {
        if (CollectionUtils.isEmpty(trainingData) || CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("training data, testing data shouldn't be empty!");
        }
        this.trainingData = trainingData;
        this.testingData = testingData;
        this.labels = labels;
        init();
    }

    public Learner(List<Data> trainingData, Labels labels) {
        if (CollectionUtils.isEmpty(trainingData)) {
            throw new ClassificationException("training data shouldn't be empty!");
        }
        this.trainingData = trainingData;
        this.labels = labels;
        init();
    }

    public void train() {

        for (Data data : trainingData) {
            labels.countPlus(data.getLabels());

            List<String> featuresInData = data.getFeatures();
            int labelIndex = labels.indexOf(data.getLabel());
            List<String> labels = data.getLabels();

            for (String feature : featuresInData) {
                if (!features.contains(feature)) {
                    features.addFeature(feature);

                    for (int i = 0; i <= labels.size(); i++) {
                        if (i == labelIndex + 1 || i == 0) {
                            features.initCount(i, 1);
                        } else {
                            features.initCount(i, 0);
                        }
                    }
                } else {
                    int featureIndex = features.indexOf(feature);

                    features.plus(0, featureIndex);
                    features.plus(labelIndex + 1, featureIndex);
                }
            }
        }
    }

    public EvaluationResult evaluate() {
        if (CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("testing data should not be empty!");
        }
        // 先进行训练
        this.train();
        System.out.println("training done !!");

        EvaluationResult evaluationResult = new EvaluationResult();

        for (int i = 0; i < features.size(); i++) {
            if (i != 0 && i % 6 == 0) {
                System.out.println();
            }
            String countsStr = " : (";
            for (int j = 0; j < features.getAllCounts().size(); j++) {
                countsStr += features.getCounts(j).get(i) + ", ";
            }
            System.out.printf(features.getFeature(i) + countsStr + ") |||  ");
        }
        System.out.println();


        for (Data data : testingData) {

            List<String> featuresOfData = data.getFeatures();

            if (CollectionUtils.isEmpty(featuresOfData)) {
                evaluationResult.undonePlus();
                continue;
            }

            // 初始化labelValue
            double[] labelValue = new double[labels.size()];
            for (int i = 0; i < labelValue.length; i++) {
                labelValue[i] = 1.0 * labels.getCount(i);
            }

            for (String feature : featuresOfData) {

                int featureIndex = features.indexOf(feature);
                if (featureIndex < 0) continue;

                for (int i = 1; i <= labels.size(); i++) {
                    double curLabelValue = labelValue[i - 1];
//                    if(curLabelValue == 0) {
//                        curLabelValue = 1;
//                    }

                    curLabelValue *= features.getCounts(i).get(featureIndex) == 0 ?
                            0.1 / labels.getCount(data.getLabel()) :
                            features.getCounts(i).get(featureIndex) * 1.0 / labels.getCount(data.getLabel());
                    labelValue[i - 1] = curLabelValue;
                }
                // 平衡label中的值，使得里面的值不会太小
                balanceValue(labelValue);
            }
            if (getMax(labelValue).isMulti()) {
                evaluationResult.undonePlus();
                System.out.println("undone : " + data.getId() + ", values : " + Arrays.toString(labelValue) + ", label : " + data.getLabel());
            } else if (labels.get(getMax(labelValue).getIndex()).equals(data.getLabel())) {
                evaluationResult.successPlus();
                System.out.println("success : " + data.getId() + ", values : " + Arrays.toString(labelValue) + ", label : " + data.getLabel());
            } else {
                evaluationResult.failedPlus();
                System.err.println("failed : " + data.getId() + ", values : " + Arrays.toString(labelValue) + ", label : " + data.getLabel());
            }
        }
        return evaluationResult;
    }

    public Prediction predict() {
        return null;
    }

    /**
     * 使数组中最大的值不小于1
     *
     * @param arr
     */
    private void balanceValue(double[] arr) {
        if (arr[getMax(arr).getIndex()] < 1) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] *= 10;
            }
            balanceValue(arr);
        }
    }

    private MaxValuedLabel getMax(double[] arr) {
        int maxIndex = 0;
        boolean multi = false;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[maxIndex]) {
                maxIndex = i;
                multi = false;
            } else if (arr[i] == arr[maxIndex]) {
                multi = true;
            }
        }
        return new MaxValuedLabel(maxIndex, multi);
    }
}
