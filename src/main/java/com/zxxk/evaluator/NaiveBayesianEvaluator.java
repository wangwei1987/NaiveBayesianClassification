package com.zxxk.evaluator;

import com.zxxk.data.Data;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import com.zxxk.exception.ClassificationException;
import com.zxxk.learner.Result;
import com.zxxk.trainer.NaiveBayesianTrainer;
import com.zxxk.util.LabelUtils;
import com.zxxk.util.MaxValuedLabel;
import org.apache.commons.collections4.CollectionUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wangwei.
 */
public abstract class NaiveBayesianEvaluator implements Evaluator {

    protected List<Label> evaluatingLabels;

    private List<String> evaluatingLabelNames;

    public abstract long getTrainingSize();

    public abstract List<Feature> getFeaturesByName(String featureName, List<Label> evaluatingLabels);

    public abstract List<Label> getAllLabels();

    public Result predictMultiLabel(Data data, List<Label> evaluatingLabels) {
        if (CollectionUtils.isEmpty(evaluatingLabels)) {
            throw new ClassificationException("evaluating labels can't be empty!");
        }
        this.evaluatingLabels = evaluatingLabels;
        return predictMultiLabel(data);
    }

    public Result predictMultiLabel(Data data) {

        data.setLabels(LabelUtils.filterValidLabels(data.getLabels(), evaluatingLabelNames));

        Result result = new Result(data.getId(), data.getLabels());

        checkEvaluatingLabels();


        List<String> featuresOfData = data.getFeatures();

        if (CollectionUtils.isEmpty(featuresOfData)) {
            return null;
        }

        // 初始化labelvalues(labelvalues存储的是此条数据在各个标签下正反得分)
        Map<String, double[]> labelScores = new HashMap<>();
        for (Label label : evaluatingLabels) {
            double[] values = new double[2];
            values[0] = label.getCount();
            values[1] = getTrainingSize() - label.getCount();
            labelScores.put(label.getName(), values);
        }
        result.setLabelScores(labelScores);

        for (String featureName : featuresOfData) {

            List<Feature> featuresInTrainingData = getFeaturesByName(featureName, evaluatingLabels);
            if (CollectionUtils.isEmpty(featuresInTrainingData)) continue;

            // list转map
            Map<String, Integer> featureMap = featuresInTrainingData.stream()
                    .collect(Collectors.toMap(
                            feature1 -> feature1.getName() + NaiveBayesianTrainer.SEPERATOR + feature1.getLabel(),
                            feature1 -> feature1.getCount()));

            for (Label label : evaluatingLabels) {
                double[] values = labelScores.get(label.getName());

                // 当前标签下，此特征出现的概率
                String keyOfCurLabel = featureName + NaiveBayesianTrainer.SEPERATOR + label.getName();
                double presentCount = featureMap.get(keyOfCurLabel) == null ? 0.1 : featureMap.get(keyOfCurLabel);

                values[0] *= presentCount / label.getCount();

                // 非当前标签下，此特征出现的概率
                String keyOfTotal = featureName + NaiveBayesianTrainer.SEPERATOR + "_total";

                double absentCount = featureMap.get(keyOfTotal) - presentCount;
                double absentTotal = getTrainingSize() - label.getCount();

                values[1] *= absentCount == 0 ? 0.1 / absentTotal : absentCount * 1.0 / absentTotal;
            }
            // 平衡label中的值，使得里面的值不会太小
            balanceValue(labelScores);
        }
        return result;
    }

    public EvaluationResult evaluateMultiLabel(List<Data> testingData) {

        checkEvaluatingLabels();

        List<Result> results = new ArrayList<>();

        // 检查testingData是否为空
        checkTestData(testingData);

//        List<String> labelNames = evaluatingLabels.stream().map(label -> label.getName()).collect(Collectors.toList());

        EvaluationResult evaluationResult = new EvaluationResult();

        for (Data data : testingData) {

            // 根据feature预测labels
            Result result = predictMultiLabel(data);
            if (result == null) continue;

            results.add(result);
            if (result.getResult()) {
                evaluationResult.successPlus();
            } else {
                evaluationResult.failedPlus();
            }
        }

        saveToLocal(results, evaluatingLabels);
        return evaluationResult;
    }

    public EvaluationResult evaluateMultiLabel(List<Data> testingData, List<Label> evaluatingLabels) {
        if (CollectionUtils.isEmpty(evaluatingLabels)) {
            throw new ClassificationException("evaluating labels can't be empty!");
        }

        this.evaluatingLabels = evaluatingLabels;
        this.evaluatingLabelNames = evaluatingLabels.stream().map(label -> label.getName()).collect(Collectors.toList());
        return evaluateMultiLabel(testingData);
    }

    /**
     * 如果用户没有设置要验证的标签，则验证训练时训练出的所有标签
     */
    private void checkEvaluatingLabels() {
        if (evaluatingLabels == null)
            evaluatingLabels = getAllLabels();
    }


    private void checkTestData(List<Data> testingData) {
        if (CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("testing data should not be empty!");
        }
    }

    private void saveToLocal(List<Result> results, List<Label> evaluatingLabels) {
        EvaluationResult evaluationResult = new EvaluationResult();
        try {
            FileWriter fileWriter = new FileWriter("/home/wangwei/Dev/data/result1.txt", false);

            for (Result result : results) {
                fileWriter.append(result.getId() + ", " + result.getLabels());
                fileWriter.append("\n");

                for (Label label : evaluatingLabels) {
                    fileWriter.append(label.getName() + " values : " + Arrays.toString(result.getLabelScore(label.getName())));
                    fileWriter.append("\n");
//                    System.out.println("values : " + Arrays.toString(value));
                }
                fileWriter.write(result.getId() + ", true labels are " + result.getLabels());
                fileWriter.write("\n");
                fileWriter.write("predicted labels are " + result.getPredictedLabels());
                fileWriter.write("\n");
                if (result.getResult()) {
                    evaluationResult.successPlus();
                    fileWriter.write("so predict success!");
                    fileWriter.append("\n");
//                    System.out.println("true labels are "+result.getLabels()+", predicted labels are "+result.getPredictedLabels()+", so predict success!");
                } else {
                    evaluationResult.failedPlus();
                    fileWriter.write("so predict failed!");
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

    private void balanceValue(Map<String, double[]> labelValues) {
        // TODO: 17-5-5 rerealize this method
        for (double[] values : labelValues.values()) {
            balanceArrayValue(values);
        }
//        for (double[] arr : arrs) {
//            balanceArrayValue(arr);
//        }
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

    public List<Label> getevaluatingLabels() {
        return evaluatingLabels;
    }

    public void setevaluatingLabels(List<Label> evaluatingLabels) {
        this.evaluatingLabels = evaluatingLabels;
    }

}
