package com.zxxk.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by shiti on 17-4-17.
 */
public class SentenceAnalyzer {

    private final static String[] blackList = {"已知", "一个", "如图", "三个", "由此", "一些", "我们", "一天", "一条"};


    public static List<String> analyze(String text) {
        text = text.replaceAll("<math.*?latex=\"(.*?)\".*?</math>", "$1");
        text = text.replaceAll("&#[0-9]{1,4}", " ");
        text = text.replaceAll("<[^>]+>", " ").replace("&gt;", ">").replace("&lt;", "<");

        Set<String> result = new HashSet<>();

        //创建分词对象
        Analyzer anal = new IKAnalyzer(true);
        StringReader reader = new StringReader(text);
        //分词
        TokenStream ts = null;
        try {
            ts = anal.tokenStream("", reader);
            CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
            //遍历分词数据
            while (ts.incrementToken()) {
//            System.out.print(term.toString()+"|");
                if (!term.toString().trim().equals("")) {

                    String word = term.toString();
//                    if (Pattern.compile("^[0-9,]+$").matcher(word).matches()) continue;
                    if (Pattern.compile("^[a-zA-Z]?$").matcher(word).matches()) continue;
//                    if (Pattern.compile("^x[a-z0-9]{4}$").matcher(word).matches()) continue;
                    if (Pattern.compile("^[0-9.,-]+$").matcher(word).matches()) continue;

//                    word = word.replace("_", "");
                    word = word.replaceAll("^[0-9.]+([^0-9]{0,3})$", "$1");
                    if (StringUtils.isEmpty(word)) continue;

                    result.add(word);
                }

            }
            reader.close();
            return new ArrayList<>(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String replaceMath(String text) {
        text = text.replaceAll("<math.*?latex=\"(.*?)\".*?</math>", "$1");
        return text;
    }

    public static void main(String[] args) throws IOException {
        String text = "<stem><p>如果随机变量X服从N (<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/9/1/1569824318873600/1569824323969024/STEM/15aca4df91744c7aa5e59250329c3074.png\" wmf=\"http://static.zujuan.com/Upload/2010-09/01/d5c34b0b-32de-4e80-842e-cb44e9cf308c/resource.files/imaged5c34b0b-32de-4e80-842e-cb44e9cf308c47.wmf\" />)且E(X)=3,D(X)=1,则<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/9/1/1569824318873600/1569824323969024/STEM/79691449f83d4bdabc52b6b0e54f4eec.png\" wmf=\"http://static.zujuan.com/Upload/2010-09/01/d5c34b0b-32de-4e80-842e-cb44e9cf308c/resource.files/imaged5c34b0b-32de-4e80-842e-cb44e9cf308c48.wmf\" />=<span u>    </span><img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/9/1/1569824318873600/1569824323969024/STEM/a9d7509c0c6141bc8bbf6447d0feaf00.png\" wmf=\"http://static.zujuan.com/Upload/2010-09/01/d5c34b0b-32de-4e80-842e-cb44e9cf308c/resource.files/imaged5c34b0b-32de-4e80-842e-cb44e9cf308c49.wmf\" />=<span u>      </span></p></stem>";
        List<String> words = SentenceAnalyzer.analyze(text);

        System.out.println(words);
//        System.out.println(replaceMath(text));
    }
}
