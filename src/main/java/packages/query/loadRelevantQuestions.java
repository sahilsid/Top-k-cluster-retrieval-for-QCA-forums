package packages.query;

import java.awt.List;
import java.util.*;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

import packages.mtree.Mtree;
import packages.textprocess.Tfidf;

/**
 * loadRelevantQuestions
 */
public class loadRelevantQuestions {

    Mtree root;
    Float searchRadius;
    String query;
    public Map<String,Float> relevantQuestions;
    Tfidf tfidf;

    public loadRelevantQuestions(Mtree r, Float search, String Query, Tfidf tfidf_) {
        this.root = r;
        this.searchRadius = search;
        this.query = Query;
        this.relevantQuestions = new HashMap<String,Float>();
        this.tfidf = tfidf_;
        load();
    }

    public Map<String,Float> getRelevantQids() {
        return relevantQuestions;
    }

    public Float getRelevance(String qid){
        return this.relevantQuestions.containsKey(qid) ? this.relevantQuestions.get(qid) : 0.0f; 
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
            Float queryDistance = tfidf.getquerydistance(node.getQid(), query);
            if (node.isLeaf()) {
                //System.out.println(" Leaf Node : " + node.getQid() + " Querydistance : " + queryDistance + "  : " );

                for (Mtree child : node.getChildren()) {
                    if (queryDistance - tfidf.getdistance(node.getQid(), child.getQid()) < searchRadius) {
                        Float dist = tfidf.getquerydistance(child.getQid(), query);
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
                        Float dist = tfidf.getquerydistance(child.getQid(), query);
                        if (dist < searchRadius + node.getRadius()) {
                            queue.add(child);
                        }
                    }
                }
            }
        }
        relevantQuestions = relevantQuestions.entrySet().stream().sorted(comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

    }

}