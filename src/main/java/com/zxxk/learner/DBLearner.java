package com.zxxk.learner;

import com.zxxk.dao.BaseInfoDao;
import com.zxxk.dao.FeatureDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.domain.BaseInfo;
import com.zxxk.domain.Data;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import com.zxxk.exception.ClassificationException;
import com.zxxk.util.MaxValuedLabel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//    private void init() {
//        features = new Features(labels.size());
//    }

//    public DBLearner(List<Data> trainingData, List<Data> testingData, Labels labels) {
//        if (CollectionUtils.isEmpty(trainingData) || CollectionUtils.isEmpty(testingData)) {
//            throw new ClassificationException("training data, testing data shouldn't be empty!");
//        }
//        this.trainingData = trainingData;
//        this.testingData = testingData;
//        this.labels = labels;
//        init();
//    }
//
//    public DBLearner(List<Data> trainingData, Labels labels) {
//        if (CollectionUtils.isEmpty(trainingData)) {
//            throw new ClassificationException("training data shouldn't be empty!");
//        }
//        this.trainingData = trainingData;
//        this.labels = labels;
//        init();
//    }

    public DBLearner() {

    }

    @Transactional
    public void train() {
        for (Data data : trainingData) {

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
                // clear invalid labels
                data.setLabels(filterValidLabels(labelsOfData));
                labelsOfData = data.getLabels();
            }

            for (String feature : featuresInData) {
                addFeatureCount(courseId + SEPERATOR + feature + SEPERATOR + "_total");
                for (String label : labelsOfData) {
                    addFeatureCount(courseId + SEPERATOR + feature + SEPERATOR + label);
                }
            }
            if (featureToRestore.size() > 5000) {
                restoreFeatures(featureToRestore);
                featureToRestore.clear();
            }
        }
        restoreLabels(labelToRestore);

        long start = System.currentTimeMillis();
        restoreFeatures(featureToRestore);
        long end = System.currentTimeMillis();
        System.out.println("保存用时 ： " + (end - start) / 1000);

        baseInfoDao.insert(new BaseInfo(courseId, trainingData.size()));
        featureToRestore.clear();
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
        System.out.println();
    }

    private void addFeatureCount(String key) {
        if (featureToRestore.containsKey(key)) {
            featureToRestore.put(key, featureToRestore.get(key) + 1);
        } else {
            featureToRestore.put(key, 1);
        }
    }

    @Transactional()
    private void restoreFeatures(Map<String, Integer> featuresToStore) {
        for (Map.Entry<String, Integer> entry : featuresToStore.entrySet()) {
            String[] props = entry.getKey().split(SEPERATOR);
            Feature feature = new Feature(Integer.valueOf(props[0]), props[1], props[2], entry.getValue());

            Feature featureInDB = featureDao.get(Integer.valueOf(props[0]), props[1], props[2]);
            if (featureInDB == null) {
                featureDao.insert(feature);
            } else {
                featureDao.plusCount(feature);
            }
        }
    }

    @Transactional
    private void restoreLabels(Map<String, Integer> labelToStore) {
        for (String labelName : labels) {
            Label label = null;
            Integer labelCount = labelToStore.get(labelName);
            if (labelCount != null) {
                label = new Label(courseId, labelName, labelCount);
            } else {
                label = new Label(courseId, labelName, 0);
            }
            labelDao.insert(label);
        }


//        for (Map.Entry<String, Integer> entry : labelToStore.entrySet()) {
//            Label label = new Label(entry.getKey(), entry.getValue());
//            labelDao.insert(label);
//        }
//        // 某些标签在训练数据中没有出现过，这些标签也要存入数据库
//        List<String> copiedLabels = new ArrayList<>(Arrays.asList(new String[labels.size()]));
//        Collections.copy(copiedLabels, labels);
//        copiedLabels.removeAll(labelToStore.entrySet());
//        if(copiedLabels.size() > 0) {
//            copiedLabels.stream().forEach(labelName -> {
//                Label label = new Label(labelName, 0);
//                labelDao.insert(label);
//            });
//        }

//        for (int i = 0; i < labelToStore.getNames().size(); i++) {
//            Label label = new Label(labelToStore.get(i), labelToStore.getCount(i));
//            labelDao.insert(label);
//        }
    }

    @Resource
    private BaseInfoDao baseInfoDao;

    public EvaluationResult multiLabelEvaluate() {


        List<Result> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(testingData)) {
            throw new ClassificationException("testing data should not be empty!");
        }
        System.out.println("training start !!");
        long start = System.currentTimeMillis();
        // 先进行训练
//        this.train();
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
            double[][] labelValue = new double[labels.size() - 1][2];
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

                for (int i = 1; i < labelList.size(); i++) {

//                    int presentCount = features.getCounts(i).get(featureIndex);
                    // 当前标签下，此特征出现的概率
                    String keyOfCurLabel = courseId + SEPERATOR + featureName + SEPERATOR + labelList.get(i).getName();
                    double presentCount = featureMap.get(keyOfCurLabel) == null ? 0.1 : featureMap.get(keyOfCurLabel);
                    labelValue[i - 1][0] *= presentCount / labelList.get(i).getCount();

                    // 非当前标签下，此特征出现的概率
                    String keyOfTotal = courseId + SEPERATOR + featureName + SEPERATOR + "_total";
                    double absentCount = featureMap.get(keyOfTotal) - presentCount;
                    double absentTotal = baseInfo.getDataSize() - labelList.get(i).getCount();
                    labelValue[i - 1][1] *= absentCount == 0 ? 0.1 / absentTotal : absentCount * 1.0 / absentTotal;

                }
                // 平衡label中的值，使得里面的值不会太小
                balanceValue(labelValue);
            }
            Result result = new Result(data.getId(), data.getLabels(), labelValue, labelNames);
            results.add(result);

        }
        try {
            FileWriter fileWriter = new FileWriter("/home/wangwei/Dev/data/result.txt", false);

            for (Result result : results) {
                fileWriter.append(result.getId() + ", " + result.getLabels());
                fileWriter.append("\n");

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

    //    public Labels getLabels() {
//        return labels;
//    }

//    public void setLabels(Labels labels) {
//        this.labels = labels;
//        init();
//    }
//
//    public List<String> getLabelList() {
//        return labelList;
//    }
//
//    public void setLabelList(List<String> labelList) {
//        this.labelList = labelList;
//    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
