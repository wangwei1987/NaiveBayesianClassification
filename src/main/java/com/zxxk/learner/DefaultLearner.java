package com.zxxk.learner;

import com.zxxk.data.Data;
import com.zxxk.evaluator.NaiveBayesianEvaluator;
import com.zxxk.trainer.NaiveBayesianTrainer;

import java.util.List;

/**
 * 默认的learner，应用继承此类，即可完成基本的学习预测需求
 * Created by wangwei.
 */
public class DefaultLearner {

    private NaiveBayesianTrainer trainer;

    private NaiveBayesianEvaluator evaluator;

    public void train(List<Data> trainingData) {
        this.trainer.train(trainingData);
    }

    public void evaluate(List<Data> testingData) {
        this.evaluator.evaluateMultiLabel(testingData);
    }

    public void predict(Data data) {
        this.evaluator.predictMultiLabel(data);
    }
}
