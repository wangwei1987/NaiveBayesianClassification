package com.zxxk.dao;

import com.zxxk.domain.Label;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangwei on 17-4-28.
 */
@Repository
public interface LabelDao {

    int insert(Label label);

    int insertAll(List<Label> labels);

    List<Label> getAll(int courseId);

    Label get(@Param("courseId") int courseId, @Param("name") String name);

    void clear(int courseId);

    void update(Label label);

    List<Label> getValidLabels(int courseId);

    List<Label> getLabelsByNames(List<String> evaluatingLabelNames);
}
