
package packages.textprocess;

import java.awt.Container;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.*;

/**
 * tfidf
 */
public class Tfidf {

    Map<String, Double> valueMap;
    Map<String, Double> idfMap;
    Map<String, Double> similiarityMap;
    Map<String, List<String>> data;
    Map<String,Pair<String,Integer>> ifrequency;
    Map<String,Integer> indexMap;
    JsonNode questions;
    Double similiarityMatrix[][];
    List<String> processed;

    public static void main(String[] args) throws Exception {
        Tfidf a = new Tfidf();
    }

    public Tfidf() throws Exception {
        data = new HashMap<String, List<String>>();
        valueMap = new HashMap<String, Double>();
        ifrequency = new HashMap<String,Pair<String,Integer>>();
        idfMap = new HashMap<String, Double>();
        indexMap = new HashMap<String,Integer>();
        similiarityMap = new HashMap<String, Double>();
        Preprocess p = new Preprocess();
        questions = jsonImport.getjsonlarge("src/main/resources/android/android_questions.json");
        processed = new LinkedList<String>();
        Iterator<String> nodeIterator = questions.fieldNames();
        int count =0;
        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();
            //System.out.println(count+" . "+key);
            processed = p.keywords(questions.get(key).get("body").toString());
            data.put(key, processed);
            indexMap.put(key, count);
            count++;

        }

        similiarityMatrix = new Double[data.size()][data.size()];
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

    void initialize() throws Exception {
        for (String qid : data.keySet()) {
            for (String keywords : data.get(qid)) {
                valueMap.putIfAbsent(qid + "." + keywords, 0.0);
                idfMap.putIfAbsent(keywords, 0.0);
                if(ifrequency.containsKey(keywords)){
                    Pair<String,Integer> lastAdded = ifrequency.get(keywords);
                    if(lastAdded.getLeft()!=qid){
                        Pair<String,Integer> newAdded = Pair.of(qid, lastAdded.getRight()+1);
                        ifrequency.replace(keywords,newAdded);
                    }
                }
                else{
                    ifrequency.put(keywords, Pair.of(qid, 1));                    
                }
            }
        }
<<<<<<< Updated upstream
        //System.out.println(valueMap.size() + "\n " + data.size());
=======
        //System.out.println(valueMap.size() + "\n " + ifrequency);
>>>>>>> Stashed changes

        assignTfIdf();
    }

    void assignTfIdf() throws Exception{
        Double tf, idf,count=0.0;
        for (String key : valueMap.keySet()) {
            StringTokenizer token = new StringTokenizer(key, ".");
            String qid = token.nextToken();
            String word = token.nextToken();
            tf = 0.0;
            idf = 0.0;
            List<String> keywords = data.get(qid);
            tf = tf + Collections.frequency(keywords, word);

            idf = idf + ifrequency.get(word).getRight();
            count++;
            //System.out.println(count +  " : "  + key);

            // System.out.println("Word : "+ word + " TF : " + tf +" Idf " + idf);

            tf = tf / keywords.size();
            idf = 1 + Math.log(data.size() / idf);
            // System.out.println("Word : "+ word + " TF : " + tf +" Idf " + idf);

            valueMap.replace(key, tf * idf);
            idfMap.replace(word, idf);

            // System.out.println("Word c : "+ word + " TF : " + tf*idf +" Idf " + idf);

        }
        //System.out.println("Assigned");

        generateSimiliarityMap();
    }


    public Double similiarity(String q1, String q2) {
        List<String> common_words = new LinkedList<String>(data.get(q1));
        Double psum = 0.0, i_sum_1 = 0.0, i_sum_2 = 0.0;
        List<String> q1_words = new LinkedList<String>(data.get(q1));
        List<String> q2_words = new LinkedList<String>(data.get(q2));

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

        return (Double) ((i_sum_1 * i_sum_2 > 0) ? psum / (i_sum_1 * i_sum_2) : 0.0f);
    }

    void generateSimiliarityMap() throws Exception {
        int count=0;
        for (Object qid1 : data.keySet()) {
            
            for (Object qid2 : data.keySet()) {
<<<<<<< Updated upstream
                // if (!similiarityMap.containsKey(qid1 + "." + qid2) && !similiarityMap.containsKey(qid2 + "." + qid1)){
                //     count++;
                //     similiarityMap.put(qid1 + "." + qid2, similiarity((String) qid1, (String) qid2));
                //     System.out.println(count+" Adding Similiarity : \n \t" + qid1 +" : "+qid2);
                // }
                similiarityMatrix[indexMap.get(qid1)][indexMap.get(qid2)] = (similiarity((String) qid1, (String) qid2));
=======
                if (!similiarityMap.containsKey(qid1 + "." + qid2) && !similiarityMap.containsKey(qid2 + "." + qid1)){
                    count++;
                    similiarityMap.put(qid1 + "." + qid2, similiarity((String) qid1, (String) qid2));
                    //System.out.println(count+" Adding Similiarity : \n \t" + qid1 +" : "+qid2);
                }
>>>>>>> Stashed changes
            }
        }
        Properties properties = new Properties();

for (Map.Entry<String,Double> entry : similiarityMap.entrySet()) {
    properties.put(entry.getKey(), ""+ entry.getValue()+"");
}

    properties.store(new FileOutputStream("./src/main/resources/offlineResults/similiarityMap.properties"), null);
        //System.out.println("Similiarity Map Generated : \n \t" + similiarityMap);
        // System.out.println("Idf Map Generated : \n \t" + idfMap);

    }

    public Double getSimiliarity(String qid1, String qid2) {
        Double similiarity = 0.0;
        // similiarity = (similiarityMap.containsKey(qid1 + "." + qid2) ? similiarityMap.get(qid1 + "." + qid2)
        //         : (similiarityMap.containsKey(qid2 + "." + qid1) ? similiarityMap.get(qid2 + "." + qid1) : 0.0));
        //
        similiarity = (indexMap.containsKey(qid1)&&indexMap.containsKey(qid2) ?  similiarityMatrix[indexMap.get(qid1)][indexMap.get(qid2)] : 0.0);

        
        similiarity = similiarity > 1 ? 1 : similiarity;
        return similiarity;
    }

    public Double getdistance(String qid1, String qid2) {
        Double similiarity = 0.0;
        // if (similiarityMap.containsKey(qid1 + "." + qid2))
        //     similiarity = similiarityMap.get(qid1 + "." + qid2);
        // else if (similiarityMap.containsKey(qid2 + "." + qid1))
        //     similiarity = similiarityMap.get(qid2 + "." + qid1);

        similiarity = (indexMap.containsKey(qid1)&&indexMap.containsKey(qid2) ?  similiarityMatrix[indexMap.get(qid1)][indexMap.get(qid2)] : 0.0);


        //System.out.println(" Similiarity " + qid1 + " : " + qid2 + " : " + similiarity);
        similiarity = similiarity > 1 ? 1 : similiarity;
        return 2 * Math.acos(similiarity);
    }

    public Double getquerydistance(String qid, String query) {

        Double distance = 0.0;
        Preprocess p = new Preprocess();
        List<String> common_words = new LinkedList<String>(p.keywords(questions.get(qid).get("body").toString()));
        Double psum = 0.0, i_sum_1 = 0.0, i_sum_2 = 0.0;
        List<String> q1_words = new LinkedList<String>(p.keywords(questions.get(qid).get("body").toString()));
        List<String> q2_words = new LinkedList<String>(p.keywords(query));

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
            i_sum_2 = i_sum_2 + Math.pow(tf_query * idf_query, 2);
        }

        common_words.retainAll(q2_words);

        for (String common_word : common_words) {
            tf_query = tf_query + Collections.frequency(q2_words, common_word);
            tf_query = tf_query / q2_words.size();

            idf_query = idfMap.get(common_word);
            psum = psum + (valueMap.get(qid + "." + common_word) * (tf_query * idf_query));
            // System.out.println(q1+"."+q2 + " : "+ common_word + " : " + valueMap.get(q1 +
            // "." + common_word) +" : " +valueMap.get(q2 + "." + common_word));
        }
        i_sum_1 = Math.sqrt(i_sum_1);
        i_sum_2 = Math.sqrt(i_sum_2);

        distance = (i_sum_1 * i_sum_2 > 0) ? 2 * Math.acos(psum / (i_sum_1 * i_sum_2)) : 2 * Math.acos(0.0);
        return distance;
    }
}