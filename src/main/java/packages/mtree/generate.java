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

    public static void main(String[] args) throws Exception{
        Cluster a = new Cluster(100);
        System.out.println("hi  : ");
        Map<String, Mtree> tree = new HashMap<String, Mtree>();
        Mtree root = new Mtree();
        List<String> nextLevel = new ArrayList<String>();
        for (String key : a.clusters.keySet()) {
            nextLevel.add(key);
            tree.put(key, new Mtree(new Data(key)));
            for (String children : a.clusters.get(key)) {
                if (children != key)
                    tree.get(key).addChild(new Mtree(new Data(children)), a.tfidf);
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
                tree.put(key+i, new Mtree(new Data(key)));
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
        root.displaytree();
    }

}