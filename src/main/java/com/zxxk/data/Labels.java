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

    public static final String LABEL_OTHER = "_other";

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

    public Labels(List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            throw new ClassCastException("the names should not be empty！");
        }
        this.names = names;
        // if data not belone to any labels, then it belone to _other
        this.names.add(LABEL_OTHER);
        this.counts = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
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

    public List<Integer> indexOf(List<String> labels) {
        List<Integer> indexes = new ArrayList<>();
        for(String label : labels) {
            indexes.add(names.indexOf(label));
        }
        return indexes;
    }

    public String get(int index) {
        return names.get(index);
    }

    public void countPlus(String label) {
        if (!names.contains(label)) {
            counts.set(names.size() - 1, counts.get(names.size() - 1) + 1);
        }
        else {
            int index = names.indexOf(label);
            counts.set(index, counts.get(index) + 1);
        }
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
