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
                    if (Pattern.compile("^[a-zA-z]$").matcher(word).matches()) continue;
                    if (Pattern.compile("^x[a-z0-9]{4}$").matcher(word).matches()) continue;
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
        String text = "<stem><p>已知数列<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/02071cc593774425b4551c405121154c.png\" wmf=\"http://static.zujuan.com/Upload/2012-02/13/1dcf93c0-3132-497f-a944-eef77e2054d1/resource.files/image1dcf93c0-3132-497f-a944-eef77e2054d1140.wmf\" />，其前<math guid=\"f401a17ab9d04ea5a98a4f54f072f519\" latex=\"$n$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/f401a17ab9d04ea5a98a4f54f072f519.png\" xmlns='http://www.w3.org/1998/Math/MathML'> <mi>n</mi></math>项和<math guid=\"5b838782aa614fe7b7ae4dd514644904\" latex=\"${{S}_{n}}$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/5b838782aa614fe7b7ae4dd514644904.png\" xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <msub>   <mi>S</mi>   <mi>n</mi>  </msub>  </mrow></math>满足<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/a0b174154f674f4eb5498b5ed159dee7.png\" wmf=\"http://static.zujuan.com/Upload/2012-02/13/1dcf93c0-3132-497f-a944-eef77e2054d1/resource.files/image1dcf93c0-3132-497f-a944-eef77e2054d1143.wmf\" />是大于0的常数），且<math guid=\"2286e842319f4069bfe1a9e1f4f01137\" latex=\"${{a}_{1}}=1,{{a}_{3}}=4$\" pic=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/2286e842319f4069bfe1a9e1f4f01137.png\" xmlns='http://www.w3.org/1998/Math/MathML'> <mrow>  <msub>   <mi>a</mi>   <mn>1</mn>  </msub>  <mo>=</mo><mn>1</mn><mo>,</mo><msub>   <mi>a</mi>   <mn>3</mn>  </msub>  <mo>=</mo><mn>4</mn></mrow></math></p><p>（1）求<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/05ad8023773f438c9fb23b9d973d9dd7.png\" wmf=\"http://static.zujuan.com/Upload/2012-02/13/1dcf93c0-3132-497f-a944-eef77e2054d1/resource.files/image1dcf93c0-3132-497f-a944-eef77e2054d1145.wmf\" />的值；</p><p>（2）求数列<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/02071cc593774425b4551c405121154c.png\" wmf=\"http://static.zujuan.com/Upload/2012-02/13/1dcf93c0-3132-497f-a944-eef77e2054d1/resource.files/image1dcf93c0-3132-497f-a944-eef77e2054d1140.wmf\" />的通项公式<i>a</i><sub>n</sub>；</p><p>（3）设数列<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/fd86583b4ead4158bac17c092b5d5749.png\" wmf=\"http://static.zujuan.com/Upload/2012-02/13/1dcf93c0-3132-497f-a944-eef77e2054d1/resource.files/image1dcf93c0-3132-497f-a944-eef77e2054d1146.wmf\" />的前<i>n</i>项和为<i>T<sub>n</sub></i>，试比较<img src=\"http://qbm-images.oss-cn-hangzhou.aliyuncs.com/QBM/2012/2/13/1570735397715968/1570735403196416/STEM/a00697f5b9774ebd96d0174ce56302bb.png\" wmf=\"http://static.zujuan.com/Upload/2012-02/13/1dcf93c0-3132-497f-a944-eef77e2054d1/resource.files/image1dcf93c0-3132-497f-a944-eef77e2054d1147.wmf\" />与<i>S<sub>n</sub></i>的大小.</p></stem>";
        List<String> words = SentenceAnalyzer.analyze(text.replaceAll("<[^>]+>", " ").replace("&gt;", ">"));

        System.out.println(words);
    }
}
