
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
    Map<String, Double> idfMap;
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
        idfMap = new HashMap<String, Double>();
        similiarityMap = new HashMap<String, Double>();
        Preprocess p = new Preprocess();
        questions = jsonImport.getjsonlarge("src/main/resources/android_questions.json");
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
                idfMap.putIfAbsent(keywords, 0.0);
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
//            System.out.println("Word : "+ word + " TF : " + tf +" Idf " + idf);

            tf = tf / keywords.size();
            idf = 1 + Math.log(data.size() / idf);
            valueMap.replace(key, tf * idf);
            idfMap.replace(key,idf);

//            System.out.println("Word c : "+ word + " TF : " + tf*idf +" Idf " + idf);

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
    public Double getdistance(String qid1, String qid2) {
        Double similiarity = 0.0;
        similiarity = (similiarityMap.containsKey(qid1 + "." + qid2) ? similiarityMap.get(qid1 + "." + qid2)
                : (similiarityMap.containsKey(qid1 + "." + qid2) ? similiarityMap.get(qid2 + "." + qid1) : 0.0));
        Double distance = 2 * Math.acos(similiarity);
        return distance;
    }

    public Double getquerydistance(String qid, String query){
                
        Double distance = 0.0;
        Preprocess p = new Preprocess();
        List<String> common_words = new ArrayList<String>(p.keywords(questions.get(qid).get("body").toString()));
        Double psum = 0.0, i_sum_1 = 0.0, i_sum_2 = 0.0;
        List<String> q1_words = new ArrayList<String>(p.keywords(questions.get(qid).get("body").toString()));
        List<String> q2_words = new ArrayList<String>(p.keywords(query));

        Double tf_query, idf_query;
        tf_query = 0.0;
        idf_query = 0.0;
        for (String word : q1_words) {
            i_sum_1 = i_sum_1 + Math.pow(valueMap.get(qid + "." + word), 2);
        }
        for (String word : q2_words) {
            
            tf_query = tf_query + Collections.frequency(q2_words, word);
            tf_query = tf_query / q2_words.size();

            idf_query = idfMap.get(word);
            i_sum_2 = i_sum_2 + Math.pow(tf_query*idf_query, 2);
        }
        common_words.retainAll(q2_words);
        // System.out.println(q1 + " : " + q2 +" : " +common_words);

        for (String common_word : common_words) {
            tf_query = tf_query + Collections.frequency(q2_words, common_word);
            tf_query = tf_query / q2_words.size();

            idf_query = idfMap.get(common_word);
            psum = psum + valueMap.get(qid + "." + common_word) *(tf_query*idf_query);
            // System.out.println(q1+"."+q2 + " : "+ common_word + " : " + valueMap.get(q1 +
            // "." + common_word) +" : " +valueMap.get(q2 + "." + common_word));
        }
        i_sum_1 = Math.sqrt(i_sum_1);
        i_sum_2 = Math.sqrt(i_sum_2);

        distance = (i_sum_1 * i_sum_2 > 0) ? 2*Math.acos(psum / (i_sum_1 * i_sum_2)) : 2*Math.acos(0.0);
        return distance;
    }
}