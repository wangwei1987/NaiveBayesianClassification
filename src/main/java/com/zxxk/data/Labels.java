package com.zxxk.data;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
public class Labels {

    // 标签名
    private List<String> names;
    // 已学习的成果中各个标签下的数据数
    private List<Integer> distribution;

    public Labels(String[] names, Integer[] distribution) {
        if (ArrayUtils.isEmpty(names) || ArrayUtils.isEmpty(distribution)) {
            throw new ClassCastException("the names and distribution of labels should not be empty！");
        }
        this.names = Arrays.asList(names);
        this.distribution = Arrays.asList(distribution);
    }

    public Labels() {

    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Integer> getDistributions() {
        return distribution;
    }

    public Integer getDistribution(int labelIndex) {
        return distribution.get(labelIndex);
    }

    public Integer getDistribution(String label) {
        return distribution.get(names.indexOf(label));
    }

    public void setDistribution(List<Integer> distribution) {
        this.distribution = distribution;
    }

    public int size() {
        return names.size();
    }

    public int indexOf(String label) {
        return names.indexOf(label);
    }

    public String get(int index) {
        return names.get(index);
    }
}
