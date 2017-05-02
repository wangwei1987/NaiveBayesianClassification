package com.zxxk.learner;

import com.zxxk.domain.Data;
import com.zxxk.exception.ClassificationException;
import com.zxxk.util.MaxValuedLabel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
@Component
public class Learner {

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

    public Learner() {

    }

    public void train() {

        for (Data data : trainingData) {
            data.setLabels(filterValidLabels(data.getLabels()));
            labels.countPlus(data.getLabels());

            List<String> featuresInData = data.getFeatures();
            List<String> labelsOfData = data.getLabels();

            // labels is empty or invalid, set the label to _other
            if(CollectionUtils.isEmpty(labelsOfData)) {
                // if label is empty, set its label to _other
                labelsOfData = new ArrayList<>();
                labelsOfData.add(Labels.LABEL_OTHER);
            }
            else {
                // clear invalid labels
                data.setLabels(filterValidLabels(labelsOfData));
                labelsOfData = data.getLabels();
            }

            for (String feature : featuresInData) {
                if (!features.contains(feature)) {
                    features.addFeature(feature);

                    features.initCount(0, 1);
                    for(String label : labelsOfData) {
                        int labelIndex = labels.indexOf(label);
                        features.plus(labelIndex + 1, features.size() - 1);
                    }
                } else {
                    int featureIndex = features.indexOf(feature);

                    features.plus(0, featureIndex);
                    for(String label : labelsOfData) {
                        int labelIndex = labels.indexOf(label);
                        features.plus(labelIndex + 1, featureIndex);
                    }
                }
            }
        }

//        for (int i = 0; i < features.size(); i++) {
//            if (i != 0 && i % 6 == 0) {
//                System.out.println();
//            }
//            String countsStr = " : (";
//            for (int j = 0; j < features.getAllCounts().size(); j++) {
//                countsStr += features.getCounts(j).get(i) + ", ";
//            }
//            System.out.printf(features.getFeature(i) + countsStr + ") |||  ");
//        }
//        System.out.println();
    }

    public EvaluationResult evaluate() {
        if (CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("testing data should not be empty!");
        }
        System.out.println("training start !!");
        // 先进行训练
        this.train();
        System.out.println("training done !!");
        System.out.println();
        System.out.println("evaluating start !!");

        EvaluationResult evaluationResult = new EvaluationResult();

        for (Data data : testingData) {

            data.setLabels(filterValidLabels(data.getLabels()));

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

                    curLabelValue *= features.getCounts(i).get(featureIndex) == 0 ?
                            0.1 / labels.getCount(data.getLabel()) :
                            features.getCounts(i).get(featureIndex) * 1.0 / labels.getCount(data.getLabel());
                    labelValue[i - 1] = curLabelValue;
                }
                // 平衡label中的值，使得里面的值不会太小
                balanceArrayValue(labelValue);
            }
            if (getMax(labelValue).isMulti()) {
                evaluationResult.undonePlus();
//                System.out.println("undone : " + data.getId() + ", values : " + Arrays.toString(labelValue) + ", label : " + data.getLabel());
            } else if (labels.get(getMax(labelValue).getIndex()).equals(data.getLabel())) {
                evaluationResult.successPlus();
//                System.out.println("success : " + data.getId() + ", values : " + Arrays.toString(labelValue) + ", label : " + data.getLabel());
            } else {
                evaluationResult.failedPlus();
//                System.err.println("failed : " + data.getId() + ", values : " + Arrays.toString(labelValue) + ", label : " + data.getLabel());
            }
        }
        return evaluationResult;
    }

    public EvaluationResult multiLabelEvaluate() {
        List<Result> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("testing data should not be empty!");
        }
        System.out.println("training start !!");
        long start = System.currentTimeMillis();
        // 先进行训练
        this.train();
        long end = System.currentTimeMillis();
        System.out.println("training done !! use time : " + (end - start) / 1000);
        System.out.println();
        System.out.println("evaluating start !!");

        EvaluationResult evaluationResult = new EvaluationResult();




        for (Data data : testingData) {

            data.setLabels(filterValidLabels(data.getLabels()));

            List<String> featuresOfData = data.getFeatures();

            if (CollectionUtils.isEmpty(featuresOfData)) {
                evaluationResult.undonePlus();
//                System.out.println("undone : " + data.getId());
                continue;
            }

            // 初始化labelValue
            double[][] labelValue = new double[labels.size()][2];
            for (int i = 0; i < labelValue.length; i++) {
                labelValue[i][0] = 1.0 * labels.getCount(i);
                labelValue[i][1] = 1.0 * (trainingData.size() - labels.getCount(i));
            }

            for (String feature : featuresOfData) {
                double[] featureValue = new double[2];

                int featureIndex = features.indexOf(feature);
                if (featureIndex < 0) continue;

                for (int i = 0; i < labels.size(); i++) {


                    int presentCount = features.getCounts(i + 1).get(featureIndex);
                    if (data.getId().equals("1569739163893760")) {
                        System.out.printf(feature);
                        System.out.printf(", presentCount : " + presentCount);
                        System.out.printf(", labelcount : " + labels.getCount(i));
                        System.out.println();
                    }
//                    labelValue[i-1][0] *= presentCount == 0 ?
//                            0.1 / labels.getCount(data.getLabel()) :
//                            presentCount * 1.0 / labels.getCount(data.getLabel());
                    labelValue[i][0] *= presentCount == 0 ?
                            0.1 / labels.getCount(data.getLabel()) :
                            presentCount * 1.0 / labels.getCount(i);

                    int absentCount = features.getCounts(0).get(featureIndex);
//                    int absentTotal = trainingData.size() - labels.getCount(data.getLabel());
                    int absentTotal = trainingData.size() - labels.getCount(i);
                    if (data.getId().equals("1569739163893760")) {
                        System.out.printf(", absentCount : " + absentCount);
                        System.out.printf(", featuresCounts : " + features.getCounts(0).get(featureIndex));
                        System.out.printf(", trainingDataSize : " + trainingData.size());
                        System.out.printf(", absentTotal : " + absentTotal);
                        System.out.println("======================");
                    }
                    labelValue[i][1] *= absentCount == 0 ?
                            0.1 / absentTotal :
                            absentCount * 1.0 / absentTotal;

                }
                // 平衡label中的值，使得里面的值不会太小
                balanceValue(labelValue);
            }
            Result result = new Result(data.getId(), data.getLabels(), labelValue, labels.getNames());
            results.add(result);

            for (int i = 0; i < labelValue.length; i++) {
                double[] values = labelValue[i];
//                System.out.println(labels.get(i)+", "+data.getId()+" value : " + data.getId() + ", values : " + Arrays.toString(values) + ", label : " + data.getLabels());

                if (getMax(values).isMulti()) {
                    evaluationResult.undonePlus();
                    System.out.println(i + " undone : " + data.getId() + ", values : " + Arrays.toString(values) + ", label : " + data.getLabels());
                } else if (labels.get(getMax(values).getIndex()).equals(data.getLabel())) {
                    evaluationResult.successPlus();
                    System.out.println(i + " success : " + data.getId() + ", values : " + Arrays.toString(values) + ", label : " + data.getLabels());
                } else {
                    evaluationResult.failedPlus();
                    System.err.println(i + " failed : " + data.getId() + ", values : " + Arrays.toString(values) + ", label : " + data.getLabels());
                }
            }
            System.out.println("==========================");
        }
//        EvaluationResult evaluationResult1 = new EvaluationResult();
        try {
            FileWriter fileWriter = new FileWriter("/home/wangwei/Dev/data/result.txt", false);

            for (Result result : results) {
                fileWriter.append(result.getId() + ", " + result.getLabels());
                fileWriter.append("\n");
                //            System.out.println(result.getId()+", "+result.getLabels());
//                for (double[] value : result.getScores()) {
                for (int i = 0; i < result.getScores().length; i++) {
                    fileWriter.append(labels.get(i) + " values : " + Arrays.toString(result.getScores()[i]));
                    fileWriter.append("\n");
//                    System.out.println("values : " + Arrays.toString(value));
                }

                if (result.getResult()) {
                    evaluationResult.successPlus();
                    fileWriter.write("true labels are " + result.getLabels() + ", predicted labels are " + result.getPredictedLabels() + ", so predict success!");
                    fileWriter.append("\n");
//                    System.out.println("true labels are "+result.getLabels()+", predicted labels are "+result.getPredictedLabels()+", so predict success!");
                } else {
                    evaluationResult.failedPlus();
                    fileWriter.write("true labels are " + result.getLabels() + ", predicted labels are " + result.getPredictedLabels() + ", so predict failed!");
                    fileWriter.append("\n");
//                    System.out.println("true labels are "+result.getLabels()+", predicted labels are "+result.getPredictedLabels()+", so predict failed!");
                }
                fileWriter.append("============================================");
                fileWriter.append("\n");
//                System.out.println("====================================");
            }

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evaluationResult;
    }

    public Prediction predict() {
        return null;
    }


    private void balanceValue(double[][] arrs) {
        for (double[] arr : arrs) {
            balanceArrayValue(arr);
        }
    }
    /**
     * 使数组中最大的值不小于1
     *
     * @param arr
     */
    private void balanceArrayValue(double[] arr) {
        if (arr[getMax(arr).getIndex()] < 1) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] *= 10;
            }
            balanceArrayValue(arr);
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

    private List<String> filterValidLabels(List<String> labelsOfData) {
        // clear invalid labels
        List<String> finalLabels = new ArrayList<>();
        for(String label : labelsOfData) {
            if(labels.indexOf(label) >= 0) {
                finalLabels.add(label);
            }
        }
        if(CollectionUtils.isEmpty(finalLabels)) {
            finalLabels.add(Labels.LABEL_OTHER);
        }
        return finalLabels;
    }

    public List<Data> getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(List<Data> trainingData) {
        this.trainingData = trainingData;
    }

    public List<Data> getTestingData() {
        return testingData;
    }

    public void setTestingData(List<Data> testingData) {
        this.testingData = testingData;
    }

    public Labels getLabels() {
        return labels;
    }

    public void setLabels(Labels labels) {
        this.labels = labels;
        init();
    }

}
