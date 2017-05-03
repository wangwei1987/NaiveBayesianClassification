package com.zxxk.dao;

import org.springframework.stereotype.Repository;

/**
 * Created by wangwei on 17-4-27.
 */
@Repository
public interface QuestionDao {

    int getQuestionCount();

    int countZujuanQuestion(int courseId);
}
