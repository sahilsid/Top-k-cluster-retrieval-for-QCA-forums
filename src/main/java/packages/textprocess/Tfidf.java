
package packages.textprocess;

import java.awt.Container;
import java.util.*;
import javafx.util.Pair;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * tfidf
 */
public class Tfidf {

    Map<String, Double> valueMap;
    Map<String, Double> similiarityMap;
    Map<String, List<String>> data;
    JsonNode questions;
    List<String> processed;

    public static void main(String[] args) throws Exception {
        Tfidf a = new Tfidf();
    }

    public Tfidf() throws Exception {
        data = new HashMap<String, List<String>>();
        valueMap = new HashMap<String, Double>();
        similiarityMap = new HashMap<String, Double>();
        Preprocess p = new Preprocess();
        questions = jsonImport.getjsonlarge("src/main/resources/android/android_questions.json");
        processed = new ArrayList<String>();
        Iterator<String> nodeIterator = questions.fieldNames();

        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();
            //System.out.println(key);
            processed = p.keywords(questions.get(key).get("body").toString());
            data.put(key, processed);
        }
        initialize();
    }

    public JsonNode getquestions() {
        return questions;
    }

    public Map<String, List<String>> getdata() {
        return data;
    }

    public List<String> getprocessedData() {
        return processed;
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
             System.out.println("Word : "+ word + " TF : " + tf +" Idf " + idf);

            tf = tf / keywords.size();
            idf = 1 + Math.log(data.size() / idf);
            valueMap.replace(key, tf * idf);
             System.out.println("Word c : "+ word + " TF : " + tf*idf +" Idf " + idf);

        }
        generateSimiliarityMap();
    }

    public Double similiarity(String q1, String q2) {
        List<String> common_words = new ArrayList<String>(data.get(q1));
        Double psum = 0.0, i_sum_1 = 0.0, i_sum_2 = 0.0;
        List<String> q1_words = new ArrayList<String>(data.get(q1));
        List<String> q2_words = new ArrayList<String>(data.get(q2));

        for (String word : q1_words) {
            i_sum_1 = i_sum_1 + Math.pow(valueMap.get(q1 + "." + word), 2);
        }
        for (String word : q2_words) {
            i_sum_2 = i_sum_2 + Math.pow(valueMap.get(q2 + "." + word), 2);
        }
        common_words.retainAll(data.get(q2));
        // System.out.println(q1 + " : " + q2 +" : " +common_words);

        for (String common_word : common_words) {
            psum = psum + valueMap.get(q1 + "." + common_word) * valueMap.get(q2 + "." + common_word);
            // System.out.println(q1+"."+q2 + " : "+ common_word + " : " + valueMap.get(q1 +
            // "." + common_word) +" : " +valueMap.get(q2 + "." + common_word));
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