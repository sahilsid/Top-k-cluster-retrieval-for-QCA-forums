package packages.userinteraction;

import packages.textprocess.*;
import javafx.util.Pair;
import java.util.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

public class Mapping {
    JsonNode questions = jsonImport.getjsonlarge("src/main/resources/android/android_questions.json");
    JsonNode answers = jsonImport.getjsonlarge("src/main/resources/android/android_answers.json");
    JsonNode users = jsonImport.getjsonlarge("src/main/resources/android/android_users.json");
    // JSONObject questions =
    // jsonImport.getjson("src/main/resources/android_questions.json");
    // JSONObject answers =
    // jsonImport.getjson("src/main/resources/android_answers.json");
    // JSONObject users =
    // jsonImport.getjson("src/main/resources/android/android_users.json");
    public Map<String, String> AidQid;
    public Map<String, String> QidUid;
    public Map<String, List<String>> UidQid;
    public Map<String, List<String>> UidAid;
    public Map<String, List<String>> UidQaid;
    public Map<String, List<String>> UidUid;
    public Map<String, Integer> UidUid_commonQs;
    public Map<String, Integer> UidUid_commonQs_sorted;
    Map<String, Integer> indexMap;
    SocialGraph socialGraph;
    String qn;
    List<String> qn1;
    List<String> ans1;

    public class SocialGraph {
        int vertices;
        int edges;
        List<Pair<Integer, Integer>> adjacencyList[];

        public SocialGraph(int v) {
            this.vertices = v;
            this.edges=0;
            adjacencyList = new LinkedList[v];
            for (int i = 0; i < v; i++) {
                this.adjacencyList[i] = new LinkedList<Pair<Integer, Integer>>();
            }
        }

        public void addEdge(int source, int destination, int weight) {
            edges++;
            System.out.println(edges+". Adding edge : "+source+"---->"+destination+"\tWeight : "+weight);
            adjacencyList[source].add(new Pair<Integer, Integer>(destination, weight));
            adjacencyList[destination].add(new Pair<Integer, Integer>(source, weight));
        }

        public Double getWeightedDistance(int a, int b) {
            Double WeightedDistance = Double.POSITIVE_INFINITY;
            // DIJKSTRA'S SHORTEST PATH O(ElogV)
            return WeightedDistance;
        }

        public void display(){

            System.out.print(" Vertex     \t Adjacency List\n");
            for(int i = 0;i<this.vertices;i++){
                System.out.print(i+"\t" + adjacencyList[i]);
            }
        }
    }

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
        this.mapAnsQn();
        this.mapUidQn();
        this.mapUidAns();
        this.mapUidQaid();
        this.mapUidUid();

    }

    public void mapAnsQn() {
        AidQid = new HashMap<String, String>();
        Iterator<String> nodeIterator = answers.fieldNames();
        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();
            AidQid.put(key, answers.get(key).get("parentid").asText());
        }
        System.out.println("Aid - Qid Generated");

    }

    public void mapQidUid() {
        QidUid = new HashMap<String, String>();
        Iterator<String> nodeIterator = questions.fieldNames();
        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();
            QidUid.put(key, questions.get(key).get("userid").asText());
        }
        System.out.println("Qid - Uid Generated");

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
        int count = 0;
        Iterator<String> nodeIterator = users.fieldNames();

        while (nodeIterator.hasNext()) {
            List<String> answerids = new LinkedList<String>();
            String key = nodeIterator.next();// userid
            Iterator<JsonNode> arrayiIterator = users.get(key).get("answers").elements();
            while (arrayiIterator.hasNext()) {
                String val = arrayiIterator.next().asText();
                answerids.add(val);
            }
            UidAid.put(key, new LinkedList<String>(answerids));

        }
        System.out.println(UidAid.size());
        System.out.println("Uid - Aid Generated");

    }

    public void mapUidQaid() {
        UidQaid = new HashMap<String, List<String>>();
        List<String> ans_list = new LinkedList<String>();
        ;
        List<String> qid_list = new LinkedList<String>();
        for (Object uid : UidAid.keySet()) {
            // System.out.println(uid); //entire hash map
            // System.out.println(UidAid.get(uid));// answer id's by a user
            ans_list = UidAid.get(uid);
            if (!ans_list.isEmpty()) { // answers of this user
                for (Object aid : ans_list) {
                    qid_list.add(AidQid.get(aid));
                }
                // System.out.println("user : "+ uid+" : " + qid_list);

                UidQaid.put((String) uid, new LinkedList<String>(qid_list));
                // System.out.println("QAID"+UidQaid.get(uid));

                qid_list.clear();
            }
        }
        System.out.println("Uid - Qaid Generated ");
    }

    public void mapUidUid() {
        UidUid = new HashMap<String, List<String>>();
        UidUid_commonQs = new HashMap<String, Integer>();
        UidUid_commonQs_sorted = new HashMap<String, Integer>();
        List<String> interact_Qid = new LinkedList<String>();
        int count_common_ques = 0;
        int finished = 0;
        for (Object uid1 : UidQid.keySet()) {
            for (Object uid2 : UidQid.keySet()) {
                if (uid1 != uid2 && (!UidUid_commonQs.containsKey(uid2 + "." + uid1))
                        && (!UidUid_commonQs.containsKey(uid1 + "." + uid2))) {
                    List<String> l1 = UidQid.get(uid1);// qid's
                    List<String> l2 = UidQid.get(uid2);

                    if (UidQaid.containsKey(uid1)) {
                        List<String> ql1 = UidQaid.get(uid1);// Qaid
                        List<String> temp2 = new LinkedList<String>(l2);
                        temp2.retainAll(ql1);

                        if (temp2.size() > 0) {
                            // System.out.println("COMMON QAID OF USER2"+temp2);

                            interact_Qid.add((String) uid2);
                            count_common_ques = count_common_ques + temp2.size();
                        }
                        temp2=null;
                    }
                    if (UidQaid.containsKey(uid2)) {

                        List<String> ql2 = UidQaid.get(uid2);
                        List<String> temp = new LinkedList<String>(l1);
                        // temp.addAll(l1);// if we use l1, it will be modified

                        temp.retainAll(ql2);

                        if (temp.size() > 0) {
                            // System.out.println("COMMON QAID OF USER2"+temp);

                            if (!interact_Qid.contains(uid2)) {
                                interact_Qid.add((String) uid2);
                            }
                            count_common_ques = count_common_ques + temp.size();
                        }
                        temp=null;
                        System.gc();
                    }
                    // if(UidQaid.containsKey(uid1)||UidQaid.containsKey(uid2))
                    // {
                    // interact_Qid.add((String)uid2+"."+count_common_ques);
                    // }
                    if (count_common_ques > 0) {
                        // UidUid_commonQs.put(((String) uid1 + "." + (String) uid2),
                        // count_common_ques);
                        //System.out.println(UidUid_commonQs.get((String)uid1+"."+(String)uid2));
                        socialGraph.addEdge(indexMap.get(uid1), indexMap.get(uid2), count_common_ques);
                    }
                    System.out.println("Completed" + finished);

                }
                //System.out.println("Max:"+Runtime.getRuntime().maxMemory()+" Tot : "+Runtime.getRuntime().totalMemory()+" Free : "+ Runtime.getRuntime().freeMemory());
                finished++;
            }

            // UidUid.put((String) uid1, new LinkedList<String>(interact_Qid));
            // System.out.println("uid-uid"+UidUid.get(uid1));

            interact_Qid.clear();
            count_common_ques = 0;
        }
        System.out.println("Social Graph Generated");
        socialGraph.display();
    }

    public Double getSocialDistance(String user1, String user2) {
        // return 2 * Math.acos(UidUid_commonQs_sorted.containsKey(user1 + "." + user2)
        // ? UidUid_commonQs_sorted.get(user1 + "." + user2)
        // : UidUid_commonQs_sorted.containsKey(user2 + "." + user1)
        // ? UidUid_commonQs_sorted.get(user2 + "." + user1)
        // : 0.0);
        return 2 * Math.acos(socialGraph.getWeightedDistance(indexMap.get(user1), indexMap.get(user2)));
    }

    public void sort_UidUid_commonQs() {

        List<Integer> sorted_value_list = new LinkedList<Integer>(UidUid_commonQs.values());
        Collections.sort(sorted_value_list);
        for (Object val : sorted_value_list) {
            for (Object key : UidUid_commonQs.keySet()) {
                if (UidUid_commonQs.get(key) == val) {
                    UidUid_commonQs_sorted.put((String) key, (Integer) val);
                }
            }

        }
        System.out.println(UidUid_commonQs_sorted);
    }

}
