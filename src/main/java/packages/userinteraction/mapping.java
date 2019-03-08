package packages.userinteraction;
import packages.textprocess.*;
import java.util.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
public class mapping{
    JsonNode questions = jsonImport.getjsonlarge("src/main/resources/android/android_questions.json");
    JsonNode answers = jsonImport.getjsonlarge("src/main/resources/android/android_answers.json");
    JsonNode users = jsonImport.getjsonlarge("src/main/resources/android/android_users.json");
    // JSONObject questions = jsonImport.getjson("src/main/resources/android_questions.json");
    // JSONObject answers = jsonImport.getjson("src/main/resources/android_answers.json");
    // JSONObject users = jsonImport.getjson("src/main/resources/android/android_users.json");
     Map<String, String> AidQid;
    Map<String, List<String>>UidQid;
    Map<String, List<String>>UidAid;
    Map<String, List<String>>UidQaid;
    Map<String, List<String>>UidUid;
    Map<String, Integer>UidUid_commonQs; 
    Map<String, Integer>UidUid_commonQs_sorted;
    String qn;
    List<String> qn1;
    List<String>ans1;
    public static void main(String[] args)throws Exception {
        mapping m =new mapping();
        m.mapAnsQn();
        m.mapUidQn();
        m.mapUidAns();
        m.mapUidQaid();
        m.mapUidUid();
        m.sort_UidUid_commonQs();
    }
    public mapping() throws Exception{

    }  

       void mapAnsQn(){
            AidQid = new HashMap<String, String>();
            Iterator<String> nodeIterator = answers.fieldNames();
            while (nodeIterator.hasNext()) {
                String key = nodeIterator.next();
                AidQid.put(key, answers.get(key).get("parentid").toString());
            }
            // for (Object aid : answers.keySet()) {
            //     JSONObject value = (JSONObject) answers.get(aid);
            //     qn= (String)value.get("parentid");
            //     AidQid.put((String)aid, qn);
            // }
           // System.out.println(AidQid);

        }
        void mapUidQn(){
            UidQid = new HashMap<String, List<String>>();
            Iterator<String> nodeIterator = users.fieldNames();
            // for (Object uid : users.keySet()) {
            //     JSONObject value2 = (JSONObject) users.get(uid);
            //     qn1= (List<String>)value2.get("questions");
            //     if (!qn1.isEmpty()){
            //         UidQid.put((String)uid, qn1);
            //     }
            // }
            while (nodeIterator.hasNext()) {
                List<String> questionids = new LinkedList<String>();
                String key = nodeIterator.next();//userid
                Iterator<JsonNode> arrayiIterator = users.get(key).get("questions").elements();
                while(arrayiIterator.hasNext()){
                String val = arrayiIterator.next().asText();
                questionids.add(val);
                }
                UidQid.put(key, new LinkedList<String>( questionids));
                System.out.println(UidQid.get(key)); 
            }
            //System.out.println(UidQid);
        }
        void mapUidAns(){
            UidAid = new HashMap<String, List<String>>();
            Iterator<String> nodeIterator = users.fieldNames();
            // for (Object uid : users.keySet()) {
            //     JSONObject value3 = (JSONObject) users.get(uid);
            //     ans1= (List<String>)value3.get("answers");
            //     if (!ans1.isEmpty()){
            //         UidAid.put((String)uid, ans1);
            //     }
            // }
            while (nodeIterator.hasNext()) {
                List<String> answerids = new LinkedList<String>();
                String key = nodeIterator.next();//userid
                Iterator<JsonNode> arrayiIterator = users.get(key).get("answers").elements();
                while(arrayiIterator.hasNext()){
                String val = arrayiIterator.next().asText();
                answerids.add(val);
                }
                UidAid.put(key, new LinkedList<String>(answerids));
                System.out.println(UidAid.get(key)); 

            }
            //System.out.println(UidAid);
      
        }
        void mapUidQaid(){
            UidQaid = new HashMap<String, List<String>>();
            List<String> ans_list=new LinkedList<String>();;
            List<String> qid_list = new LinkedList<String>();
            for (Object uid : UidAid.keySet()) {
                //System.out.println(uid); //entire hash map
                //System.out.println(UidAid.get(uid));// answer id's by a user
                ans_list = UidAid.get(uid);
                if (!ans_list.isEmpty()){ // answers of this user
                    for (Object aid: ans_list){
                        qid_list.add(AidQid.get(aid));
                    }   
                    // System.out.println("user : "+ uid+" : "  + qid_list);

                    UidQaid.put((String)uid, new LinkedList<String>(qid_list));
                    System.out.println("QAID"+UidQaid.get(uid)); 

                    qid_list.clear();
                }  
            }
            //System.out.println(UidQaid);
        }
        void mapUidUid(){
            UidUid = new HashMap<String, List<String>>();
            UidUid_commonQs = new HashMap<String, Integer>();
            UidUid_commonQs_sorted=new HashMap<String, Integer>();
            List<String> interact_Qid = new LinkedList<String>();
            int count_common_ques=0;
            
            for (Object uid1 : UidQid.keySet()) {
                for (Object uid2 : UidQid.keySet()) {
                    if (uid1 != uid2 && (!UidUid_commonQs.containsKey(uid2+"."+uid1)) && (!UidUid_commonQs.containsKey(uid1+"."+uid2))){
                        LinkedList<String> l1 = new LinkedList<String>(UidQid.get(uid1));//qid's 
                        LinkedList<String> l2 = new LinkedList<String>(UidQid.get(uid2));
                         System.out.println("QAID OF USER2"+UidQaid.get(uid2));
                        if(UidQaid.containsKey(uid1)){
                            LinkedList<String> ql1 = new LinkedList<String>(UidQaid.get(uid1));//Qaid
                            LinkedList<String> temp2 = new LinkedList<String>(l2);
                            temp2.retainAll(ql1);
                            System.out.println("COMMON QAID OF USER2"+temp2);

                            if(temp2.size()>0)
                             {
                                interact_Qid.add((String)uid2);
                                count_common_ques=count_common_ques+temp2.size();
                             }       
                        }
                        if(UidQaid.containsKey(uid2)){
                            LinkedList<String> ql2 = new LinkedList<String>(UidQaid.get(uid2));
                            LinkedList<String> temp = new LinkedList<String>(l1);
                            //temp.addAll(l1);// if we use l1, it will be modified
                            temp.retainAll(ql2);
                            if(temp.size()>0){
                                if(!interact_Qid.contains(uid2))
                                {
                                   interact_Qid.add((String)uid2);
                                }
                                count_common_ques=count_common_ques+temp.size();
                            }
                            
                        }
                        //if(UidQaid.containsKey(uid1)||UidQaid.containsKey(uid2))
                            //{
                             //   interact_Qid.add((String)uid2+"."+count_common_ques);
                            //}
                        if (count_common_ques>0){
                            UidUid_commonQs.put(((String)uid1+"."+(String)uid2),count_common_ques); 
                            System.out.println(UidUid_commonQs.get((String)uid1+"."+(String)uid2)); 

                        }
                    }   
                } 
                UidUid.put((String)uid1, new LinkedList<String>(interact_Qid));
                System.out.println("uid-uid"+UidUid.get(uid1));

                interact_Qid .clear();
                count_common_ques=0;       
            }
            System.out.println("uid-uid"+UidUid);
            //System.out.println(UidUid_commonQs);
        }
        void sort_UidUid_commonQs(){
            
            List<Integer>sorted_value_list= new LinkedList<Integer>( UidUid_commonQs.values());
            Collections.sort(sorted_value_list);
            for (Object val : sorted_value_list) {
                for(Object key : UidUid_commonQs.keySet()){
                    if (UidUid_commonQs.get(key)==val){
                        UidUid_commonQs_sorted.put((String)key, (Integer)val);
                    }
                }

            }
            //System.out.println(UidUid_commonQs_sorted);
        }

    
    
}
