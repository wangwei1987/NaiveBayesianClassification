package com.zxxk.learner;

import com.zxxk.dao.BaseInfoDao;
import com.zxxk.dao.FeatureDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.domain.BaseInfo;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import com.zxxk.trainer.NaiveBayesianTrainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 提供保存学习成果到数据库的方法
 * Created by wangwei on 17-5-2.
 */
@Component
public class LearnerSaver {

    @Resource
    private FeatureDao featureDao;
    @Resource
    private LabelDao labelDao;
    @Resource
    private BaseInfoDao baseInfoDao;

    @Transactional
    public void saveFeatures(int courseId, Map<String, Integer> featuresToStore) {
        int i = 0;
        List<Feature> insertFeatures = new ArrayList<>();

        Iterator iterator = featuresToStore.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
            i++;
            String[] props = entry.getKey().split(NaiveBayesianTrainer.SEPERATOR);
            Feature feature = new Feature(courseId, props[0], props[1], entry.getValue());

            insertFeatures.add(feature);
        }
        long startSave = System.currentTimeMillis();
        featureDao.insertAll(insertFeatures);
        long endSave = System.currentTimeMillis();
        System.out.println("insert All time : " + (endSave - startSave));
    }

    @Transactional
    public void saveFeatures(List<Feature> features) {
        featureDao.insertAll(features);
    }

    @Transactional
    public void saveLabels(int courseId, List<String> allLabels, Map<String, Integer> labelToStore) {
        for (String labelName : allLabels) {
            Label label = null;
            Integer labelCount = labelToStore.get(labelName);
            if (labelCount != null) {
                label = new Label(courseId, labelName, labelCount);
            } else {
                label = new Label(courseId, labelName, 0);
            }

            Label labelInDB = labelDao.get(courseId, labelName);
            if (labelInDB == null) {
                labelDao.insert(label);
            } else {
                labelInDB.setCount(labelInDB.getCount() + label.getCount());
                labelDao.update(label);
            }
        }
    }

    @Transactional
    public void saveBaseInfo(int courseId, int trainingDataSize) {
        BaseInfo baseInfo = baseInfoDao.get(courseId);
        if (baseInfo == null) {
            baseInfoDao.insert(new BaseInfo(courseId, trainingDataSize));
        } else {
            baseInfo.setDataSize(baseInfo.getDataSize() + trainingDataSize);
            baseInfoDao.update(baseInfo);
        }
    }

    @Transactional
    public void saveAll(int courseId, int trainingDataSize, Map<String, Integer> featuresToStore, List<String> allLabels, Map<String, Integer> labelsTostore) {
        saveBaseInfo(courseId, trainingDataSize);
        saveFeatures(courseId, featuresToStore);
        saveLabels(courseId, allLabels, labelsTostore);
    }

}
