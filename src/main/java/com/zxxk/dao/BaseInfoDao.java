package com.zxxk.dao;

import com.zxxk.domain.BaseInfo;
import org.springframework.stereotype.Repository;

/**
 * Created by wangwei on 17-4-29.
 */
@Repository
public interface BaseInfoDao {

    BaseInfo get(int courseId);

    void insert(BaseInfo baseInfo);
}
