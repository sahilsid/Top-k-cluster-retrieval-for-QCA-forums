
package packages.kmedoid;

import java.util.*;
import packages.textprocess.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;

/**
 * generate
 */
public class Cluster {

    int k;
    Multimap<String, String> clusters;
    Map<String, List<String>> data;
    List<String> centroids;

    public static void main(String[] args) {
        Cluster a = new Cluster(4);
        // a.displaydata();
        // a.initialize();
    }

    Cluster(int a) {
        k = a;
        clusters = ArrayListMultimap.create();
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
        initialize();
        while (formNewCentroids()) {
            System.out.println("New Centroids :  " + centroids);
            clusterize();
        }
    }

    public void initialize() {
        centroids = new ArrayList<String>();
        Object[] keys = data.keySet().toArray();
        for (int i = 0; i < this.k; i++) {
            Object randomkey = keys[new Random().nextInt(keys.length)];
            while (centroids.size() > 0 && centroids.contains((String) randomkey)) {
                randomkey = keys[new Random().nextInt(keys.length)];
            }
            centroids.add((String) randomkey);
        }
        System.out.println("Random Keys" + centroids);
        clusterize();
    }

    public void clusterize() {
        int distance, min;
        clusters.clear();
        String nearestCentroid = "";
        for (String qid : data.keySet()) {
            distance = 0;
            min = -1;
            for (String c : centroids) {
                distance = ldistance.distance(qid, c);
                if ((min == -1 || min > distance)) {
                    min = distance;
                    nearestCentroid = c;
                }
            }
            // System.out.println("Node : " + qid + "\tNearest Centroid : " +
            // nearestCentroid);
            if (!clusters.containsEntry(nearestCentroid, qid))
                clusters.put(nearestCentroid, qid);
        }
        System.out.println("Clusters : ");

        for (Object k : clusters.keySet()) {
            System.out.print(k + "      : \t");
            System.out.println(clusters.get((String) k));
        }
    }

    public boolean formNewCentroids() {
        String medoid = "";
        List<String> tempCentroid = new ArrayList<String>();
        boolean centroid_change = false;
        int distance = 0, min = -1;
        for (String elem : centroids) {
            tempCentroid.add(elem);
        }
        for (String centroid : tempCentroid) {
            min = -1;
            System.out.println(" Cluster  : " + centroid);
            for (String node : clusters.get(centroid)) {
                distance = 0;
                for (String clusterNeighbour : clusters.get(centroid)) {
                    distance = distance + ldistance.distance(node, clusterNeighbour);
                }
                System.out.println("\t node  : " + node + " \t \t DistSum : " + distance);

                // System.out.println(" node : " + node + " Distance sum : " + distance + " min
                // : " + min);
                if (distance < min || min == -1) {
                    min = distance;
                    medoid = node;
                }
            }
            if (!centroids.contains(medoid)) {
                centroid_change = true;
                centroids.remove(centroid);
                centroids.add(medoid);
            }
        }
        System.out.println("New Centroids :  " + centroids);

        return centroid_change;
    }

    public void displaydata() {
        System.out.println(data);
    }

}