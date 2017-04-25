package com.zxxk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shiti on 17-4-20.
 */
public class Features {
    /**
     * 所有的特征
     */
    private List<String> names;
    /**
     * 特征在各个Label中的数量
     */
    private List<List<Integer>> counts;

    public Features(int labelSize) {
        names = new ArrayList<>();
        counts = new ArrayList<>();
        // counts的长度为labelsize+1,第一项用来存储label出现的总数
        for (int i = 0; i <= labelSize; i++) {
            counts.add(new ArrayList<Integer>());
        }
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<List<Integer>> getAllCounts() {
        return counts;
    }

//    public void setCounts(List<List<Integer>> counts) {
//        this.counts = counts;
//    }

    public boolean contains(String feature) {
        return names.contains(feature);
    }

    public void addFeature(String feature) {
        names.add(feature);
    }

    public void initCount(int index, int count) {
        counts.get(index).add(count);
    }

    public int indexOf(String feature) {
        return names.indexOf(feature);
    }

    public void plus(int labelIndex, int featureIndex) {
        counts.get(labelIndex).set(featureIndex, counts.get(labelIndex).get(featureIndex) + 1);
    }

    public int size() {
        return names.size();
    }

    public List<Integer> getCounts(int index) {
        return counts.get(index);
    }

    public String getFeature(int index) {
        return names.get(index);
    }
}
