
package packages.cluster;

import java.util.*;
import packages.textprocess.*;
import org.json.simple.JSONObject;

/**
 * generate
 */
public class generate {

    int k;
    HashMap<String, String> cluster ; 
    Map<String, List<String>> data;
    List<String> centroids;
    public static void main(String[] args) {
        generate a = new generate(3);
        a.displaydata();
        a.initialize();
    }

    generate(int a) {
        k = a;
        cluster= new HashMap<>();
        data = new HashMap<String, List<String>>();
        Preprocess p = new Preprocess();
        JSONObject questions = jsonImport.getjson("src/main/resources/android_questions.json");
        List<String> processed = new ArrayList<String>();

        for (Object key : questions.keySet()) {
            String k = (String) key;
            JSONObject value = (JSONObject) questions.get(key);
            processed = p.keywords((String) value.get("body"));

            data.put(k, processed);
        }
    }


    public void initialize() {
        centroids = new ArrayList<String>();
        Object[] keys = data.keySet().toArray();
        for (int i = 0; i < this.k; i++) {
            Object randomkey = keys[new Random().nextInt(keys.length)];
            while(centroids.size()>0 && centroids.contains((String)randomkey)){
                randomkey = keys[new Random().nextInt(keys.length)];
            }
            centroids.add((String) randomkey);
        }
        System.out.println("Random Keys" + centroids);

        int distance,mindist=-1;
        String nearestCentroid = "";
        for(String qid : data.keySet()){
            for(String c : centroids){
                distance = ldistance.distance(data.get(qid).get(0),c);
                if(mindist==-1 || distance<mindist){
                    mindist = distance;
                    nearestCentroid = c;
                }
            }   
            cluster.put(nearestCentroid, qid);
        }
    }

    public void clusterize(){
        for(String c : cluster.keySet()){
            List<String> neighbours;
            for(String neighbour : cluster.get(c)){
                neighbours.add(neighbour);
            }
        }
    }

    public void displaydata() {
        System.out.println(data);
    }

}