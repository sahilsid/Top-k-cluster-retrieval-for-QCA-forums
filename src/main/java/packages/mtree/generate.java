package packages.mtree;

import java.util.*;

import packages.kmedoid.Cluster;
import packages.query.loadRelevantQuestions;
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

    Mtree root;
    public Tfidf tfidf;
    public static void main(String[] args) throws Exception {
        generate a = new generate(4);
        loadRelevantQuestions b = new loadRelevantQuestions(a.root, 3.1, "<p>I want to do a comparison of different types of configurations of roms kernels etc. What are the best tools that I can use to do this.</p>\n\n<p>I know this cannot be done with one tool, seperate is fine. The Tools that I am using now are as follow:</p>\n\n<p>Rom/kernel: Quadrant Standard Edition, Linpack for Android\nwifi: wifi-analizer\nwifi/3g: speetest.net app</p>\n\n<p>But are these tools good to do benchmarking, or are there other tools that do a better job. I heard that some of the rom/kernels produce fake results for these tools.</p>\n", a.tfidf);
        System.out.println(b.getRelevantQids());

    }

    public generate(int clustersize) throws Exception {
        Cluster a = new Cluster(clustersize);
        tfidf = a.tfidf;
        Map<String, Mtree> tree = new HashMap<String, Mtree>();
        root = new Mtree();
        a.displaydata();
        List<String> nextLevel = new ArrayList<String>();
        for (String key : a.clusters.keySet()) {
            nextLevel.add(key);
            tree.put(key, new Mtree(new Data(key)));
            for (String children : a.clusters.get(key)) {
                // if (children != key)
                    tree.get(key).addChild(new Mtree(new Data(children)), a.tfidf);
            }
        }
       
        System.out.println("initialized  : ");
        int i = 0;
        while (nextLevel.size() / 2 > 1) {
            System.out.println("New Level Formation  : ");
            a.reinitialize(nextLevel, nextLevel.size() / 2);
            a.displaydata();
            nextLevel.clear();
            for (String key : a.clusters.keySet()) {
                nextLevel.add(key + i);
                tree.put(key + i, new Mtree(new Data(key)));
                for (String children : a.clusters.get(key)) {
                    // if (children != key)
                        tree.get(key + i).addChild(tree.get(children), a.tfidf);
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