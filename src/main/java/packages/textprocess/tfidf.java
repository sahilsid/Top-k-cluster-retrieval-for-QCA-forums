
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
        System.out.println("data : "+data);
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
            System.out.println(qid + " and " + word);
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
}