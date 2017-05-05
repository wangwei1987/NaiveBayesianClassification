package com.zxxk.trainer;

import com.zxxk.data.Data;

import java.util.List;

/**
 * Created by wangwei on 17-5-2.
 */
public interface Trainer {

    // train data
    void train(List<Data> trainingData);

}
