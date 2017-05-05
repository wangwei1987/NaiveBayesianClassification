package com.zxxk.trainer;

import com.zxxk.learner.LearnerSaver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by wangwei on 17-5-2.
 */
@Component
public class KPointTrainer extends NaiveBayesianTrainer {

    private int courseId;

    @Resource
    private LearnerSaver saver;

    public KPointTrainer(int courseId) {
        this.courseId = courseId;
    }

    public KPointTrainer() {

    }

    @Override
    protected void save() {
        saver.saveAll(courseId, getTrainingDataSize(), featuresToRestore, getLabels(), labelsToRestore);
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
