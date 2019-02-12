
package packages.textprocess;

import java.util.*;
import java.lang.*;
import javax.lang.model.element.VariableElement;

import org.json.simple.JSONObject;

/**
 * tfidf
 */
public class tfidf {
public static void main(String[] args) {
    Preprocess p = new Preprocess();
        JSONObject jsonfile = jsonImport.getjson("src/main/resources/android_questions.json");
        List<String> newList = new ArrayList<String>();
        List<String> list_keys = new ArrayList<String>();
        List<Map> termsfreq = new ArrayList<Map>();
        //Dictionary tf= new Hashtable();
        int total_Questions=0;
    for(Object key : jsonfile.keySet()){
        //System.out.println(key);
        total_Questions++;
        list_keys.add((String)key);
        JSONObject value = (JSONObject) jsonfile.get(key);
        
        List<String> keywords = p.keywords((String) value.get("body"));
        //System.out.println(keywords);
        newList.addAll(keywords);//all keywords of this qn  
        int Keyword_list_size = keywords.size(); // no keywords in this qn
        Map<String,Double> Term_frequency = new HashMap();
        Map temp= new HashMap();
        for(String x:keywords){//tf cal
    
            if(!Term_frequency .containsKey(x)){
                //Term_frequency .put(x,(double)(1/Keyword_list_size));
                Term_frequency .put(x,(double)(1));
            }else{
                //Term_frequency .put(x, (double)((Term_frequency .get(x)+1)/Keyword_list_size));
                Term_frequency .put(x, (double)(Term_frequency .get(x)+1));
            }
            //tf.put(x,Term_frequency.get(x) );
            temp.put(x,((Term_frequency.get(x))/Keyword_list_size));
            //System.out.println(tf);
        }
        
        //System.out.println(Term_frequency);
        termsfreq.add(temp);// list of dictionaries for tf's of each qn
        //System.out.println(tf);

    }
    //System.out.println(newList);
    //System.out.println(termsfreq);
    
    HashSet<String> uniqueNewList = new HashSet<>(newList);
  

    //computing idf
    Dictionary idf= new Hashtable();
    for (String word: uniqueNewList){//every word
        int count_no_ques=0;
        double idf_value=0;
        for(Object key : jsonfile.keySet()){//every qn
            //System.out.println(key);
            JSONObject value = (JSONObject) jsonfile.get(key);
            
            List<String> keywords = p.keywords((String) value.get("body"));
            //System.out.println(keywords);
        
            if (keywords.contains(word)){//in how many qns this word is present
                count_no_ques++;
            }
        }
        //System.out.println(word+"    "+count_no_ques);
        double val = total_Questions/count_no_ques;
        idf_value=Math.log(val);
        idf.put(word, idf_value);
      


    }
    //System.out.println(idf);
    //computing tfidf of keywords
   
    List<Map> tfidf = new ArrayList<Map>();
    double tfidf_val=1.0;
    for(Map dict1:termsfreq){//tf's of each qn
        Map temp= new HashMap();
        //System.out.println(dict1);
        for(Object key: dict1.keySet()){//each word of qn
            tfidf_val=((double)dict1.get(key))*((double)idf.get(key));
            temp.put(key, tfidf_val);

        }
        //System.out.println(temp);
        tfidf.add(new HashMap(temp));
        temp.clear();
    
        
    } 
    //System.out.println(tfidf);
    //Associate tfidfs with the qid's
    int i=0;
    Map Qid_tfidf = new HashMap();
    for(Map dict_x:tfidf){
        //System.out.println(dict_x);
        Qid_tfidf.put(list_keys.get(i), dict_x);//from keylist get qid
        i++;
    }
    //System.out.println(Qid_tfidf);//qid's +their corresponding tfidfs of each keyword
    Map keyi_i= new HashMap();
    Map keyj_j= new HashMap();
    List<String> Cosine_similarity= new ArrayList<String>();
    // Computing cosine similarity between each pair of questions
    for(Object key_i : jsonfile.keySet()){
        //System.out.println(key);
       
        JSONObject value_i = (JSONObject) jsonfile.get(key_i);
        List<String> keywords_i = p.keywords((String) value_i.get("body"));
        //System.out.println(keywords);
        keyi_i=(Map)Qid_tfidf.get(key_i);
        //System.out.println(keyi_i);
        for(Object key_j : jsonfile.keySet()){
            JSONObject value_j = (JSONObject) jsonfile.get(key_j);
            List<String> keywords_j = p.keywords((String) value_j.get("body"));
            List<String> common_keywords = new ArrayList<String>(keywords_i);
            common_keywords.retainAll(keywords_j);
            //System.out.println(common_keywords);
            
            keyj_j=(Map)Qid_tfidf.get(key_j);
            //System.out.println(keyj_j);
            double score=0.0;
            double den_i=0.0;
            double den_j=0.0;
            for(String c_word : common_keywords){
                score= score + (((double)keyi_i.get(c_word))*((double)keyj_j.get(c_word)));
                den_i=den_i+(((double)keyi_i.get(c_word))*((double)keyi_i.get(c_word)));
                den_j=den_j+((double)(keyj_j.get(c_word))*(double)(keyj_j.get(c_word)));
             

            }
            Double cosine_sim;
            if(common_keywords.size()!=0){
                cosine_sim=((score)/(Math.sqrt(den_i)*Math.sqrt(den_j)));
            }
            else{
                cosine_sim=0.0;
            }
            
            System.out.println(cosine_sim);
            //System.out.println(Qid_tfidf.get(key_i));
            common_keywords.clear();
            
           

        }
       
        
        
       

    }

      
}
    
}