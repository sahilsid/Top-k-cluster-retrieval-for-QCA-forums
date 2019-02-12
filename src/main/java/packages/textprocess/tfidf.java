
package packages.textprocess;

import java.awt.Container;
import java.util.*;
import javafx.util.Pair;
import org.json.simple.JSONObject;

/**
 * tfidf
 */
public class tfidf {

    Map<String, Double> valueMap;
    Map<String, List<String>> data;

    public static void main(String[] args) {
        tfidf a = new tfidf();
    }

    tfidf() {
        data = new HashMap<String, List<String>>();
        valueMap = new HashMap<String, Double>();
        Preprocess p = new Preprocess();
        JSONObject questions = jsonImport.getjson("src/main/resources/android_questions.json");
        List<String> processed = new ArrayList<String>();

        for (Object key : questions.keySet()) {
            String k = (String) key;
            JSONObject value = (JSONObject) questions.get(key);
            processed = p.keywords((String) value.get("body"));
            data.put(k, processed);
        }
        initialize();
        // System.out.println(data);
    }

    void initialize() {
        for (String qid : data.keySet()) {
            for (String keywords : data.get(qid)) {
                valueMap.putIfAbsent(qid + "." + keywords, 0.0);
            }
        }
        assignTfIdf();
    }

    void assignTfIdf() {
        for (String key : valueMap.keySet()) {
            StringTokenizer token = new StringTokenizer(key, ".");
            String qid = token.nextToken();
            String word = token.nextToken();
            Double tf, idf;
            tf = 0.0;
            idf = 0.0;
            List<String> keywords = data.get(qid);
            tf = tf + Collections.frequency(keywords, word);
            tf = tf / keywords.size();
        
            for (String qids : data.keySet()) {
                if (data.get(qids).contains(word))
                    idf = idf + 1;
            }
            idf = idf / data.size();
            valueMap.replace(key, tf * idf);
        }
        System.out.println(valueMap);

    }
    Double similiarity(String q1, String q2){
        List<String> common_words = data.get(q1);
        common_words.retainAll(data.get(q2));
        Double psum = 0.0,i_sum_1=0.0,i_sum_2=0.0;
        for(String common_word : common_words){
            i_sum_1 = i_sum_1 + valueMap.get(q1+"."+common_word);
            i_sum_2 = i_sum_2 + valueMap.get(q2+"."+common_word);
            psum = psum + i_sum_1*i_sum_2;
        }
        i_sum_1 = Math.sqrt(i_sum_1);
        i_sum_2 = Math.sqrt(i_sum_2);

        return (psum/(i_sum_1*i_sum_2));
    }
}