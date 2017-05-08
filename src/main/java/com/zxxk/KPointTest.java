package com.zxxk;

import com.zxxk.dao.KPointDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.dao.QuestionDao;
import com.zxxk.data.Data;
import com.zxxk.evaluator.EvaluationResult;
import com.zxxk.learner.DBLearner;
import com.zxxk.learner.KPointLearner;
import com.zxxk.learner.Learner;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.List;

/**
 * Created by wangwei.
 */
public class KPointTest {

    private DBLearner dbLearner;
    private KPointDao kPointDao;
    private LabelDao labelDao;
    private Learner learner;
    private QuestionDao questionDao;
    private ToolsService toolsService;
    private KPointLearner kPointLearner;

    @Before
    public void init() {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");

        learner = applicationContext.getBean(Learner.class);
        dbLearner = applicationContext.getBean(DBLearner.class);
        kPointDao = applicationContext.getBean(KPointDao.class);
        labelDao = applicationContext.getBean(LabelDao.class);
        questionDao = applicationContext.getBean(QuestionDao.class);
        toolsService = applicationContext.getBean(ToolsService.class);
        kPointLearner = applicationContext.getBean(KPointLearner.class);
    }

    public void batchTrain(int courseId, List<String> labelNames, int batchSize) {
        int i = 0;
        int trainDataSize = 0;
        List<Data> trainingDatas;
        do {
            System.out.println("batch index : " + i);
            trainingDatas = kPointDao.getKpointIdsWithQidAndStem(courseId, batchSize * i++, batchSize, labelNames);
            // 如果没有更多数据了，则退出循环
//            if (CollectionUtils.isEmpty(trainingDatas)) {
//                break;
//            } else {
//                trainDataSize += trainingDatas.size();
//            }

            for (Data data : trainingDatas) data.buildLabelsAndFeatures();
            System.out.println("start train...");
//            if(i > 3) {
//                trainingDatas = new ArrayList<>();
//            }
            kPointLearner.train(courseId, trainingDatas, labelNames);
            System.out.println("finish training!");
//            System.out.println(dbLearner.multiLabelEvaluate());
        } while (!CollectionUtils.isEmpty(trainingDatas));

        long start = System.currentTimeMillis();
//        dbLearner.save(trainDataSize);
        long end = System.currentTimeMillis();
        System.out.println("training and saving finished, total size is " + trainDataSize + ", use time : " + (end - start) / 10000);
    }

    @Test
    public void testMultiTrain() {
        int courseId = 27;
        List<String> labels = kPointDao.getAllKpointIds(courseId);
//        List<Label> labels = kPointDao.getValidLabels(27);
//        List<Label> labels = new ArrayList<>();
//        Label label1 = new Label(27, "11383", 0);
//        Label label2 = new Label(27, "10 454", 0);
//        labels.add(label1);
//        labels.add(label2);
//        List<String> labelNames = labels.stream().map(label -> label.getName()).collect(Collectors.toList());
        this.batchTrain(courseId, labels, 10000);
    }

    @Test
    public void testEvaluate() {
        int courseId = 27;
//        List<String> allLabels = labelDao.getAll(courseId).stream().map(label -> label.getName()).collect(Collectors.toList());
//        List<Label> allLabels = kPointDao.getValidLabels(courseId);
        List<String> allLabelNames = kPointDao.getAllKpointIds(courseId);
//        List<Label> allLabels = new ArrayList<>();
//        Label label1 = new Label(27, "11383", 0);
//        Label label2 = new Label(27, "10454", 0);
//        allLabels.add(label1);
//        allLabels.add(label2);

//        List<String> allLabelNames = allLabels.stream().map(label -> label.getName()).collect(Collectors.toList());
        List<Data> datas = kPointDao.getKpointIdsWithQidAndStem(courseId, 13000, 200, allLabelNames);
        datas.stream().forEach(data -> data.buildLabelsAndFeatures());

        long start = System.currentTimeMillis();
        EvaluationResult result = kPointLearner.evaluate(courseId, datas, allLabelNames);
        long end = System.currentTimeMillis();
        System.out.println("用时 ： " + (end - start) / 1000);
        System.out.println(result);
    }
}
