package com.zxxk.service.impl;

import com.zxxk.dao.BaseInfoDao;
import com.zxxk.domain.BaseInfo;
import com.zxxk.service.BaseInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by wangwei on 17-5-2.
 */
@Service
public class BaseInfoServiceImpl implements BaseInfoService {

    @Resource
    private BaseInfoDao baseInfoDao;

    @Override
    public void save(int courseId, int trainingDataSive) {
        BaseInfo baseInfo = baseInfoDao.get(courseId);
        if (baseInfo == null) {
            baseInfoDao.insert(new BaseInfo(courseId, trainingDataSive));
        } else {
            baseInfo.setDataSize(baseInfo.getDataSize() + trainingDataSive);
            baseInfoDao.update(baseInfo);
        }
    }
}
