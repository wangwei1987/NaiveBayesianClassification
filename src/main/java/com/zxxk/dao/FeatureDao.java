package com.zxxk.dao;

import com.zxxk.domain.Feature;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangwei on 17-4-28.
 */
@Repository
public interface FeatureDao {

    int insert(Feature feature);

    int insertAll(List<Feature> features);

    Feature get(@Param("courseId") int courseId, @Param("name") String name, @Param("label") String label);

    List<Feature> getByName(String name);

    void plusCount(Feature feature);

    void clear(int courseId);
}
