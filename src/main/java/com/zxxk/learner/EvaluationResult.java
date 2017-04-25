package com.zxxk.learner;

/**
 * 对测试集验证的结果
 * Created by shiti on 17-4-20.
 */
public class EvaluationResult {

    private int success;

    private int failed;

    private int undone;

    public void successPlus() {
        this.success++;
    }

    public void failedPlus() {
        this.failed++;
    }

    public void undonePlus() {
        this.undone++;
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
                "success=" + success +
                ", failed=" + failed +
                ", undone=" + undone +
                '}';
    }
}
