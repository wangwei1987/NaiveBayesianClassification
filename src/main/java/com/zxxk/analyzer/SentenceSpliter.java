package com.zxxk.analyzer;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by shiti on 17-4-17.
 */
public class SentenceSpliter {

    private final static String[] blackList = {"已知", "一个", "如图", "三个", "由此", "一些", "我们", "一天", "一条",};


    public static List<String> analyze(String text) {
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
                if (!term.toString().trim().equals(""))
                    result.add(term.toString());
            }
            reader.close();
//        System.out.println();
            return new ArrayList<>(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String text = "<stem><p>若命题P：“<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/8/16/1569817032122368/1569817037283328/STEM/1125bbd16bf24cebb910cbeac5e15207.png\" wmf=\"http://static.zujuan.com/Upload/2010-08/16/171c5cec-cf53-4965-a496-67305d7efc27/resource.files/image171c5cec-cf53-4965-a496-67305d7efc2765.wmf\" />x＞0，<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/8/16/1569817032122368/1569817037283328/STEM/e399b8f0ef674e6584c0c97911a9a966.png\" wmf=\"http://static.zujuan.com/Upload/2010-08/16/171c5cec-cf53-4965-a496-67305d7efc27/resource.files/image171c5cec-cf53-4965-a496-67305d7efc2766.wmf\" />”是真命题，则实数a的取值范围是___．</p></stem>";
        List<String> words = SentenceSpliter.analyze(text.replaceAll("<[^>]+>", " ").replace("&gt;", ">"));
        List<String> finalWords = new ArrayList<>();
        for (String word : words) {
            if (StringUtils.isNumeric(word)) continue;
            if (Pattern.compile("^[0-9.,-]+$").matcher(word).matches()) continue;
            if (Arrays.asList(blackList).contains(word)) continue;

            word = word.replace("_", "");
            word = word.replaceAll("^[0-9.]+([^0-9]{0,3})$", "$1");
            if (!StringUtils.isEmpty(word)) {
                finalWords.add(word);
            }
        }
        System.out.println(words);
    }
}
