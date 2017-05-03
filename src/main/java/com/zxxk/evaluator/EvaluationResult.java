package com.zxxk.evaluator;

/**
 * 对测试集验证的结果
 * Created by shiti on 17-4-20.
 */
public class EvaluationResult {

    private int success;

    private int failed;

    private int undone;

    private String accuracy;

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
        accuracy = (success * 100.0 / (success + failed + undone)) + "%";
        return "EvaluationResult{" +
                "success=" + success +
                ", failed=" + failed +
                ", undone=" + undone +
                ", accuracy=" + accuracy +
                '}';
    }
}
