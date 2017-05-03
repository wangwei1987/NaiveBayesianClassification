package com.zxxk.evaluator;

/**
 * Created by wangwei on 17-5-2.
 */
public class KPointEvaluator extends NaiveBayesianEvaluator {

    private int courseId;

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
