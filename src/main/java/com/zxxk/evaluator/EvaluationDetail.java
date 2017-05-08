package com.zxxk.evaluator;

import com.zxxk.data.Data;
import com.zxxk.learner.Result2;

import java.util.ArrayList;
import java.util.List;

/**
 * 对测试集验证的结果
 * Created by wangwei.
 */
public class EvaluationDetail {

    private List<Data> success;

    private List<Data> failed;

    private List<Data> undone;

    private List<Result2> results = new ArrayList<>();

    private double accuracy;

    public List<Data> getSuccess() {
        return success;
    }

    public void setSuccess(List<Data> success) {
        this.success = success;
    }

    public List<Data> getFailed() {
        return failed;
    }

    public void setFailed(List<Data> failed) {
        this.failed = failed;
    }

    public List<Data> getUndone() {
        return undone;
    }

    public void setUndone(List<Data> undone) {
        this.undone = undone;
    }

    public double getAccuracy() {
        if (accuracy == 0.0 && success.size() != 0)
            return success.size() * 1.0 / (success.size() + undone.size() + failed.size());
        else
            return this.accuracy;
    }

    public static void main(String[] args) {
        System.out.println(new EvaluationDetail().getAccuracy());
    }
}
