package com.zxxk.dao;

import com.zxxk.domain.Data;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangwei on 17-4-27.
 */
@Repository
public interface KPointDao {

    List<Data> getKpointIdsWithQidAndStem(@Param("courseId") int courseId, @Param("start") int start, @Param("limit") int limit);

    List<String> getTop20KpointIds(int courseId);

    List<String> getAllKpointIds(int courseId);
}
