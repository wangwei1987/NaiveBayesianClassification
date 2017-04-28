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
                    if (StringUtils.isNumeric(word)) continue;
                    if (Pattern.compile("^[0-9.,-]+$").matcher(word).matches()) continue;

                    word = word.replace("_", "");
                    word = word.replaceAll("^[0-9.]+([^0-9]{0,3})$", "$1");
                    if (StringUtils.isEmpty(word)) continue;

                    result.add(term.toString());
                }

            }
            reader.close();
            return new ArrayList<>(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String text = "<stem><p>在<math guid=\"5a2992a585eb4897814d61e06d8c361e\" latex=\"$\\Delta&#32;ABC$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/5a2992a585eb4897814d61e06d8c361e.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>&#x0394;</mi><mi>A</mi><mi>B</mi><mi>C</mi></mrow></math>中，角<math guid=\"06223a1a51a14e7e99320ca2b37cd79c\" latex=\"$A,B,C$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/06223a1a51a14e7e99320ca2b37cd79c.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>A</mi><mo>,</mo><mi>B</mi><mo>,</mo><mi>C</mi></mrow></math>的对边分别为<math guid=\"ba1f7eb4f8ca4b3ba3a692247c13bdf0\" latex=\"$a,b,c$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/ba1f7eb4f8ca4b3ba3a692247c13bdf0.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>a</mi><mo>,</mo><mi>b</mi><mo>,</mo><mi>c</mi></mrow></math>，已知<math guid=\"1bf36b1b1b7440b09f640068a374a92c\" latex=\"$3\\cos&#32;(B-C)-1=6\\cos&#32;B\\cos&#32;C$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/1bf36b1b1b7440b09f640068a374a92c.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mn>3</mn><mi>cos</mi><mo stretchy='false'>(</mo><mi>B</mi><mo>&#x2212;</mo><mi>C</mi><mo stretchy='false'>)</mo><mo>&#x2212;</mo><mn>1</mn><mo>=</mo><mn>6</mn><mi>cos</mi><mi>B</mi><mi>cos</mi><mi>C</mi></mrow></math>.</p><p>（1）求<math guid=\"937324d60264422eb96f3e813d45b233\" latex=\"$\\cos&#32;A$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/937324d60264422eb96f3e813d45b233.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>cos</mi><mi>A</mi></mrow></math>；</p><p>（2）若<math guid=\"f63d845369444937aa9335b08d212093\" latex=\"$a=3$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/f63d845369444937aa9335b08d212093.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>a</mi><mo>=</mo><mn>3</mn></mrow></math>，<math guid=\"5a2992a585eb4897814d61e06d8c361e\" latex=\"$\\Delta&#32;ABC$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/5a2992a585eb4897814d61e06d8c361e.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>&#x0394;</mi><mi>A</mi><mi>B</mi><mi>C</mi></mrow></math>的面积为<math guid=\"97af5ba020204299b622a389154ec15f\" latex=\"$2\\sqrt{2}$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/97af5ba020204299b622a389154ec15f.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mn>2</mn><msqrt>   <mn>2</mn>  </msqrt>  </mrow></math>，求<math guid=\"560f1bfb509b4578aa3735b998ffb381\" latex=\"$b,c$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2016/11/3/1573114276339712/1573114282647552/STEM/560f1bfb509b4578aa3735b998ffb381.png\"  xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <mi>b</mi><mo>,</mo><mi>c</mi></mrow></math>.</p></stem>";
        List<String> words = SentenceAnalyzer.analyze(text.replaceAll("<[^>]+>", " ").replace("&gt;", ">"));
        List<String> finalWords = new ArrayList<>();
        for (String word : words) {
            if (StringUtils.isNumeric(word)) continue;
            if (Pattern.compile("^[0-9.,-]+$").matcher(word).matches()) continue;

            word = word.replace("_", "");
            word = word.replaceAll("^[0-9.]+([^0-9]{0,3})$", "$1");
            if (!StringUtils.isEmpty(word)) {
                finalWords.add(word);
            }
        }
        System.out.println(words);
    }
}
