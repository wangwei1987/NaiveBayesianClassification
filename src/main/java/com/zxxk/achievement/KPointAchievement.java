package com.zxxk.achievement;

import com.zxxk.dao.BaseInfoDao;
import com.zxxk.dao.FeatureDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.domain.BaseInfo;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import com.zxxk.exception.ClassificationException;
import com.zxxk.trainer.NaiveBayesianTrainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei.
 */
@Service
public class KPointAchievement extends NaiveBayesianAchievement {

    private Integer courseId;

    @Resource
    private BaseInfoDao baseInfoDao;
    @Resource
    private FeatureDao featureDao;
    @Resource
    private LabelDao labelDao;

    @Override
    public BaseInfo getBaseInfo() {
        checkCourseId();
        return baseInfoDao.get(courseId);
    }

    @Override
    public List<Label> getAllLabels() {
        checkCourseId();
        return labelDao.getValidLabels(courseId);
    }

    @Override
    public List<Feature> getFeaturesByName(String featureName, List<Label> labels) {
        checkCourseId();
        return featureDao.getByName(courseId, featureName, labels);
    }


    //    @Transactional
    public void saveFeatures(Map<String, Integer> featuresToStore) {
        checkCourseId();

        List<Feature> insertFeatures = new ArrayList<>();
        Iterator iterator = featuresToStore.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
            String[] props = entry.getKey().split(NaiveBayesianTrainer.SEPERATOR);
            Feature feature = new Feature(courseId, props[0], props[1], entry.getValue());

            insertFeatures.add(feature);

            if (insertFeatures.size() > 10000) {
                saveFeatures(insertFeatures);
                insertFeatures.clear();
            }
        }
        System.out.println("saving features partly...");
        this.saveFeatures(insertFeatures);
    }

    @Override
    @Transactional
    public boolean saveFeatures(List<Feature> features) {
        featureDao.insertAll(features);
        return true;
    }

    @Override
    public boolean saveLabels(List<Label> labels) {
        labelDao.insertAll(labels);
        return true;
    }

    @Transactional
    public void saveLabels(List<String> allLabels, Map<String, Integer> labelToStore) {
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

    @Override
    @Transactional
    public boolean saveBaseInfo(BaseInfo baseInfo) {
        BaseInfo baseInfoInDB = baseInfoDao.get(baseInfo.getCourseId());
        if (baseInfoInDB == null) {
            baseInfoDao.insert(new BaseInfo(courseId, baseInfo.getDataSize()));
        } else {
            baseInfo.setDataSize(baseInfo.getDataSize() + baseInfoInDB.getDataSize());
            baseInfoDao.update(baseInfo);
        }
        return true;
    }

    public boolean saveBaseInfo(int trainingDataSize) {
        BaseInfo baseInfo = new BaseInfo(courseId, trainingDataSize);
        return saveBaseInfo(baseInfo);
    }

    @Override
    public boolean eraseHistoryData() {
        return false;
    }

    //    @Transactional
    public void save(int trainingDataSize, Map<String, Integer> featuresToStore, List<String> allLabels, Map<String, Integer> labelsTostore) {
        saveBaseInfo(new BaseInfo(courseId, trainingDataSize));
        saveLabels(allLabels, labelsTostore);
        saveFeatures(featuresToStore);

    }

    /**
     * 检查course id是否为空
     */
    private void checkCourseId() {
        if (courseId == null) {
            throw new ClassificationException("course id can't be empty!");
        }
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public List<Label> getLabelsByNames(List<String> evaluatingLabelNames) {
        return labelDao.getLabelsByNames(evaluatingLabelNames);
    }
}
