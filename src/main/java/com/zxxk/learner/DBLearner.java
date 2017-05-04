package com.zxxk.learner;

import com.zxxk.dao.BaseInfoDao;
import com.zxxk.dao.FeatureDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.data.Data;
import com.zxxk.domain.BaseInfo;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import com.zxxk.evaluator.EvaluationResult;
import com.zxxk.evaluator.MultiLabelPrediction;
import com.zxxk.exception.ClassificationException;
import com.zxxk.util.MaxValuedLabel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import trainer.NaiveBayesianTrainer;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by shiti on 17-4-20.
 */
@Service
public class DBLearner {

    // 标签
//    private Labels labels;

//    private Features features;

    private List<String> labels;

    private List<Data> trainingData;

    private List<Data> testingData;

    private int courseId;

    private Map<String, Integer> featureToRestore = new HashMap<>();

    private Map<String, Integer> labelToRestore = new HashMap<>();

    private static final String SEPERATOR = "###";

    @Resource
    private FeatureDao featureDao;
    @Resource
    private LabelDao labelDao;
    @Resource
    private BaseInfoDao baseInfoDao;
    @Resource
    private LearnerSaver learnerSaver;

    public DBLearner() {

    }

    //    @Transactional
    public void train(boolean append) {
        if (!append) {
            // 训练前先删除上次的训练结果
            featureDao.clear(courseId);
            labelDao.clear(courseId);
            baseInfoDao.clear(courseId);
        }

        for (Data data : trainingData) {
            long trainStart = System.currentTimeMillis();
            data.setLabels(filterValidLabels(data.getLabels()));

            data.getLabels().stream().forEach(label -> {
                if (labelToRestore.containsKey(label)) {
                    labelToRestore.put(label, labelToRestore.get(label) + 1);
                } else {
                    labelToRestore.put(label, 1);
                }
            });

            List<String> featuresInData = data.getFeatures();
            List<String> labelsOfData = data.getLabels();

            // labels is empty or invalid, set the label to _other
            if (CollectionUtils.isEmpty(labelsOfData)) {
                // if label is empty, set its label to _other
                labelsOfData = new ArrayList<>();
                labelsOfData.add(Labels.LABEL_OTHER);
            } else {
                // clear invalid laels
                data.setLabels(filterValidLabels(labelsOfData));
                labelsOfData = data.getLabels();
            }

            for (String feature : featuresInData) {

                addFeatureCount(feature + SEPERATOR + "_total");
                for (String label : labelsOfData) {
                    addFeatureCount(feature + SEPERATOR + label);
                }
            }
//            if (featureToRestore.size() > 2000000) {
//                long start = System.currentTimeMillis();
//                System.out.println("train interrupt : "+(start - trainStart)/1000);
//                System.out.println("saving ...");
//                learnerSaver.saveFeatures(courseId, featureToRestore);
//                featureToRestore.clear();
//                long end = System.currentTimeMillis();
//                System.out.println("保存用时b ： " + (end - start) / 1000);
//            }
//            System.out.println("finished training! current size is "+featureToRestore.size());
        }


//        long start = System.currentTimeMillis();
//        learnerSaver.saveLabels(courseId, labels, labelToRestore);
//        learnerSaver.saveFeatures(courseId, featureToRestore);
//        learnerSaver.saveBaseInfo(courseId, trainingData.size());
//        long end = System.currentTimeMillis();
//        System.out.println("保存用时a ： " + (end - start) / 1000);

        System.out.println("All finished! total size is " + featureToRestore.size());


//        featureToRestore.clear();
        int i = 0;
//        for (Map.Entry<String, Integer> feature : featureToRestore.entrySet()) {
//            if (i != 0 && i % 6 == 0) {
//                System.out.println();
//            }
//            String countsStr = " : (";
//        }
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

    public void save(int trainingDataSize) {
        learnerSaver.saveBaseInfo(courseId, trainingDataSize);
        learnerSaver.saveLabels(courseId, labels, labelToRestore);

//        learnerSaver.saveFeatures(courseId, featureToRestore);
        List<Feature> features = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : featureToRestore.entrySet()) {
            String[] props = entry.getKey().split(NaiveBayesianTrainer.SEPERATOR);
            Feature feature = new Feature(courseId, props[0], props[1], entry.getValue());
            features.add(feature);

            if (features.size() > 10000) {
                long start = System.currentTimeMillis();
                learnerSaver.saveFeatures(features);
                long end = System.currentTimeMillis();
                System.out.println("finished insert : " + (end - start) / 1000);
                features.clear();
            }
        }
    }

    private void addFeatureCount(String key) {
        if (!key.contains("_total")) {
            String totalStr = key.substring(0, key.lastIndexOf("#") + 1);
            if (!featureToRestore.containsKey(totalStr + "_total")) {
                System.out.println();
            }
        }
        if (featureToRestore.containsKey(key)) {
            featureToRestore.put(key, featureToRestore.get(key) + 1);
        } else {
            featureToRestore.put(key, 1);
        }
    }


    public EvaluationResult multiLabelEvaluate(boolean train) {


        List<Result> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("testing data should not be empty!");
        }
        System.out.println("training start !!");
        long start = System.currentTimeMillis();
        if (train) {
            // 先进行训练
//            this.train(false);
        }

        long end = System.currentTimeMillis();
        System.out.println("training done !! use time : " + (end - start) / 1000);
        System.out.println();
        System.out.println("evaluating start !!");

        BaseInfo baseInfo = baseInfoDao.get(courseId);
        List<Label> labelList = labelDao.getAll(courseId);
        List<String> labelNames = labelList.stream().map(label -> label.getName()).collect(Collectors.toList());

        EvaluationResult evaluationResult = new EvaluationResult();

        for (Data data : testingData) {

            data.setLabels(filterValidLabels(data.getLabels()));

            List<String> featuresOfData = data.getFeatures();

            if (CollectionUtils.isEmpty(featuresOfData)) {
                evaluationResult.undonePlus();
                System.out.println("undone : " + data.getId());
                continue;
            }

            // 初始化labelValue
            double[][] labelValue = new double[labels.size()][2];
            for (int i = 0; i < labelValue.length; i++) {
                labelValue[i][0] = 1.0 * labelList.get(i).getCount();
                labelValue[i][1] = 1.0 * (baseInfo.getDataSize() - labelList.get(i).getCount());
            }

            for (String featureName : featuresOfData) {
                double[] featureValue = new double[2];

                List<Feature> featuresInTrainingData = featureDao.getByName(featureName);
                if (CollectionUtils.isEmpty(featuresInTrainingData)) continue;
//                int featureIndex = features.indexOf(feature);
//                if (featureIndex < 0) continue;
                // list转map
                Map<String, Integer> featureMap = featuresInTrainingData.stream()
                        .collect(Collectors.toMap(
                                feature1 -> courseId + SEPERATOR + feature1.getName() + SEPERATOR + feature1.getLabel(),
                                feature1 -> feature1.getCount()));


                for (int i = 0; i < labelList.size(); i++) {

//                    int presentCount = features.getCounts(i).get(featureIndex);
                    // 当前标签下，此特征出现的概率
                    String keyOfCurLabel = courseId + SEPERATOR + featureName + SEPERATOR + labelList.get(i).getName();
                    double presentCount = 1.0;

                    presentCount = featureMap.get(keyOfCurLabel) == null ? 0.1 : featureMap.get(keyOfCurLabel);


                    if (data.getId().equals("1570736451715072")) {
                        System.out.println("present : " + featureName + ", " + labelList.get(i).getName() + ", " + (presentCount / labelList.get(i).getCount()));
                    }
                    labelValue[i][0] *= presentCount / labelList.get(i).getCount();

                    // 非当前标签下，此特征出现的概率
                    String keyOfTotal = courseId + SEPERATOR + featureName + SEPERATOR + "_total";

                    double absentCount = 1.0;
                    try {
                        absentCount = featureMap.get(keyOfTotal) - presentCount;
                    } catch (Exception e) {
                        System.out.println();
                    }



                    double absentTotal = baseInfo.getDataSize() - labelList.get(i).getCount();
                    if (data.getId().equals("1570736451715072")) {
                        System.out.println("absent : " + featureName + ", " + labelList.get(i).getName() + ", " + (absentCount == 0 ? 0.1 / absentTotal : absentCount * 1.0 / absentTotal));
                        System.out.println("===================================");
                    }
                    labelValue[i][1] *= absentCount == 0 ? 0.1 / absentTotal : absentCount * 1.0 / absentTotal;

                }
                // 平衡label中的值，使得里面的值不会太小
                balanceValue(labelValue);
            }
            Result result = new Result(data.getId(), data.getLabels(), labelValue, labelNames);
            results.add(result);

            for (int i = 0; i < labelValue.length; i++) {
                double[] values = labelValue[i];
                if (data.getId().equals("1570736451715072")) {
                    System.out.println(labels.get(i) + ", " + data.getId() + ", values : " + Arrays.toString(values) + ", label : " + data.getLabels());
                }
            }
//            System.out.println("==========================");
        }
        try {
            FileWriter fileWriter = new FileWriter("/home/wangwei/Dev/data/result1.txt", false);

            for (Result result : results) {
                fileWriter.append(result.getId() + ", " + result.getLabels());
                fileWriter.append("\n");

                for (int i = 0; i < result.getScores().length; i++) {
                    fileWriter.append(labels.get(i) + " values : " + Arrays.toString(result.getScores()[i]));
                    fileWriter.append("\n");
//                    System.out.println("values : " + Arrays.toString(value));
                }
                fileWriter.write("question id : " + result.getId());
                fileWriter.write("\n");
                fileWriter.write("true labels are " + result.getLabels());
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
                    fileWriter.write(", so predict failed!");
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

    public MultiLabelPrediction predict() {
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

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }


    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
