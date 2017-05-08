package com.zxxk.trainer;

import com.zxxk.achievement.KPointAchievement;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei.
 */
@Component
public class KPointTrainer extends NaiveBayesianTrainer {

    private int trainintDataSize;
    @Resource
    private KPointAchievement kPointAchievement;

    @Override
    public void save(int trainingDataSize, Map<String, Integer> featuresToRestore,
                     List<String> allLabels, Map<String, Integer> labelsToRestore) {
//        kPointAchievement.save(trainingDataSize, featuresToRestore, allLabels, labelsToRestore);
        if (trainingDataSize > 0) {
            this.trainintDataSize += trainingDataSize;
            return;
        }

        long t1 = System.currentTimeMillis();
        kPointAchievement.saveBaseInfo(this.trainintDataSize);
        long t2 = System.currentTimeMillis();
        System.out.println("finish saving base info..." + (t2 - t1) / 1000);
        kPointAchievement.saveLabels(allLabels, labelsToRestore);
        long t3 = System.currentTimeMillis();
        System.out.println("finish sving labels..." + (t3 - t2) / 1000);

        kPointAchievement.saveFeatures(featuresToRestore);
        long t4 = System.currentTimeMillis();
        System.out.println("finish saving features..." + (t4 - t3) / 1000);
    }

    public void setCourseId(int courseId) {
        kPointAchievement.setCourseId(courseId);
    }
}
