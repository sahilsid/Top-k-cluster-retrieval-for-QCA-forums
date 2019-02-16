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
        Cluster a = new Cluster(5);
        Map<String, mtree_beta> tree = new HashMap<String, mtree_beta>();
        mtree_beta root = new mtree_beta();
        List<String> nextLevel = new ArrayList<String>();
        for (String key : a.clusters.keySet()) {
            nextLevel.add(key);
            tree.put(key, new mtree_beta(new Data(key)));
            for (String children : a.clusters.get(key)) {
                if (children != key)
                    tree.get(key).addChild(new mtree_beta(new Data(children)), a.tfidf);
            }
        }
        System.out.println("initialized  : ");
        int i=0;
        while (nextLevel.size() / 2 > 1) {
            System.out.println("New Level Formation  : ");
            a.reinitialize(nextLevel, nextLevel.size() / 2);
            nextLevel.clear();
            for (String key : a.clusters.keySet()) {
                nextLevel.add(key+i);
                tree.put(key+i, new mtree_beta(new Data(key)));
                for (String children : a.clusters.get(key)) {
                    if (children != key)
                        tree.get(key+i).addChild(tree.get(children), a.tfidf);
                }

            }
            i++;
        }
        for (String top : nextLevel) {
            root.addChild(tree.get(top), a.tfidf);
        }
        root.display();
    }

}