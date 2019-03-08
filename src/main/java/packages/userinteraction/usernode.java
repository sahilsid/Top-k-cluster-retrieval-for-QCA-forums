package packages.userinteraction;
import packages.textprocess.*;
import java.util.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;
public class usernode{
    JSONObject user_info;
    JSONObject ques_info;
    Map<String, Double> connected_users_weight;
    String user_id;
    Map<List<String>, List<String>> qid_aid;
    public static void main(String[] args) {
      usernode u= new usernode();
      u.initialize();
    }
    // public usernode(String id) {
    //     this.user_id = id;
    //     connected_users_weight = new HashMap<String, Double>();
    // }

    // void generateConnections() {
    //     questions = jsonImport.getjson("src/main/resources/android_questions.json");
    //      for(Object key1 : questions.keySet()){
    //         Double count_common_ques=0.0;
    //         for(Object key2 : questions.keySet()){
    //             String k1 = (String) key1;
    //             String k2 = (String) key2;
    //             JSONObject value1 = (JSONObject) questions.get(key1);
    //             JSONObject value2 = (JSONObject) questions.get(key2);
    //             //System.out.print(" qid "+key+"  userid  "+value.get("userid"));
    //             String user_id_1=((String)value1.get("userid"));
    //             String user_id_2=((String)value2.get("userid"));
    //             if(user_id_1==user_id_2){
    //                 count_common_ques++;
    //             }
    //         drawEdge(user_id_1,user_id_2,count_common_ques);
    //         }
            
    //      }
    // }

    // void drawEdge(String u1, String u2, Double count_common_ques) {//here
    //     if(count_common_ques){

            
    //     }


    // }
    // void initialize() {
    //       List<String> User_id_list = new ArrayList<String>();
    //       for(Object key : questions.keySet()){
    //         String k = (String) key;
    //         JSONObject value = (JSONObject) questions.get(key);
    //         //System.out.print(" qid "+key+"  userid  "+value.get("userid"));
    //         String user_id=((String)value.get("userid"));
    //         if (!User_id_list.contains(user_id)){
    //             usernode u=new usernode(user_id);
    //             User_id_list.add(user_id);
    //         } 
    //       }
    //     }
    void initialize() {
          user_info = jsonImport.getjson("src/main/resources/android/android_users.json");
          ques_info = jsonImport.getjson("src/main/resources/android_questions.json");
          qid_aid = new HashMap<List<String>, List<String>>();
          List<String> User_id_list = new ArrayList<String>();
          List<String> answers= new ArrayList<String>();
          List<String> questions=new ArrayList<String>();
          List<String> answers2 = new ArrayList<String>();
          List<String> questions2=new ArrayList<String>();
          List<String> ans= new ArrayList<String>();
          String userid;
          for(Object uid : user_info .keySet())
          {
            JSONObject value = (JSONObject) user_info.get(uid);
            //System.out.print("uid"+uid);
            //answers=(List<String>)value.get("answers");
            questions=(List<String>)value.get("questions");
            
            // for (Object aid : answers){
            //     for (Object qid : ques_info .keySet()){
            //         JSONObject value2 = (JSONObject) ques_info.get(qid);
            //         ans=(List<String>) value2.get("answers");
            //         System.out.print("answers"+ans+".............");
            //         //questions1=(List<String>)value2.get("questions1");
            //         //answers1=(JSONObject)value2.get("answers1");
            //         //if(questions1.containsAll(answers) && answers.containsAll(questions1)){
            //         if(ans.contains(aid) ){
            //              //System.out.print("answers"+ans);
            //             //drawEdge(userid,uid);
            //             //qid_aid.put((List<String>)questions, (List<String>)answers);
            //         }
                
            //     }
    
            // }
            for (Object qid : questions){//questions asked by user1
              for (Object qid2 : ques_info .keySet()){//all qn databasse, find these qns there
                if (qid==qid2){
                  JSONObject value2 = (JSONObject) ques_info.get(qid2);
                  ans=(List<String>) value2.get("answers");//get answers of qns asked by user1
                  System.out.print("answers"+ans+".............");
                  for(Object uid2 : user_info .keySet())//check users of those answers
                    {
                       JSONObject val = (JSONObject) user_info.get(uid2);
                       answers2=(List<String>)val.get("answers");
                    }
                  
                  
                  
                  if(ans.contains(aid){
                       //System.out.print("answers"+ans);
                      //drawEdge(userid,uid);
                      //qid_aid.put((List<String>)questions, (List<String>)answers);
                  }
              
              }
  
          }
            

            
          }



           
                

            
     }
          //System.out.print(qid_aid);
          
          
        
    
}
