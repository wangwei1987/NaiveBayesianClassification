package com.zxxk.dao;

import com.zxxk.domain.Label;
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
}
