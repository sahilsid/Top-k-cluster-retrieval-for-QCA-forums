package packages.query;

import java.awt.List;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;
import packages.mtree.Mtree;
import packages.textprocess.Tfidf;

/**
 * loadRelevantQuestions
 */
public class loadRelevantQuestions {

    Mtree root;
    Double searchRadius;
    String query;
    LinkedList<String> relevantQuestions;
    Tfidf tfidf;

    public loadRelevantQuestions(Mtree r, Double search, String Query, Tfidf tfidf_) {
        this.root = r;
        this.searchRadius = search;
        this.query = Query;
        this.relevantQuestions = new LinkedList<String>();
        this.tfidf = tfidf_;
        load();
    }

    public LinkedList<String> getRelevantQids() {
        return relevantQuestions;
    }

    public void load() {
        Queue<Mtree> queue = new LinkedList<Mtree>();
        for (Mtree child : root.getChildren())
            queue.add(child);

        while (queue.size() > 0) {

            Iterator<Mtree> queueIterator = queue.iterator();

            System.out.println("\n____________");
            while(queueIterator.hasNext()){
                Mtree temp = queueIterator.next();
                temp.dataDisplay();
            }

            System.out.println("\n____________");
            Mtree node = queue.remove();
            Double queryDistance = tfidf.getquerydistance(node.getQid(), query);
            if (node.isLeaf()) {
                System.out.println(" Leaf Node : " + node.getQid() + " Querydistance : " + queryDistance + "  : " );

                for (Mtree child : node.getChildren()) {
                    if (queryDistance - tfidf.getdistance(node.getQid(), child.getQid()) < searchRadius) {
                        if (tfidf.getquerydistance(child.getQid(), query) < searchRadius) {
                            relevantQuestions.add(child.getQid());
                        }
                    }
                }
            } else {
                System.out.println(" Routng Object : " + node.getQid());
                for (Mtree child : node.getChildren()) {
                    System.out.println(" query dist :  "+queryDistance + " Parent dist : " + tfidf.getdistance(node.getQid(), child.getQid() )+ " Noderadius : "+ node.getRadius());
                    if (queryDistance - tfidf.getdistance(node.getQid(), child.getQid()) < searchRadius
                            + node.getRadius()) {
                        if (tfidf.getquerydistance(child.getQid(), query) < searchRadius + node.getRadius()) {
                            queue.add(child);
                        }
                    }
                }
            }
        }
    }

}