package trainer;

import com.zxxk.data.Data;
import com.zxxk.exception.ClassificationException;
import com.zxxk.learner.Labels;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * naive bayesian trainer
 * Created by wangwei on 17-5-2.
 */
public abstract class NaiveBayesianTrainer implements Trainer {

    // seperator between feature and labels
    public static String SEPERATOR = "###";

    protected Map<String, Integer> featuresToRestore = new HashMap<>();

    protected Map<String, Integer> labelsToRestore = new HashMap<>();

    private List<String> labels;

    private int trainingDataSize = 0;

    // 当features的数量达到此阈值时，将触发保存操作,不需要此功能的话，可以将此值设为0
    private int featureThreshold = 10000;

    // 保存学习成果
    protected abstract void save();

    @Override
    public void train(List<Data> trainingData) {
        checkLabels();

        for (Data data : trainingData) {
            trainingDataSize++;

            data.setLabels(filterValidLabels(data.getLabels()));

            data.getLabels().stream().forEach(label -> {
                if (labelsToRestore.containsKey(label)) {
                    labelsToRestore.put(label, labelsToRestore.get(label) + 1);
                } else {
                    labelsToRestore.put(label, 1);
                }
            });

            List<String> featuresInData = data.getFeatures();
            List<String> labelsOfData = data.getLabels();

            // labels is empty or invalid, set the label to _other
            if (CollectionUtils.isEmpty(labelsOfData)) {
                // if label is empty, set its label to _other
                labelsOfData = new ArrayList<>();
                labelsOfData.add(Labels.LABEL_OTHER);
            } else {
                // clear invalid laels
                data.setLabels(filterValidLabels(labelsOfData));
                labelsOfData = data.getLabels();
            }

            for (String feature : featuresInData) {

                addFeatureCount(feature + SEPERATOR + "_total");
                for (String label : labelsOfData) {
                    addFeatureCount(feature + SEPERATOR + label);
                }
            }

            // 待保存的feature的数量超出阈值时，出发保存操作
            if (featureThreshold > 0 && featuresToRestore.size() > featureThreshold) {
                save();
            }
        }
    }

    /**
     * 清除无效的label
     *
     * @param labelsOfData
     * @return
     */
    private List<String> filterValidLabels(List<String> labelsOfData) {
        // clear invalid labels
        List<String> finalLabels = new ArrayList<>();
        for (String label : labelsOfData) {
            if (labels.indexOf(label) >= 0) {
                finalLabels.add(label);
            }
        }
        if (CollectionUtils.isEmpty(finalLabels)) {
            finalLabels.add(Labels.LABEL_OTHER);
        }
        return finalLabels;
    }

    private void addFeatureCount(String key) {
        if (featuresToRestore.containsKey(key)) {
            featuresToRestore.put(key, featuresToRestore.get(key) + 1);
        } else {
            featuresToRestore.put(key, 1);
        }
    }

    private void checkLabels() {
        if (CollectionUtils.isEmpty(labels)) {
            throw new ClassificationException("labels can not be empty!");
        }
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getLabels() {
        return labels;
    }

    public int getTrainingDataSize() {
        return trainingDataSize;
    }

    public void setFeatureThreshold(int featureThreshold) {
        featureThreshold = featureThreshold;
    }

}
