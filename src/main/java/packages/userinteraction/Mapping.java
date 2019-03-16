package packages.userinteraction;

import packages.textprocess.*;
import java.util.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapping {
    JsonNode questions = jsonImport.getjsonlarge("src/main/resources/android/android_questions.json");
    JsonNode answers = jsonImport.getjsonlarge("src/main/resources/android/android_answers.json");
    JsonNode users = jsonImport.getjsonlarge("src/main/resources/android/android_users.json");

//    public Map<String, String> AidQid;
    public Map<String, String> QidUid;
    public Map<String, List<String>> UidQid;
    public Map<String, List<String>> QidAid;
    public Map<String, String> AidUid;
    public Map<String, List<String>> UidAid;
    Map<String, Integer> indexMap;
    SocialGraph socialGraph;
  

    public static void main(String[] args) throws Exception {
        Mapping m = new Mapping();
    }

    public Mapping() throws Exception {
        Iterator<String> nodeIterator = users.fieldNames();
        Integer count = 0;
        indexMap = new HashMap<String, Integer>();
        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();// userid
            indexMap.put(key, count);
            count++;
        }
        System.out.println(" Users : " + indexMap.size());
        socialGraph = new SocialGraph(indexMap.size());
//        this.mapAnsQn();
//        this.mapUidQn();
        this.mapUidAns();
        this.mapQidUid();
        generateSocialGraph();
    }

    // public void mapAnsQn() {
    //     AidQid = new HashMap<String, String>();
    //     Iterator<String> nodeIterator = answers.fieldNames();
    //     while (nodeIterator.hasNext()) {
    //         String key = nodeIterator.next();
    //         AidQid.put(key, answers.get(key).get("parentid").asText());
    //     }
    //     System.out.println("Aid - Qid Generated" + AidQid.size());

    // }

    public void mapQidUid() {
        QidUid = new HashMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        QidAid = new HashMap<String, List<String>>();
        Iterator<String> nodeIterator = questions.fieldNames();
        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();
            QidUid.put(key, questions.get(key).get("userid").asText());
            QidAid.put(key, mapper.convertValue(questions.get(key).get("answers"), ArrayList.class));
        }
        System.out.println("Qid - Uid Generated" + QidUid.size());

    }

    public void mapUidQn() {
        UidQid = new HashMap<String, List<String>>();
        Iterator<String> nodeIterator = users.fieldNames();

        while (nodeIterator.hasNext()) {
            List<String> questionids = new LinkedList<String>();
            String key = nodeIterator.next();// userid
            Iterator<JsonNode> arrayiIterator = users.get(key).get("questions").elements();
            while (arrayiIterator.hasNext()) {
                String val = arrayiIterator.next().asText();
                questionids.add(val);
            }
            UidQid.put(key, new LinkedList<String>(questionids));
            // System.out.println(UidQid.get(key));
        }
        System.out.println("Uid - Qid Generated");
    }

    public void mapUidAns() {
        UidAid = new HashMap<String, List<String>>();
        AidUid = new HashMap<String, String>();
        int count = 0;
        Iterator<String> nodeIterator = users.fieldNames();

        while (nodeIterator.hasNext()) {
            List<String> answerids = new LinkedList<String>();
            String key = nodeIterator.next();// userid
            Iterator<JsonNode> arrayiIterator = users.get(key).get("answers").elements();
            while (arrayiIterator.hasNext()) {
                String val = arrayiIterator.next().asText();
                answerids.add(val);
                AidUid.put(val, key);
            }
            UidAid.put(key, new LinkedList<String>(answerids));

        }
        System.out.println("Uid - Aid Generated. \t Size : "+ UidAid.size() );

    }

    // public void mapUidQaid() {
    //     UidQaid = new HashMap<String, List<String>>();
    //     List<String> ans_list = new LinkedList<String>();
    //     ;
    //     List<String> qid_list = new LinkedList<String>();
    //     for (Object uid : UidAid.keySet()) {
    //         // System.out.println(uid); //entire hash map
    //         // System.out.println(UidAid.get(uid));// answer id's by a user
    //         ans_list = UidAid.get(uid);
    //         if (!ans_list.isEmpty()) { // answers of this user
    //             for (Object aid : ans_list) {
    //                 qid_list.add(AidQid.get(aid));
    //             }
    //             // System.out.println("user : "+ uid+" : " + qid_list);

    //             UidQaid.put((String) uid, new LinkedList<String>(qid_list));
    //             // System.out.println("QAID"+UidQaid.get(uid));

    //             qid_list.clear();
    //         }
    //     }
    //     System.out.println("Uid - Qaid Generated  \t Size : " + UidQaid.size());

    // }

    public void generateSocialGraph() {
        for (String question : QidAid.keySet()) {
            for (String answer : QidAid.get(question)) {
               
                if (indexMap.get(QidUid.get(question)) != null && indexMap.get(AidUid.get(answer)) != null)
                    socialGraph.addEdge(indexMap.get(QidUid.get(question)), indexMap.get(AidUid.get(answer)));
            }
        }
        socialGraph.display();
    }

    public Integer getInteractionLevel(String user1, String user2) {
        return socialGraph.getWeight(indexMap.get(user1), indexMap.get(user2));
    }

    public Double getSocialDistance(String user1, String user2) {
        return 2 * Math.acos(socialGraph.getWeightedDistance(indexMap.get(user1), indexMap.get(user2)));
    }
}
