package com.zxxk.learner;

import com.zxxk.dao.FeatureDao;
import com.zxxk.dao.LabelDao;
import com.zxxk.domain.Feature;
import com.zxxk.domain.Label;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei on 17-5-2.
 */
@Service
public class LearnerSaver {

    private static final String SEPERATOR = "###";

    private int courseId;

    @Resource
    private FeatureDao featureDao;
    @Resource
    private LabelDao labelDao;

    @Transactional
    public void restoreFeatures(Map<String, Integer> featuresToStore) {
        for (Map.Entry<String, Integer> entry : featuresToStore.entrySet()) {
            String[] props = entry.getKey().split(SEPERATOR);
            Feature feature = new Feature(Integer.valueOf(props[0]), props[1], props[2], entry.getValue());

            Feature featureInDB = featureDao.get(Integer.valueOf(props[0]), props[1], props[2]);
            if (featureInDB == null) {
                featureDao.insert(feature);
            } else {
                featureDao.plusCount(feature);
            }
        }
    }

    @Transactional
    public void restoreLabels(List<String> allLabels, Map<String, Integer> labelToStore) {
        for (String labelName : allLabels) {
            Label label = null;
            Integer labelCount = labelToStore.get(labelName);
            if (labelCount != null) {
                label = new Label(courseId, labelName, labelCount);
            } else {
                label = new Label(courseId, labelName, 0);
            }

            Label labelInDB = labelDao.get(courseId, labelName);
            if (labelInDB == null) {
                labelDao.insert(label);
            } else {
                labelInDB.setCount(labelInDB.getCount() + label.getCount());
                labelDao.update(label);
            }
        }


//        for (Map.Entry<String, Integer> entry : labelToStore.entrySet()) {
//            Label label = new Label(entry.getKey(), entry.getValue());
//            labelDao.insert(label);
//        }
//        // 某些标签在训练数据中没有出现过，这些标签也要存入数据库
//        List<String> copiedLabels = new ArrayList<>(Arrays.asList(new String[labels.size()]));
//        Collections.copy(copiedLabels, labels);
//        copiedLabels.removeAll(labelToStore.entrySet());
//        if(copiedLabels.size() > 0) {
//            copiedLabels.stream().forEach(labelName -> {
//                Label label = new Label(labelName, 0);
//                labelDao.insert(label);
//            });
//        }

//        for (int i = 0; i < labelToStore.getNames().size(); i++) {
//            Label label = new Label(labelToStore.get(i), labelToStore.getCount(i));
//            labelDao.insert(label);
//        }
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
