package packages.mtree;

import java.util.*;

import packages.kmedoid.Cluster;
import packages.textprocess.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.google.common.collect.ArrayListMultimap;

/**
 * generate
 */

/**
 * generate
 */
public class generate {

    public static void main(String[] args) {
        Cluster a = new Cluster(4);
        int i = 0;
        Map<String, mtree_beta> tree = new HashMap<String, mtree_beta>();
        mtree_beta root;
        List<String> nextLevel = new ArrayList<String>();
        for (String key : a.clusters.keySet()) {
            nextLevel.add(key);
            tree.put(key, new mtree_beta(new Data(key)));
            for (String children : a.clusters.get(key)) {
                tree.get(key).addChild(new mtree_beta(new Data(children)), a.tfidf);
            }
            i++;
        }
        while (nextLevel.size()/2 > 0) {
            a.reinitialize(nextLevel, nextLevel.size() / 2);
            nextLevel.clear();
            System.out.println("New Cluster  : "+a.clusters.keySet());
            for (String key : a.clusters.keySet()) {
                nextLevel.add(key);
                tree.put(key, new mtree_beta(new Data(key)));
                for (String children : a.clusters.get(key)) {
                    tree.get(key).addChild(tree.get(children), a.tfidf);
                }
                i++;
            }
            System.out.println("Next Levels  : "+nextLevel);
        }
        root = tree.get(nextLevel.get(0));
        System.out.println("Root  : ");root.display();

        for (Object node : tree.keySet()) {
            tree.get(node).display();
        }
    }
}