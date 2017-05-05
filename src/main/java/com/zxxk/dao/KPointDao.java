package com.zxxk.dao;

import com.zxxk.data.Data;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by wangwei on 17-4-27.
 */
@Repository
public interface KPointDao {

    List<Data> getKpointIdsWithQidAndStem(@Param("courseId") int courseId, @Param("start") int start, @Param("limit") int limit, @Param("labelNames") List<String> labelNames);

    List<String> getTop20KpointIds(int courseId);

    List<String> getAllKpointIds(int courseId);

    List<String> getKPointNames(List<String> predictedLabels);

    List<String> getkpointNamesByQid(String questionId);

    List<String> getValidLabels(Integer courseId);

    List<Map<String, Object>> getKPointsWithCount();

    void saveKPointCount(List<Map<String, Object>> KPointCounts);
}
