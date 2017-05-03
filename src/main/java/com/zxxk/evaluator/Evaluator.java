package com.zxxk.evaluator;

import com.zxxk.data.Data;

import java.util.List;

/**
 * Created by wangwei on 17-5-2.
 */
public interface Evaluator {

    MultiLabelPrediction predictMultiLabel(Data data);

    EvaluationResult evaluateMultiLabel(List<Data> testingData);
}
