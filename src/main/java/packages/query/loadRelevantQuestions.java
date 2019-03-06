package packages.query;

import java.awt.List;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
            Mtree node = queue.remove();
            LinkedList<Mtree> children = node.getChildren();
            Double queryDistance = tfidf.getquerydistance(node.getQid(), query);
            if (node.isLeaf()) {
                System.out.println(" Leaf Node : " + node.getQid() + " Querydistance : " + queryDistance + "  : " );

                if (queryDistance < searchRadius) {
                    relevantQuestions.add(node.getQid());
                }
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