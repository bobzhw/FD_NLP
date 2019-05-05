import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.*;

public class computeSim {

    public static void main(String[] args) {
        String question = "已知,△ABC的外角∠CBD和∠BCE的平分线相交于点F,证明:点F在∠DAE的平分线上";
        List<String> questions = new ArrayList<String>();
        questions.add(question);
        questions.add("我贼");
        List<Integer> r = getSimiliary(question,questions,3);
        System.out.println(r);
    }

    public static List<Integer> getSimiliary(String question, List<String> questions,int wantSize)
    {
        List<Integer> r = new ArrayList<Integer>();
        if(questions.isEmpty())
            return r;
        List<String> questionWords = SegmentationWord(question);
        Map<Integer,Double> maps = new TreeMap<Integer, Double>();

        for(int i = 0;i<questions.size();i++)
        {
            List<String> list = SegmentationWord(questions.get(i));
            int count = 0;
            int sum = questionWords.size() + list.size();

            for(String s : list)
            {
                if(questionWords.contains(s))
                {
                    count++;
                    sum--;
                }
            }
            double res = (double)count / (double)sum;
            maps.put(i,res);
        }

        List<Map.Entry<Integer,Double>> list = new ArrayList<Map.Entry<Integer,Double>>(maps.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<Integer,Double>>() {
            //升序排序
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }

        });

        for(int i = 0;i<wantSize;i++)
        {
            if(i >= list.size())
                break;
            r.add(list.get(i).getKey());
        }
        return r;
    }

    private static List<String> SegmentationWord(String line)
    {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> sts = segmenter.process(line,JiebaSegmenter.SegMode.INDEX);
        List<String> words = new ArrayList<String>();
        for(SegToken st : sts)
        {
            words.add(st.word);
        }
        return words;
    }
}
