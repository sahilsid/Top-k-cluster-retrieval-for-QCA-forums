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
        List<mtree_beta> tree = new ArrayList<mtree_beta>();
        for (String key : a.clusters.keySet()) {
            tree.add(i, new mtree_beta(new Data(key)));
            for (String children : a.clusters.get(key)) {
                tree.get(i).addChild(new mtree_beta(new Data(children)),a.tfidf);
            }
            i++;
        }
        for (mtree_beta node : tree) {
            node.display();
        }
    }
}