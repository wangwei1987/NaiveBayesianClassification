package com.zxxk;

import com.alibaba.fastjson.JSONArray;
import com.zxxk.dao.KPointDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.dao.QuestionDao;
import com.zxxk.data.Data;
import com.zxxk.data.DataSource;
import com.zxxk.domain.Label;
import com.zxxk.evaluator.EvaluationResult;
import com.zxxk.evaluator.MultiLabelPrediction;
import com.zxxk.learner.DBLearner;
import com.zxxk.learner.Labels;
import com.zxxk.learner.Learner;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
@Component
public class Main {

    private Main main;
    private DBLearner dbLearner;
    private KPointDao kPointDao;
    private LabelDao labelDao;
    private Learner learner;

    @Before
    public void init() {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");

        main = applicationContext.getBean(Main.class);
        learner = applicationContext.getBean(Learner.class);
        dbLearner = applicationContext.getBean(DBLearner.class);
        kPointDao = applicationContext.getBean(KPointDao.class);
        labelDao = applicationContext.getBean(LabelDao.class);
    }

    @Test
    public void test1() {
        DataSource dataSource = new DataSource();
        List<Data> trainingData = dataSource.extractOraginalData("1", "0, 20000", true);
        trainingData.addAll(dataSource.extractOraginalData("2", "0, 10000", true));
        trainingData.addAll(dataSource.extractOraginalData("3", "0, 15000", true));

        List<Data> testingData = dataSource.extractOraginalData("1", "20000, 5000", false);
        testingData.addAll(dataSource.extractOraginalData("2", "20000, 500", false));
        testingData.addAll(dataSource.extractOraginalData("3", "20000, 4000", false));

//        List<String> labels = new ArrayList<>();
//        labels.add("1");
//        labels.add("2");
//        labels.add("3");
        String[] labelNames = {"1", "2", "3"};
        Integer[] labelDistribution = {20000, 10000, 15000};
        Labels labels = new Labels(labelNames, labelDistribution);
        Learner learner = new Learner(trainingData, testingData, labels);

        EvaluationResult result = learner.evaluate();
        System.out.println(result);
    }

    @Test
    public void testKpointId() {
        DataSource dataSource = new DataSource();
        System.out.println("fetching data");
        long start = System.currentTimeMillis();
        List<Data> trainingData = dataSource.extractOraginalDataOfQbm("10466", null, true, "0, 9000");
        ;
        trainingData.addAll(dataSource.extractOraginalDataOfQbm("11053", null, true, "0, 8000"));
        trainingData.addAll(dataSource.extractOraginalDataOfQbm("10466", "11053", false, "0, 9000"));

        List<Data> testingData = dataSource.extractOraginalDataOfQbm("10466", null, true, "9000, 1000");
        testingData.addAll(dataSource.extractOraginalDataOfQbm("11053", null, true, "8000, 1000"));
        testingData.addAll(dataSource.extractOraginalDataOfQbm("10466", "11053", false, "9000, 1000"));

        long end = System.currentTimeMillis();
        System.out.println("finish fetching data, use time : " + (end - start) / 1000);
//        String[] labelNames = {"10466"};
//        Integer[] labelDistribution = {8000, 8000};
        List<String> labelNames = new ArrayList<>();
        labelNames.add("10466");
        labelNames.add("11053");
        Labels labels = new Labels(labelNames);
        Learner learner = new Learner(trainingData, testingData, labels);

        EvaluationResult result = learner.multiLabelEvaluate();
        System.out.println(result);
    }

    @Test
    public void testKpointId1() {
        DataSource dataSource = new DataSource();
        System.out.println("fetching data");
        List<Data> trainingData = dataSource.extractOraginalDataOfQbm(null, null, true, "0, 220000");

        List<Data> testingData = dataSource.extractOraginalDataOfQbm(null, null, true, "220000, 30000");

        System.out.println("finish fetching data");
        long start = System.currentTimeMillis();
//        String[] labelNames = {"10466"};
//        Integer[] labelDistribution = {8000, 8000};
        List<String> labelNames = new ArrayList<>();
        labelNames.add("10466");
        labelNames.add("11053");
        labelNames.add("11121");
        labelNames.add("10454");
        labelNames.add("11680");
        labelNames.add("11129");
        Labels labels = new Labels(labelNames);
        Learner learner = new Learner(trainingData, testingData, labels);

        EvaluationResult result = learner.multiLabelEvaluate();
        System.out.println(result);
        long end = System.currentTimeMillis();
        System.out.println("Time spent " + (end - start) / 1000 + "s");
    }

    @Test
    public void test() {
        String a = "[[1,2,3],[4,5,6],[7,8,9]]";
        JSONArray array = JSONArray.parseArray(a);
        System.out.println(array.get(0));
    }

    @Test
    public void testSpring() {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");
        QuestionDao questionDao = (QuestionDao) applicationContext.getBean("questionDao");
        System.out.println(questionDao.getQuestionCount());
    }


//    public void start(int courseId, int trainingSize, int testingSize, List<String> labelNames) {
//        List<Data> trainingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, 0, trainingSize);
//        for (Data data : trainingDatas) data.buildLabelsAndFeatures();
//
//        List<Data> testingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, trainingSize, testingSize);
//        for (Data data : testingDatas) data.buildLabelsAndFeatures();
//
//        learner.setTrainingData(trainingDatas);
//        learner.setTestingData(testingDatas);
//
////        List<String> labelNames = new ArrayList<>();
////        labelNames.add("15976");
////        labelNames.add("16361");
////        labelNames.add("16016");
////        labelNames.add("16068");
////        labelNames.add("15954");
////        labelNames.add("16320");
//        Labels labels = new Labels(labelNames);
//        learner.setLabels(labels);
//        System.out.println(learner.multiLabelEvaluate());
//    }

    public void start1(int courseId, int trainingSize, int testingSize, List<String> labelNames) {
        List<Data> trainingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, 0, trainingSize, labelNames);
        for (Data data : trainingDatas) data.buildLabelsAndFeatures();

        List<Data> testingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, trainingSize, testingSize, labelNames);
        for (Data data : testingDatas) data.buildLabelsAndFeatures();

        dbLearner.setTrainingData(trainingDatas);
        dbLearner.setTestingData(testingDatas);

        Labels labels = new Labels(labelNames);
        dbLearner.setLabels(labelNames);
        dbLearner.setCourseId(courseId);
        dbLearner.train(false);
        System.out.println(dbLearner.multiLabelEvaluate(false));
    }

    public void batchTrain(int courseId, List<String> labelNames, int batchSize) {
        int i = 0;
        int trainDataSize = 0;
        do {
            System.out.println("batch index : " + i);
            List<Data> trainingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, batchSize * i++, batchSize, labelNames);
            // 如果没有更多数据了，则退出循环
            if (CollectionUtils.isEmpty(trainingDatas)) {
                break;
            } else {
                trainDataSize += trainingDatas.size();
            }

            for (Data data : trainingDatas) data.buildLabelsAndFeatures();

            dbLearner.setTrainingData(trainingDatas);

            dbLearner.setLabels(labelNames);
            dbLearner.setCourseId(courseId);
            dbLearner.train(true);
            System.out.println("finish training!");
//            System.out.println(dbLearner.multiLabelEvaluate());
        } while (true);

        long start = System.currentTimeMillis();
        dbLearner.save(trainDataSize);
        long end = System.currentTimeMillis();
        System.out.println("training and saving finished, total size is " + trainDataSize + ", use time : " + (end - start) / 1000);
    }

    @Test
    public void testMultiTrain() {
        int courseId = 27;
//        List<String> labels = kPointDao.getAllKpointIds(courseId);
        List<String> labels = kPointDao.getValidLabels(courseId);
        this.batchTrain(courseId, labels, 10000);
    }

    @Test
    public void testEvaluate() {
        int courseId = 27;
//        List<String> allLabels = labelDao.getAll(courseId).stream().map(label -> label.getName()).collect(Collectors.toList());
        List<String> allLabels = kPointDao.getValidLabels(courseId);
        List<Data> datas = kPointDao.getKpointIdsWithQidAndStem(courseId, 10000, 1000, allLabels);
        datas.stream().forEach(data -> data.buildLabelsAndFeatures());

        dbLearner.setCourseId(courseId);
        dbLearner.setLabels(allLabels);
        dbLearner.setTestingData(datas);

        long start = System.currentTimeMillis();
        System.out.println(dbLearner.multiLabelEvaluate(false));
        long end = System.currentTimeMillis();
        System.out.println("预测用时 : " + (end - start) / 1000);
    }

    @Test
    public void testPredict() {
        String stem = "<stem><p>(本小题满分12分)设两抛物线<math guid=\"7d3e7356e5cc4d598522db6c95b8150e\" latex=\"$y=-{{x}^{2}}+2x,y={{x}^{2}}$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/4/17/1569698613747712/1569698618548224/STEM/7d3e7356e5cc4d598522db6c95b8150e.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>y</mi><mo>=</mo><mo>&#x2212;</mo><msup>   <mi>x</mi>   <mn>2</mn>  </msup>  <mo>+</mo><mn>2</mn><mi>x</mi><mo>,</mo><mi>y</mi><mo>=</mo><msup>   <mi>x</mi>   <mn>2</mn>  </msup>  </mrow></math>所围成的图形为<math guid=\"093c98d0e5d041e988254233d9b3e9ed\" latex=\"$M$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/4/17/1569698613747712/1569698618548224/STEM/093c98d0e5d041e988254233d9b3e9ed.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mi>M</mi></math>，求<math guid=\"093c98d0e5d041e988254233d9b3e9ed\" latex=\"$M$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/4/17/1569698613747712/1569698618548224/STEM/093c98d0e5d041e988254233d9b3e9ed.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mi>M</mi></math>的面积.</p></stem>";
        Data data = new Data();
        data.setStem(stem);
        data.buildLabelsAndFeatures();
        MultiLabelPrediction prediction = this.dbLearner.predictMultiLabel(27, data);

        if (prediction.getPredictedLabels().get(0).equals(Label.LABEL_OTHER)) {
            System.out.println(Label.LABEL_OTHER);
        } else {
            List<String> names = kPointDao.getKPointNames(prediction.getPredictedLabels());
            System.out.println(names);
        }

    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");

        Main main = (Main) applicationContext.getBean("main");

        QuestionDao questionDao = (QuestionDao) applicationContext.getBean("questionDao");

        KPointDao kPointDao = applicationContext.getBean(KPointDao.class);

        // 高中数学         success=29681, failed=283, undone=36, accuracy=98.93666666666667%
//        main.start(27, 220000, 30000,
//                new ArrayList<String>(Arrays.asList("10454","10467","11384","11056","11097","11391")));

        // 高中化学         success=10521, failed=479, undone=0, accuracy=95.64545454545454%
//        main.start(29, 120000, 11000,
//                new ArrayList<String>(Arrays.asList("15976","16361","16016","16068","15954","16320")));

        // 高中语文
//        EvaluationResult{success=18801, failed=4418, undone=0, accuracy=80.97247943494551%}
//        courseId : 26, training size : 124943, testing size : 13882
//        labels : [46192, 47666, 46174, 46177, 44424, 47700, _other]

        // 高中英语
//        EvaluationResult{success=11992, failed=1891, undone=0, accuracy=86.37902470647555%}
//        courseId : 28, training size : 124943, testing size : 13882
//        labels : [42226, 42232, 42246, 42235, 42244, 42236, _other]

//        EvaluationResult{success=90947, failed=506, undone=0, accuracy=99.44671033208314%}
//        courseId : 30, training size : 124943, testing size : 13882
//        labels : [7293, 7226, 7171, 7156, 7153, 7170, _other]

        for (int courseId = 27; courseId <= 27; courseId++) {
//            int courseId = 31;
//            int total = questionDao.countZujuanQuestion(courseId);
//            int trainingSize = total * 9 / 200;
//            int testingSize = total / 200;

//            List<String> labels = random6Items(kPointDao.getTop20KpointIds(26));

            int trainingSize = 25000;
            int testingSize = 3000;

            List<String> labels = new ArrayList<String>(Arrays.asList("10454", "10467", "11384", "11056", "11097", "11391"));
            System.out.println("courseId : " + courseId + ", training size : " + trainingSize + ", testing size : " + testingSize);
            System.out.println("labels : " + labels);
            main.start1(courseId, trainingSize, testingSize, labels);
            System.out.println("courseId : " + courseId + ", training size : " + trainingSize + ", testing size : " + testingSize);
            System.out.println("labels : " + labels);
            System.out.println("========================================================");
        }
    }

    private static List<String> random6Items(List<String> labels) {
        List<String> selectedLabels = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            selectedLabels.add(labels.get(i * 3));
        }
        return selectedLabels;
    }

//    @Test
//    public void testRestoreLearnerToDB() {
//        List<Data> trainingData = kPointDao.getKpointIdsWithQidAndStem(27, 0, 5000);
//        trainingData.stream().forEach(data -> {
//            data.buildLabelsAndFeatures();
//        });
//        List<String> labels = new ArrayList<String>(Arrays.asList("15976", "16361", "16016", "16068", "15954", "16320"));
//        dbLearner.setCourseId(27);
//        dbLearner.setTrainingData(trainingData);
//        dbLearner.setLabels(labels);
//        dbLearner.train(false);
//    }

//    public static void main1(String[] args) {
//        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");
//        Main main = applicationContext.getBean(Main.class);
//        DBLearner learner = applicationContext.getBean(DBLearner.class);
//        main.testRestoreLearnerToDB();
//    }

    @Test
    public void testGetValidLabels() {
        List<String> list = kPointDao.getValidLabels(27);
        System.out.printf("{");
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("\"" + list.get(i) + "\", ");
            if (i != 0 && i % 10 == 0) {
                System.out.println();
            }
        }
        System.out.println("}");

    }

}
