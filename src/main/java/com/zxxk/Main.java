package com.zxxk;

import com.alibaba.fastjson.JSONArray;
import com.zxxk.dao.KPointDao;
import com.zxxk.dao.QuestionDao;
import com.zxxk.data.DataSource;
import com.zxxk.domain.Data;
import com.zxxk.learner.EvaluationResult;
import com.zxxk.learner.Labels;
import com.zxxk.learner.Learner;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    public void start() {
        List<Data> trainingDatas = kPointDao.getKpointIdsWithQidAndStem(27, 0, 220000);
        for (Data data : trainingDatas) data.buildLabelsAndFeatures();

        List<Data> testingDatas = kPointDao.getKpointIdsWithQidAndStem(27, 220000, 30000);
        for (Data data : testingDatas) data.buildLabelsAndFeatures();

        learner.setTrainingData(trainingDatas);
        learner.setTestingData(testingDatas);

        List<String> labelNames = new ArrayList<>();
        labelNames.add("10466");
        labelNames.add("11053");
        labelNames.add("11121");
        labelNames.add("10454");
        labelNames.add("11680");
        labelNames.add("11129");
        Labels labels = new Labels(labelNames);
        learner.setLabels(labels);
        System.out.println(learner.multiLabelEvaluate());
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");

        Main main = (Main) applicationContext.getBean("main");
        main.start();
    }
}
