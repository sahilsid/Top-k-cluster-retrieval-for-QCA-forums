
package packages.kmedoid;

import java.util.*;
import packages.textprocess.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;

/**
 * generate
 */
public class Cluster {

    int k;
    public Multimap<String, String> clusters;
    Map<String, List<String>> data;
    List<String> centroids;
    JsonNode questions;
    Preprocess p;
    List<String> processed;
    public Tfidf tfidf;

    public static void main(String[] args) throws Exception {
        Cluster a = new Cluster(4);
        List<String> qids = new ArrayList<String>();

        for (String k : a.data.keySet())
            qids.add(k);

        a.displaydata();
        // a.initialize();
    }

    public Cluster(int a) throws Exception {
        k = a;
        clusters = ArrayListMultimap.create();
        data = new HashMap<String, List<String>>();

        tfidf = new Tfidf();

        p = new Preprocess();
        questions = tfidf.getquestions();
        processed = new ArrayList<String>();
        data = tfidf.getdata();
        initialize();
        while (formNewCentroids()) {
            System.out.println(".New Centroids : " + centroids);
            clusterize();
        }
    }

    public void reinitialize(int a) {
        System.out.println("New cluster size : " + a);

        processed.clear();
        centroids.clear();
        clusters.clear();
        this.k = a;
        initialize();
        while (formNewCentroids()) {
            // System.out.println("New Centroids : " + centroids);
            clusterize();
        }

    }

    public void reinitialize(List<String> newqids, int no) {
        System.out.println("New cluster size : " + no);

        processed.clear();
        data.clear();
        centroids.clear();
        clusters.clear();
        this.k = no;

        Iterator<String> nodeIterator = questions.fieldNames();

        while (nodeIterator.hasNext()) {
            String key = nodeIterator.next();
            if (newqids.contains(key)) {
                processed = p.keywords(questions.get(key).get("body").toString());
                data.put(key, processed);
            }
        }
        initialize();
        while (formNewCentroids()) {
            System.out.println("New Centroids : " + centroids);
            clusterize();
            List<String> temp = new ArrayList<String>(clusters.keySet());
            for (String var : temp) {
                if (clusters.get(var).size() == 1)
                    this.reinitialize(this.k - 1);
            }
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
        List<String> temp = new ArrayList<String>(clusters.keySet());
        for (String var : temp) {
            if (clusters.get(var).size() == 1)
                this.reinitialize(this.k - 1);
        }
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
            System.out.println("Node : " + qid + "\tNearest Centroid : " + nearestCentroid);
            if (!clusters.containsEntry(nearestCentroid, qid))
                clusters.put(nearestCentroid, qid);
        }
        System.out.println("Clusters : ");

        for (Object k : clusters.keySet()) {
            System.out.print(k + " : \t");
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
            // System.out.println(" Cluster : " + centroid);
            for (String node : clusters.get(centroid)) {
                similiarity = 0.0;
                for (String clusterNeighbour : clusters.get(centroid)) {
                    similiarity = similiarity + tfidf.similiarity(node, clusterNeighbour);
                }
                // System.out.println("\t node : " + node + " \t \t DistSum : " + similiarity);

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
        // System.out.println("New Centroids : " + centroids);

        return centroid_change;
    }

    public void displaydata() {
        System.out.println(clusters);
    }

}