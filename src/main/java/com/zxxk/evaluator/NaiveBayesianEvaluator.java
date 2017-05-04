package com.zxxk.evaluator;

import com.zxxk.dao.BaseInfoDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.data.Data;
import com.zxxk.learner.Labels;
import com.zxxk.util.MaxValuedLabel;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 17-5-2.
 */
public abstract class NaiveBayesianEvaluator implements Evaluator {

    private List<String> labels;

    private int trainingDataSize;

    private int courseId;

    @Resource
    private BaseInfoDao baseInfoDao;

    @Resource
    private LabelDao labelDao;

    @Override
    public MultiLabelPrediction predictMultiLabel(Data data) {
        return null;
    }

    @Override
    public EvaluationResult evaluateMultiLabel(List<Data> testingData) {

//        List<Result> results = new ArrayList<>();
//        if (CollectionUtils.isEmpty(testingData)) {
//            throw new ClassificationException("testing data should not be empty!");
//        }
//        System.out.println("training start !!");
//        long start = System.currentTimeMillis();
//        // 先进行训练
////        this.train();
//        long end = System.currentTimeMillis();
//        System.out.println("training done !! use time : " + (end - start) / 1000);
//        System.out.println();
//        System.out.println("evaluating start !!");
//
//        BaseInfo baseInfo = baseInfoDao.get(courseId);
//        List<Label> labelList = labelDao.getAll(courseId);
//        List<String> labelNames = labelList.stream().map(label -> label.getName()).collect(Collectors.toList());
//
//        EvaluationResult evaluationResult = new EvaluationResult();
//
//        for (Data data : testingData) {
//
//            data.setLabels(filterValidLabels(data.getLabels()));
//
//            List<String> featuresOfData = data.getFeatures();
//
//            if (CollectionUtils.isEmpty(featuresOfData)) {
//                evaluationResult.undonePlus();
//                System.out.println("undone : " + data.getId());
//                continue;
//            }
//
//            // 初始化labelValue
//            double[][] labelValue = new double[labels.size()][2];
//            for (int i = 0; i < labelValue.length; i++) {
//                labelValue[i][0] = 1.0 * labelList.get(i).getCount();
//                labelValue[i][1] = 1.0 * (baseInfo.getDataSize() - labelList.get(i).getCount());
//            }
//
//            for (String featureName : featuresOfData) {
//                double[] featureValue = new double[2];
//
//                List<Feature> featuresInTrainingData = featureDao.getByName(featureName);
//                if (CollectionUtils.isEmpty(featuresInTrainingData)) continue;
////                int featureIndex = features.indexOf(feature);
////                if (featureIndex < 0) continue;
//                // list转map
//                Map<String, Integer> featureMap = featuresInTrainingData.stream()
//                        .collect(Collectors.toMap(
//                                feature1 -> courseId + SEPERATOR + feature1.getName() + SEPERATOR + feature1.getLabel(),
//                                feature1 -> feature1.getCount()));
//
//
//                for (int i = 0; i < labelList.size(); i++) {
//
////                    int presentCount = features.getCounts(i).get(featureIndex);
//                    // 当前标签下，此特征出现的概率
//                    String keyOfCurLabel = courseId + SEPERATOR + featureName + SEPERATOR + labelList.get(i).getName();
//                    double presentCount = featureMap.get(keyOfCurLabel) == null ? 0.1 : featureMap.get(keyOfCurLabel);
//                    if (data.getId().equals("1570736451715072")) {
//                        System.out.println("present : " + featureName + ", " + labelList.get(i).getName() + ", " + (presentCount / labelList.get(i).getCount()));
//                    }
//                    labelValue[i][0] *= presentCount / labelList.get(i).getCount();
//
//                    // 非当前标签下，此特征出现的概率
//                    String keyOfTotal = courseId + SEPERATOR + featureName + SEPERATOR + "_total";
//
//                    double absentCount = featureMap.get(keyOfTotal) - presentCount;
//                    double absentTotal = baseInfo.getDataSize() - labelList.get(i).getCount();
//                    if (data.getId().equals("1570736451715072")) {
//                        System.out.println("absent : " + featureName + ", " + labelList.get(i).getName() + ", " + (absentCount == 0 ? 0.1 / absentTotal : absentCount * 1.0 / absentTotal));
//                        System.out.println("===================================");
//                    }
//                    labelValue[i][1] *= absentCount == 0 ? 0.1 / absentTotal : absentCount * 1.0 / absentTotal;
//
//                }
//                // 平衡label中的值，使得里面的值不会太小
//                balanceValue(labelValue);
//            }
//            Result result = new Result(data.getId(), data.getLabels(), labelValue, labelNames);
//            results.add(result);
//
//            for (int i = 0; i < labelValue.length; i++) {
//                double[] values = labelValue[i];
//                if (data.getId().equals("1570736451715072")) {
//                    System.out.println(labels.get(i) + ", " + data.getId() + ", values : " + Arrays.toString(values) + ", label : " + data.getLabels());
//                }
//            }
////            System.out.println("==========================");
//        }
//        try {
//            FileWriter fileWriter = new FileWriter("/home/wangwei/Dev/data/result1.txt", false);
//
//            for (Result result : results) {
//                fileWriter.append(result.getId() + ", " + result.getLabels());
//                fileWriter.append("\n");
//
//                for (int i = 0; i < result.getScores().length; i++) {
//                    fileWriter.append(labels.get(i) + " values : " + Arrays.toString(result.getScores()[i]));
//                    fileWriter.append("\n");
////                    System.out.println("values : " + Arrays.toString(value));
//                }
//
//                if (result.getResult()) {
//                    evaluationResult.successPlus();
//                    fileWriter.write("true labels are " + result.getLabels() + ", predicted labels are " + result.getPredictedLabels() + ", so predict success!");
//                    fileWriter.append("\n");
////                    System.out.println("true labels are "+result.getLabels()+", predicted labels are "+result.getPredictedLabels()+", so predict success!");
//                } else {
//                    evaluationResult.failedPlus();
//                    fileWriter.write("true labels are " + result.getLabels() + ", predicted labels are " + result.getPredictedLabels() + ", so predict failed!");
//                    fileWriter.append("\n");
////                    System.out.println("true labels are "+result.getLabels()+", predicted labels are "+result.getPredictedLabels()+", so predict failed!");
//                }
//                fileWriter.append("============================================");
//                fileWriter.append("\n");
////                System.out.println("====================================");
//            }
//
//            fileWriter.flush();
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return evaluationResult;
        return null;
    }

    /**
     * 清除无效的label
     *
     * @param labelsOfData
     * @return
     */
    private List<String> filterValidLabels(List<String> labelsOfData) {
        // clear invalid labels
        List<String> finalLabels = new ArrayList<>();
        for (String label : labelsOfData) {
            if (labels.indexOf(label) >= 0) {
                finalLabels.add(label);
            }
        }
        if (CollectionUtils.isEmpty(finalLabels)) {
            finalLabels.add(Labels.LABEL_OTHER);
        }
        return finalLabels;
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
