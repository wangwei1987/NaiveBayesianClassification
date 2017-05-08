package com.zxxk;

import com.zxxk.dao.KPointDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.dao.QuestionDao;
import com.zxxk.learner.DBLearner;
import com.zxxk.learner.Learner;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei on 17-5-4.
 */
public class ToolsMain {

    private Main main;
    private DBLearner dbLearner;
    private KPointDao kPointDao;
    private LabelDao labelDao;
    private Learner learner;
    private QuestionDao questionDao;
    private ToolsService toolsService;

    @Before
    public void init() {
        ApplicationContext applicationContext = new FileSystemXmlApplicationContext("classpath:spring/business-config.xml");

        main = applicationContext.getBean(Main.class);
        learner = applicationContext.getBean(Learner.class);
        dbLearner = applicationContext.getBean(DBLearner.class);
        kPointDao = applicationContext.getBean(KPointDao.class);
        labelDao = applicationContext.getBean(LabelDao.class);
        questionDao = applicationContext.getBean(QuestionDao.class);
        toolsService = applicationContext.getBean(ToolsService.class);
    }

    @Test
    public void testSaveKpointCount() {
        List<Map<String, Object>> list = kPointDao.getKPointsWithCount();
        List<Map<String, Object>> saveList = new ArrayList<>();
        for (Map<String, Object> kpoint : list) {
            Map<String, Object> params = new HashMap<>();
            Long pointId = (Long) kpoint.get("pointid");
            Map<String, Object> kpointById = kPointDao.getKPointById(pointId);

            if (kpointById == null) {
                continue;
            }
            params.put("courseid", kpointById.get("courseid"));
            params.put("pointid", kpoint.get("pointid"));
            params.put("count", kpoint.get("count"));

//            kPointDao.saveKPointCount(params);
            saveList.add(params);

            if (saveList.size() > 10000) {
                System.out.println("1w le , saving ...");
                toolsService.saveKPointCount(saveList);
                saveList.clear();
            }
        }
    }

    @Test
    public void testGetQuestion() {
        Map<String, Object> question = questionDao.getQuestionById("1560908513222656");
        System.out.println(question);
    }
}
