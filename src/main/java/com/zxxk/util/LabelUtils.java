package com.zxxk.util;

import com.zxxk.exception.ClassificationException;
import com.zxxk.learner.Labels;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 17-5-6.
 */
public class LabelUtils {

    /**
     * 过滤出有效的label
     *
     * @param labelsOfData
     * @return
     */
    public static List<String> filterValidLabels(List<String> labelsOfData, List<String> allLabels) {
        if (CollectionUtils.isEmpty(labelsOfData) || CollectionUtils.isEmpty(allLabels)) {
            throw new ClassificationException("labelsOfData and allLabels can't be empty!");
        }
        // 清除无效的labels
        List<String> validLabels = new ArrayList<>();
        for (String label : labelsOfData) {
            if (allLabels.indexOf(label) >= 0) {
                validLabels.add(label);
            }
        }
        if (CollectionUtils.isEmpty(validLabels)) {
            validLabels.add(Labels.LABEL_OTHER);
        }
        return validLabels;
    }
}
