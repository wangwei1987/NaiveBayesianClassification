package com.zxxk;

import com.alibaba.fastjson.JSONArray;
import com.zxxk.data.Data;
import com.zxxk.data.DataSource;
import com.zxxk.data.Labels;
import com.zxxk.learner.EvaluationResult;
import com.zxxk.learner.Learner;
import org.junit.Test;

import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
public class Main {

    public static void main(String[] args) {
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
        List<Data> trainingData = dataSource.extractOraginalDataOfQbm("10466", true, "0, 2000");
        ;
        trainingData.addAll(dataSource.extractOraginalDataOfQbm("10466", false, "0, 2000"));

        List<Data> testingData = dataSource.extractOraginalDataOfQbm("10466", true, "2000, 100");
        testingData.addAll(dataSource.extractOraginalDataOfQbm("10466", false, "2000, 100"));

        System.out.println("finish fetching data");
        String[] labelNames = {"10466", "-10466"};
//        Integer[] labelDistribution = {8000, 8000};
        Labels labels = new Labels(labelNames);
        Learner learner = new Learner(trainingData, testingData, labels);

        EvaluationResult result = learner.evaluate();
        System.out.println(result);
    }

    @Test
    public void test() {
        String a = "[[1,2,3],[4,5,6],[7,8,9]]";
        JSONArray array = JSONArray.parseArray(a);
        System.out.println(array.get(0));
    }
}
