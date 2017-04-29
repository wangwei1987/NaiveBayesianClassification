package com.zxxk.data;

import com.zxxk.domain.Data;
import com.zxxk.util.SentenceAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by shiti on 17-4-19.
 */
public class DataSource {

    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    // 数学、化学、物理
    private final static String[] subjects = {"1", "2", "3"};
    private final static String[] blackList = {"已知", "一个", "如图", "三个", "由此", "一些", "我们", "一天", "一条",};

    private List<String> allWords = new ArrayList<String>();                // 所有词语
    private List<Integer> countInLabel1 = new ArrayList<Integer>();         // label1下词语出现的次数
    private List<Integer> countInLabel2 = new ArrayList<Integer>();         // label2下词语出现的次数
    private List<Integer> count = new ArrayList<Integer>();                 // 词语出现的总次数

    private final static String driver = "com.mysql.jdbc.Driver";
    private final static String url = "jdbc:mysql://localhost:3306/test?characterEncoding=utf8";
    private final static String username = "root";
    private final static String password = "root";

    private Connection conn = null;

    public DataSource() {
        initDBConnection();
    }

    private void initDBConnection() {
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn = (Connection) DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Data> extractOraginalData(String subjectId, String limit, boolean training) {
        List<Data> result = new ArrayList<Data>();
        try {
            PreparedStatement statement = conn.prepareStatement(String.format("select qnumber,discpline_id,content from t_questions where discpline_id=%s limit " + limit, subjectId));
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getString(1));
                data.setLabel(rs.getString(2));
//                Map<String, Object> map = new HashMap<>();
//                map.put("qnumber", rs.getString(1));
//                map.put("subject", rs.getString(2));

                List<String> wordsFromSentence = SentenceAnalyzer.analyze(rs.getString(3).replaceAll("<[^>]+>", " "));
                List<String> finalWords = new ArrayList<String>();
                for (String word : wordsFromSentence) {

                    if (StringUtils.isNumeric(word)) continue;
                    if (Pattern.compile("^[0-9.,-]+$").matcher(word).matches()) continue;
                    if (Arrays.asList(blackList).contains(word)) continue;

                    word = word.replace("_", "");
                    word = word.replaceAll("^[0-9.]+([^0-9]{0,3})$", "$1");
//                    word = word.replaceAll("^[0-9.]+(公里|分|小时|千克|伏|厘米|秒|次|楼|千米|个|圈|辆|时|日|吨|件|名|元|kw|m|s|kg|km|min)$", "$1");
                    if (StringUtils.isEmpty(word)) continue;
                    finalWords.add(word);
                    if (false) {
                        if (!allWords.contains(word)) {
                            allWords.add(word);

                            switch (subjectId) {
                                case "1":
                                    countInLabel1.add(1);
                                    countInLabel2.add(0);
                                    count.add(1);
                                    break;
                                case "3":
                                    countInLabel2.add(1);
                                    countInLabel1.add(0);
                                    count.add(1);
                                    break;
                            }
                        } else {
                            int index = 0;
                            switch (subjectId) {
                                case "1":
                                    index = allWords.indexOf(word);
                                    countInLabel1.set(index, countInLabel1.get(index) + 1);
                                    count.set(index, count.get(index) + 1);
                                    break;
                                case "3":
                                    index = allWords.indexOf(word);
                                    countInLabel2.set(index, countInLabel2.get(index) + 1);
                                    count.set(index, count.get(index) + 1);
                                    break;
                            }
                        }
                    }
                }
                data.setFeatures(finalWords);
//                map.put("content", finalWords);
                if (finalWords.size() == 0) {
//                    System.out.println("空了");
                }
                result.add(data);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Data> extractOraginalDataOfQbm(String pointId, String pointId1, boolean flag, String limit) {
        List<Data> result = new ArrayList<Data>();

        PreparedStatement statement = null;
        try {
            String condition = "";
            if (pointId != null) {
                if (flag) {
                    condition = " and find_in_set('" + pointId + "',pointids) > 0";
                    if (pointId1 != null) {
                        condition += " and find_in_set('" + pointId1 + "',pointids) > 0";
                    }
                } else {
                    condition = " and find_in_set('" + pointId + "',pointids) = 0";
                    if (pointId1 != null) {
                        condition += " and find_in_set('" + pointId1 + "',pointids) = 0";
                    }
                }
            }

            statement = conn.prepareStatement("select qq.questionid, qq.stem, qk.pointids from question_kpoints qk " +
                    "join questions q on qk.questionid=q.id join question_qmls qq on qq.questionid=q.id where " +
                    "applicationid='zujuan' and courseid=27 " + condition + " limit " + limit);
            System.out.println(statement);
//            if (flag) {
//
//
//                statement = conn.prepareStatement("select qq.questionid, qq.stem, qk.pointids from question_kpoints qk " +
//                        "join questions q on qk.questionid=q.id join question_qmls qq on qq.questionid=q.id where " +
//                        "applicationid='zujuan' and courseid=27 and "+condition+" limit " + limit);
//            } else {
//                statement = conn.prepareStatement("select qq.questionid, qq.stem, qk.pointids from question_kpoints qk join questions q on qk.questionid=q.id " +
//                        " join question_qmls qq on qq.questionid=q.id where applicationid='zujuan' and courseid=27 and find_in_set('" + pointId + "',pointids) = 0" +
//                        " and find_in_set('" + pointId1 + "',pointids) = 0 limit " + limit);
//            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Data data = new Data();
                data.setId(rs.getString(1));

                String labels = rs.getString(3);
                if(!labels.contains(",")) {
                    data.setLabel(labels);
                }
                else {
                    data.setLabels(Arrays.asList(labels.split(",")));
                }

//                if (rs.getString(3).equals(pointId)) {
//                    data.setLabel(pointId);
//                } else {
//                    data.setLabel("-" + pointId);
//                }

//                Map<String, Object> map = new HashMap<>();
//                map.put("qnumber", rs.getString(1));
//                map.put("subject", rs.getString(2));

                List<String> wordsFromSentence = SentenceAnalyzer.analyze(rs.getString(2).replaceAll("<[^>]+>", " "));
                List<String> finalWords = new ArrayList<String>();
                for (String word : wordsFromSentence) {

                    if (StringUtils.isNumeric(word)) continue;
                    if (Pattern.compile("^[0-9.,-]+$").matcher(word).matches()) continue;
                    if (Arrays.asList(blackList).contains(word)) continue;

                    word = word.replace("_", "");
                    word = word.replaceAll("^[0-9.]+([^0-9]{0,3})$", "$1");
//                    word = word.replaceAll("^[0-9.]+(公里|分|小时|千克|伏|厘米|秒|次|楼|千米|个|圈|辆|时|日|吨|件|名|元|kw|m|s|kg|km|min)$", "$1");
                    if (StringUtils.isEmpty(word)) continue;

//                    System.out.println(word);
                    finalWords.add(word);
                }
                data.setFeatures(finalWords);
//                map.put("content", finalWords);
                if (finalWords.size() == 0) {
//                    System.out.println("空了");
                }
                result.add(data);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testExtractOraginalDataOfQbm() {
//        extractOraginalDataOfQbm("1", true, "0, 10");
    }

    int sucess = 0;
    int equal = 0;

    public void evalute() {
        List<Data> testDatas = extractOraginalData("1", "20000, 5000", false);
        testDatas.addAll(extractOraginalData("3", "20000, 500", false));
        for (Data data : testDatas) {
            List<String> features = data.getFeatures();

            double value1 = 1;
            double value2 = 1;
            for (String feature : features) {
                if (!allWords.contains(feature)) continue;
                int index = allWords.indexOf(feature);
                if (count.get(index) <= 3) continue;
//                System.out.println("test111 :   "+countInLabel1.get(index)+", "+countInLabel2.get(index));
                value1 *= countInLabel1.get(index) == 0 ? 0.01 / 1000 : countInLabel1.get(index) * 1.0 / 1000;
                value2 *= countInLabel2.get(index) == 0 ? 0.01 / 1000 : countInLabel2.get(index) * 1.0 / 1000;
            }
            String subjectId = data.getLabel();
//            System.out.println((String)data.get("qnumber")+", value1 : "+value1 + ", value2 : "+value2+", subjectId : "+subjectId);

            if (value1 > value2) {
                if (subjectId.equals("1")) {
                    sucess++;
                } else {
                    System.err.println((String) data.getId() + ", value1 : " + value1 + ", value2 : " + value2 + ", subjectId : " + subjectId);
                }
            } else if (value1 < value2) {
                if (subjectId.equals("3")) {
                    sucess++;
                } else
                    System.err.println((String) data.getId() + ", value1 : " + value1 + ", value2 : " + value2 + ", subjectId : " + subjectId);

            } else {
                equal++;
                System.out.println((String) data.getId() + ", value1 : " + value1 + ", value2 : " + value2 + ", subjectId : " + subjectId);

            }
        }
        System.out.println("testData.size : " + testDatas.size());
        System.out.println("预测成功率为：" + sucess + ", " + equal);
    }

    //    @Test
    public List<String> fetchAllKpointId(int courseId) {
        try {
            List<String> pointIds = new ArrayList<>();
            PreparedStatement preparedStatement = conn.prepareStatement("select id from knowledge_points " +
                    "where (type='KNOWLEDGE_POINT' or type = 'TESTING_POINT') and courseid = " + courseId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                pointIds.add(rs.getString(1));
            }
            return pointIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testExtractOraginalData() {
//        extractOraginalData("1", "0, 20000", true);
//        extractOraginalData("3", "0, 20000", true);
//
//        for (int i = 0; i < allWords.size(); i++) {
//            if (i != 0 && i % 6 == 0) {
//                System.out.println();
//            }
//
//            System.out.printf(allWords.get(i) + " :(" + count.get(i) + ", " + countInLabel1.get(i) + ", " + countInLabel2.get(i) + ") ||   ");
//        }
//        System.out.println(allWords.size());
//        evalute();
        try {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO ml_feature1 values (\"王位\",\"_other\",500, 27)");
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
