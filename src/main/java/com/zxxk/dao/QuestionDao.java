package com.zxxk.dao;

import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by wangwei on 17-4-27.
 */
@Repository
public interface QuestionDao {

    int getQuestionCount();

    int countZujuanQuestion(int courseId);

    String getStem(String questionId);

    Map<String, Object> getQuestionById(String questionId);
}
