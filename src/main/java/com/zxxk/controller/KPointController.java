package com.zxxk.controller;

import com.zxxk.controller.vo.KPointVO;
import com.zxxk.dao.KPointDao;
import com.zxxk.dao.QuestionDao;
import com.zxxk.data.Data;
import com.zxxk.evaluator.MultiLabelPrediction;
import com.zxxk.learner.DBLearner;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by wangwei on 17-5-4.
 */
@RestController
@RequestMapping("/kpoints")
public class KPointController {

    @Resource
    private DBLearner dbLearner;
    @Resource
    private KPointDao kPointDao;
    @Resource
    private QuestionDao questionDao;

//    @RequestMapping(method = RequestMethod.GET)
//    public Map<String, String> sayHello() {
//        Map<String, String> map = new HashMap<>();
//        map.put("message", "this is wangwei saying hello world");
//        return map;
//    }

    @RequestMapping(value = "/{questionId}", method = RequestMethod.GET)
    public KPointVO predictedLabelsByQustionId(@PathVariable("questionId") String questionId) {
        String stem = questionDao.getStem(questionId);
        if (stem == null) return new KPointVO();

        Data data = new Data();
        data.setStem(stem);
        data.buildLabelsAndFeatures();
        MultiLabelPrediction prediction = dbLearner.predictMultiLabel(27, data);

        List<String> labels = kPointDao.getkpointNamesByQid(questionId);
        List<String> predictedLabels = kPointDao.getKPointNames(prediction.getPredictedLabels());
        KPointVO kPointVO = new KPointVO(labels, predictedLabels);
        return kPointVO;
    }

    @RequestMapping(method = RequestMethod.POST)
    public List<String> predictedLabelsByStem(@RequestBody Map<String, String> params) {
        Data data = new Data();
        data.setStem(params.get("stem"));
        data.buildLabelsAndFeatures();
        MultiLabelPrediction prediction = dbLearner.predictMultiLabel(27, data);
        return kPointDao.getKPointNames(prediction.getPredictedLabels());
    }
}
