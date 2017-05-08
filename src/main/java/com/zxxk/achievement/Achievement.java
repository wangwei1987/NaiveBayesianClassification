package com.zxxk.achievement;

import com.zxxk.domain.BaseInfo;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;

import java.util.List;

/**
 * 1. 学习的结果通过此接口提供的方法保存
 * 2. 预测数据时，获取训练的结果数据
 * Created by wangwei on 17-5-3.
 */
public interface Achievement {
    /**
     * 获取训练成果的基本情况,如
     *
     * @return
     */
    BaseInfo getBaseInfo();

    /**
     * 获取所有标签
     *
     * @return
     */
    List<Label> getAllLabels();

    /**
     * 获取所有name为featureName的Feature
     * @param featureName
     * @return
     */
//    List<Feature> getFeaturesByName(String featureName);

    /**
     * 获取所有name为featureName,且label属于labels的Feature
     *
     * @param featureName
     * @return
     */
    List<Feature> getFeaturesByName(String featureName, List<Label> labels);

    /**
     * 保存训练出的feature
     *
     * @param features
     */
    boolean saveFeatures(List<Feature> features);

    /**
     * 保存训练出的label
     *
     * @param labels
     */
    boolean saveLabels(List<Label> labels);

    /**
     * 保存训练出的baseInfo
     *
     * @param baseInfo
     */
    boolean saveBaseInfo(BaseInfo baseInfo);

    /**
     * todo 删除历史训练数据
     *
     * @return
     */
    boolean eraseHistoryData();
}
