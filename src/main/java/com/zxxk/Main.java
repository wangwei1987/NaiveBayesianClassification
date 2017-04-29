package com.zxxk;

import com.alibaba.fastjson.JSONArray;
import com.zxxk.dao.KPointDao;
import com.zxxk.dao.QuestionDao;
import com.zxxk.data.DataSource;
import com.zxxk.domain.Data;
import com.zxxk.learner.DBLearner;
import com.zxxk.learner.EvaluationResult;
import com.zxxk.learner.Labels;
import com.zxxk.learner.Learner;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
@Component
public class Main {

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

    @Resource
    private KPointDao kPointDao;
    @Resource
    private Learner learner;

    public void start(int courseId, int trainingSize, int testingSize, List<String> labelNames) {
        List<Data> trainingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, 0, trainingSize);
        for (Data data : trainingDatas) data.buildLabelsAndFeatures();

        List<Data> testingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, trainingSize, testingSize);
        for (Data data : testingDatas) data.buildLabelsAndFeatures();

        learner.setTrainingData(trainingDatas);
        learner.setTestingData(testingDatas);

//        List<String> labelNames = new ArrayList<>();
//        labelNames.add("15976");
//        labelNames.add("16361");
//        labelNames.add("16016");
//        labelNames.add("16068");
//        labelNames.add("15954");
//        labelNames.add("16320");
        Labels labels = new Labels(labelNames);
        learner.setLabels(labels);
        System.out.println(learner.multiLabelEvaluate());
    }

    public void start1(int courseId, int trainingSize, int testingSize, List<String> labelNames) {
        List<Data> trainingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, 0, trainingSize);
        for (Data data : trainingDatas) data.buildLabelsAndFeatures();

        List<Data> testingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, trainingSize, testingSize);
        for (Data data : testingDatas) data.buildLabelsAndFeatures();

        dbLearner.setTrainingData(trainingDatas);
        dbLearner.setTestingData(testingDatas);

        Labels labels = new Labels(labelNames);
        dbLearner.setLabels(labelNames);
        dbLearner.setCourseId(courseId);
        dbLearner.train();
        System.out.println(dbLearner.multiLabelEvaluate());
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

            int trainingSize = 100;
            int testingSize = 10;

            List<String> labels = new ArrayList<String>(Arrays.asList("10454", "10467", "11384", "11056", "11097", "11391"));
            System.out.println("courseId : " + courseId + ", training size : " + trainingSize + ", testing size : " + testingSize);
            System.out.println("labels : " + labels);
            main.start1(courseId, trainingSize, trainingSize, labels);
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

    @Resource
    private DBLearner dbLearner;

    @Test
    public void testRestoreLearnerToDB() {
        List<Data> trainingData = kPointDao.getKpointIdsWithQidAndStem(27, 0, 5000);
        trainingData.stream().forEach(data -> {
            data.buildLabelsAndFeatures();
        });
        List<String> labels = new ArrayList<String>(Arrays.asList("15976", "16361", "16016", "16068", "15954", "16320"));
        dbLearner.setCourseId(27);
        dbLearner.setTrainingData(trainingData);
        dbLearner.setLabels(labels);
        dbLearner.train();
    }

    public static void main1(String[] args) {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");
        Main main = applicationContext.getBean(Main.class);
        DBLearner learner = applicationContext.getBean(DBLearner.class);
        main.testRestoreLearnerToDB();
    }
}
