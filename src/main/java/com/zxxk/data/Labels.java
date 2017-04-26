package com.zxxk.data;

import com.zxxk.exception.ClassificationException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
public class Labels {

    // 标签名
    private List<String> names;
    // 已学习的成果中各个标签下的数据数
    private List<Integer> counts;

    public Labels(String[] names, Integer[] counts) {
        if (ArrayUtils.isEmpty(names) || ArrayUtils.isEmpty(counts)) {
            throw new ClassCastException("the names and counts of labels should not be empty！");
        }
        this.names = Arrays.asList(names);
        this.counts = Arrays.asList(counts);
    }

    public Labels(String[] names) {
        if (ArrayUtils.isEmpty(names)) {
            throw new ClassCastException("the names should not be empty！");
        }
        this.names = Arrays.asList(names);
        this.counts = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            this.counts.add(0);
        }
    }

    public Labels() {

    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Integer> getCounts() {
        return counts;
    }

    public Integer getCount(int labelIndex) {
        return counts.get(labelIndex);
    }

    public Integer getCount(String label) {
        return counts.get(names.indexOf(label));
    }

    public void setCounts(List<Integer> counts) {
        this.counts = counts;
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

    public void countPlus(String label) {
        if (!names.contains(label)) {
            throw new ClassificationException("invalid label " + label);
        }
        int index = names.indexOf(label);
        counts.set(index, counts.get(index) + 1);
    }

    public void countPlus(List<String> labels) {
        if (CollectionUtils.isEmpty(labels)) {
            throw new ClassificationException("labels can not be empty!");
        }
        for (String label : labels) {
            countPlus(label);
        }
    }
}
