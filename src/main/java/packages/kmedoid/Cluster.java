
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
    JSONObject questions;
    Preprocess p;
    List<String> processed;

    Tfidf tfidf;

    public static void main(String[] args) {
        Cluster a = new Cluster(4);
        List<String> qids = new ArrayList<String>();

        for (String k : a.data.keySet())
            qids.add(k);
            
        a.displaydata();
        a.reinitialize(qids);
        // a.initialize();
    }

    Cluster(int a) {
        k = a;
        clusters = ArrayListMultimap.create();
        data = new HashMap<String, List<String>>();
        tfidf = new Tfidf();
        p = new Preprocess();
        questions = jsonImport.getjson("src/main/resources/android_questions.json");
        processed = new ArrayList<String>();

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

    public void reinitialize(List<String> newqids) {

        processed.clear();
        data.clear();
        centroids.clear();
        clusters.clear();

        for (Object key : questions.keySet()) {
            String k = (String) key;
            JSONObject value = (JSONObject) questions.get(key);
            if (newqids.contains(k)) {
                processed = p.keywords((String) value.get("body"));
                data.put(k, processed);
            }
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
        Double similiarity, max;
        clusters.clear();
        String nearestCentroid = "";
        for (String qid : data.keySet()) {
            similiarity = 0.0;
            max = -1.0;
            for (String c : centroids) {
                similiarity = tfidf.similiarity(qid, c);
                if ((max == -1 || max < similiarity)) {
                    max = similiarity;
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
        Double similiarity = 0.0, max = -1.0;
        for (String elem : centroids) {
            tempCentroid.add(elem);
        }
        for (String centroid : tempCentroid) {
            max = -1.0;
            System.out.println(" Cluster  : " + centroid);
            for (String node : clusters.get(centroid)) {
                similiarity = 0.0;
                for (String clusterNeighbour : clusters.get(centroid)) {
                    similiarity = similiarity + tfidf.similiarity(node, clusterNeighbour);
                }
                System.out.println("\t node  : " + node + " \t \t DistSum : " + similiarity);

                // System.out.println(" node : " + node + " similiarity sum : " + similiarity +
                // " max
                // : " + max);
                if (similiarity > max || max == -1) {
                    max = similiarity;
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