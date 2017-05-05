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
        String text = "<stem><p><img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/e2c1074d934f40c9a1a1f03b1f664ff8.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d13.wmf\" />（<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/257547a3ee48458fb1000add05397482.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d14.wmf\" />＞0，<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/17c22ad3a33d4c88ab7fb7302cf61264.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d15.wmf\" />＞0）在<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/63146aa77bc84e97ab51a505c04d147c.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d16.wmf\" />处取最大值，则（    ）</p><og cols=\"2\"><op><img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/1f28704168f649b89bcede1a85513656.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d17.wmf\" />一定是奇函数</op><op><img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/1f28704168f649b89bcede1a85513656.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d17.wmf\" />一定是偶函数</op><op><img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/b53e9634f65c4cea9b88e5f62097ffa0.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d19.wmf\" />一定是奇函数</op><op><img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2010/5/21/1569739251621888/1569739256455168/STEM/b53e9634f65c4cea9b88e5f62097ffa0.png\" wmf=\"http://static.zujuan.com/Upload/2010-05/21/ff23c67a-7e3c-4304-ae87-1385b87bbf4d/resource.files/imageff23c67a-7e3c-4304-ae87-1385b87bbf4d19.wmf\" />一定是偶函数</op></og></stem>";
        List<String> words = SentenceAnalyzer.analyze(text);

        System.out.println(words);
//        System.out.println(replaceMath(text));
    }

}
