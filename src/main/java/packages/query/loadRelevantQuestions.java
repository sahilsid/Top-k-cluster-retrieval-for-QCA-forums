package packages.query;

import java.awt.List;
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
    public Map<String,Double> relevantQuestions;
    Tfidf tfidf;

    public loadRelevantQuestions(Mtree r, Double search, String Query, Tfidf tfidf_) {
        this.root = r;
        this.searchRadius = search;
        this.query = Query;
        this.relevantQuestions = new HashMap<String,Double>();
        this.tfidf = tfidf_;
        load();
    }

    public Map<String,Double> getRelevantQids() {
        return relevantQuestions;
    }

    public Double getRelevance(String qid){
        return this.relevantQuestions.containsKey(qid) ? this.relevantQuestions.get(qid) : 0.0; 
    }

    public void load() {
        Queue<Mtree> queue = new LinkedList<Mtree>();
        for (Mtree child : root.getChildren())
            queue.add(child);

        while (queue.size() > 0) {

            Iterator<Mtree> queueIterator = queue.iterator();

            //System.out.println("\n____________");
            while(queueIterator.hasNext()){
                Mtree temp = queueIterator.next();
                temp.dataDisplay();
            }

            //System.out.println("\n____________");
            Mtree node = queue.remove();
            Double queryDistance = tfidf.getquerydistance(node.getQid(), query);
            if (node.isLeaf()) {
                //System.out.println(" Leaf Node : " + node.getQid() + " Querydistance : " + queryDistance + "  : " );

                for (Mtree child : node.getChildren()) {
                    if (queryDistance - tfidf.getdistance(node.getQid(), child.getQid()) < searchRadius) {
                        Double dist = tfidf.getquerydistance(child.getQid(), query);
                        if (dist < searchRadius) {
                            relevantQuestions.put(child.getQid(),dist);
                        }
                    }
                }
            } else {
                //System.out.println(" Routng Object : " + node.getQid());
                for (Mtree child : node.getChildren()) {
                    //System.out.println(" query dist :  "+queryDistance + " Parent dist : " + tfidf.getdistance(node.getQid(), child.getQid() )+ " Noderadius : "+ node.getRadius());
                    if (queryDistance - tfidf.getdistance(node.getQid(), child.getQid()) < searchRadius
                            + node.getRadius()) {
                        Double dist = tfidf.getquerydistance(child.getQid(), query);
                        if (dist < searchRadius + node.getRadius()) {
                            queue.add(child);
                        }
                    }
                }
            }
        }
    }

}