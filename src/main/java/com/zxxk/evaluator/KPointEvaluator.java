package com.zxxk.evaluator;

import com.zxxk.achievement.KPointAchievement;
import com.zxxk.domain.BaseInfo;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import com.zxxk.exception.ClassificationException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangwei.
 */
@Component
public class KPointEvaluator extends NaiveBayesianEvaluator {

    @Resource
    private KPointEvaluator kPointEvaluator;

    private List<Label> evaluatingLabels;
    @Resource
    private KPointAchievement achievement;

    @Override
    public long getTrainingSize() {
//        BaseInfo baseInfo = new BaseInfo();
        BaseInfo baseInfo = achievement.getBaseInfo();
        if (baseInfo == null || baseInfo.getDataSize() <= 0) {
            throw new ClassificationException("data size should be a positive number!");
        }
        return baseInfo.getDataSize();
    }

    @Override
    public List<Feature> getFeaturesByName(String featureName, List<Label> labels) {
        List<Feature> features = achievement.getFeaturesByName(featureName, labels);
        return features;
    }

    @Override
    public List<Label> getAllLabels() {
        List<Label> allLabels = achievement.getAllLabels();
        if (CollectionUtils.isEmpty(allLabels)) {
            throw new ClassificationException("there is must be at least one label in achievement!");
        }
        return allLabels;
    }

    //    public int getCourseId() {
//        return courseId;
//    }

    public void setCourseId(int courseId) {
//        this.courseId = courseId;
        achievement.setCourseId(courseId);
    }
}
