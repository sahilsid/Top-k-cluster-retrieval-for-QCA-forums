
package packages.textprocess;

import java.awt.Container;
import java.util.*;
import javafx.util.Pair;
import org.json.simple.JSONObject;

/**
 * tfidf
 */
public class Tfidf {

    Map<String, Double> valueMap;
    Map<String, Double> similiarityMap;
    Map<String, List<String>> data;

    public static void main(String[] args) {
        Tfidf a = new Tfidf();
    }

    public Tfidf() {
        data = new HashMap<String, List<String>>();
        valueMap = new HashMap<String, Double>();
        similiarityMap = new HashMap<String, Double>();
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

            for (String qids : data.keySet()) {
                if (data.get(qids).contains(word))
                    idf = idf + 1;
            }
            tf = tf / keywords.size();
            idf = 1 + Math.log(data.size() / idf);
            valueMap.replace(key, tf * idf);
        }
        generateSimiliarityMap();
    }

    public Double similiarity(String q1, String q2) {
        List<String> common_words = new ArrayList<String>(data.get(q1));
        Double psum = 0.0, i_sum_1 = 0.0, i_sum_2 = 0.0;

        common_words.retainAll(data.get(q2));

        for (String common_word : common_words) {
            i_sum_1 = i_sum_1 + valueMap.get(q1 + "." + common_word);
            i_sum_2 = i_sum_2 + valueMap.get(q2 + "." + common_word);
            psum = psum + i_sum_1 * i_sum_2;
        }
        i_sum_1 = Math.sqrt(i_sum_1);
        i_sum_2 = Math.sqrt(i_sum_2);

        return (i_sum_1 * i_sum_2 > 0) ? psum / (i_sum_1 * i_sum_2) : 0.0;
    }

    void generateSimiliarityMap() {
        for (Object qid1 : data.keySet()) {
            for (Object qid2 : data.keySet()) {
                if (!similiarityMap.containsKey(qid1 + "." + qid2) && !similiarityMap.containsKey(qid2 + "." + qid1))
                    similiarityMap.put(qid1 + "." + qid2, similiarity((String) qid1, (String) qid2));
            }
        }
        // System.out.println("Similiarity Map Generated : \n \t" + similiarityMap);
    }

    public Double getSimiliarity(String qid1, String qid2) {
        Double similiarity = 0.0;
        similiarity = (similiarityMap.containsKey(qid1 + "." + qid2) ? similiarityMap.get(qid1 + "." + qid2)
                : (similiarityMap.containsKey(qid1 + "." + qid2) ? similiarityMap.get(qid2 + "." + qid1) : 0.0));
        return similiarity;
    }
}