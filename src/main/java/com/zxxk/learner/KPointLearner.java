package com.zxxk.learner;

import com.zxxk.achievement.KPointAchievement;
import com.zxxk.data.Data;
import com.zxxk.domain.Label;
import com.zxxk.evaluator.EvaluationResult;
import com.zxxk.evaluator.KPointEvaluator;
import com.zxxk.trainer.KPointTrainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangwei.
 */
@Component
public class KPointLearner {

    @Resource
    private KPointTrainer trainer;
    @Resource
    private KPointEvaluator evaluator;
    @Resource
    private KPointAchievement achievement;

    public void train(int courseId, List<Data> trainingData) {
        this.trainer.setCourseId(courseId);
        this.trainer.train(trainingData);
    }

    public void train(int courseId, List<Data> trainingData, List<String> labels) {
        this.trainer.setCourseId(courseId);
        labels.add(Label.LABEL_OTHER);
        this.trainer.train(trainingData, labels);
    }

    public EvaluationResult evaluate(int courseId, List<Data> testingData) {
        this.evaluator.setCourseId(courseId);
        return this.evaluator.evaluateMultiLabel(testingData);
    }

    public EvaluationResult evaluate(int courseId, List<Data> testingData, List<String> evaluatingLabelNames) {
        this.evaluator.setCourseId(courseId);
        List<Label> evaluatingLabels = achievement.getLabelsByNames(evaluatingLabelNames);
        return this.evaluator.evaluateMultiLabel(testingData, evaluatingLabels);
    }

    public Result predictMultiLabel(int courseId, Data data) {
        return evaluator.predictMultiLabel(data);
    }
}
