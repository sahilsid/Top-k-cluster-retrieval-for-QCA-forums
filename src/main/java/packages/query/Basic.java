package packages.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;
import packages.mtree.*;
import packages.textprocess.Tfidf;
import packages.query.*;
import packages.userinteraction.Mapping;;

/**
 * Basic Query
 */
public class Basic {
    List<String> qids;
    List<String> tList;
    List<String> sList;
    List<List<String>> rList;
    List<String> noise;
    Double Tau;
    Double bound;
    Mtree root;
    Mapping mapping;
    String query;
    String queryUserId;
    Tfidf tfidf;
    Double alpha;
    loadRelevantQuestions relevantQuestions;

    public static void main(String[] args) throws Exception {
        Generate generateMtree = new Generate(4);
        Basic d = new Basic(
                "<p>I want to do a comparison of different types of configurations of roms kernels etc. What are the best tools that I can use to do this.</p>\n\n<p>I know this cannot be done with one tool, seperate is fine. The Tools that I am using now are as follow:</p>\n\n<p>Rom/kernel: Quadrant Standard Edition, Linpack for Android\nwifi: wifi-analizer\nwifi/3g: speetest.net app</p>\n\n<p>But are these tools good to do benchmarking, or are there other tools that do a better job. I heard that some of the rom/kernels produce fake results for these tools.</p>\n",
                generateMtree.root, generateMtree.tfidf);

    }

    Basic(String q, Mtree root, Tfidf tfidf) throws Exception {

        this.mapping = new Mapping();
        this.noise = new LinkedList<String>();
        this.qids = new LinkedList<String>();
        this.query = q;
        this.root = root;
        this.Tau = Double.POSITIVE_INFINITY;
        this.tfidf = tfidf;
        this.queryUserId = mapping.QidUid.get(query);
        this.relevantQuestions = new loadRelevantQuestions(root, 3.1, q, tfidf);
        qids.addAll(relevantQuestions.getRelevantQids().keySet());
        initSList();
        initTList();
        GenerateTopKCluster();
    }

    public List retrieveTopClusters() {
        return rList;
    }

    void GenerateTopKCluster() {
        String question;
        String uid;

        Iterator iter_tlist = tList.iterator();
        Iterator iter_slist = sList.iterator();

        Integer sb;
        Double tb;

        do {

            question = (String) iter_tlist.next();
            List cluster = FindCluster(question, 5);

            if (!cluster.isEmpty()) {
                rList.add(cluster);
                Tau = (alpha * avgSocialDistance(cluster)) + ((1 - alpha) * avgTextualDistance(cluster));
            }

            uid = mapping.QidUid.get((String) iter_slist.next());
            System.out.println(uid);

            sb = mapping.UidUid_commonQs_sorted.containsKey(uid + "." + queryUserId)
                    ? mapping.UidUid_commonQs_sorted.get(uid + "." + queryUserId)
                    : mapping.UidUid_commonQs_sorted.containsKey(queryUserId + "." + uid)
                            ? mapping.UidUid_commonQs_sorted.get(uid + "." + queryUserId)
                            : 0;

            tb = relevantQuestions.getRelevance((String) iter_tlist.next());
            bound = (alpha * (sb)) + ((1 - alpha) * (1 - tb));

        } while (bound >= Tau || !tList.isEmpty());

    }

    public Double avgSocialDistance(List cList) {
        String uid;
        Integer socialDistance = 0;
        Integer sum = 0;
        for (Object ques : cList) {
            uid = mapping.QidUid.get((String) ques);
            if (mapping.UidUid_commonQs_sorted.containsKey(uid + "." + queryUserId))
                socialDistance = mapping.UidUid_commonQs_sorted.get(uid + "." + queryUserId);

            else if (mapping.UidUid_commonQs_sorted.containsKey(queryUserId + "." + uid))
                socialDistance = mapping.UidUid_commonQs_sorted.get(uid + "." + queryUserId);

            sum += socialDistance;
        }
        return !cList.isEmpty() ? (double) sum / cList.size() : 0.0;
    }

    public Double avgTextualDistance(List cList) {
        Double text_distance = 0.0;
        for (Object ques : cList) {
            text_distance += relevantQuestions.relevantQuestions.get((String) ques);
        }
        return cList.isEmpty() ? 0.0 : text_distance / cList.size();
    }

    public void initSList() {

        for (String Uid : mapping.UidUid_commonQs_sorted.keySet()) {
            StringTokenizer token = new StringTokenizer(Uid, ".");
            String user1 = token.nextToken();
            String user2 = token.nextToken();
            List<String> qid_user1 = mapping.UidQid.get(user1);
            List<String> qid_user2 = mapping.UidQid.get(user2);
            for (String Qid : qids) {
                if (qid_user1.contains(Qid) || qid_user2.contains(Qid)) {
                    sList.add(Qid);
                }
            }
        }
        System.out.println("sList Generated : " + sList);
    }

    public void initTList() {

        List<Double> sorted_value_list = new LinkedList<Double>(relevantQuestions.getRelevantQids().values());
        Collections.sort(sorted_value_list);
        for (Object val : sorted_value_list) {
            for (Object qid : relevantQuestions.getRelevantQids().keySet()) {
                if (relevantQuestions.relevantQuestions.get(qid) == val) {
                    tList.add((String) qid);
                }
            }

        }
        System.out.println("tList Generated : " + tList);
    }

    public List FindCluster(String corePoint, Integer qMinPts) {

        String Question;

        RangeQuery range = new RangeQuery(root, 3.1, query, corePoint, tfidf, mapping, 0.5);

        List<String> neighbours = new LinkedList(range.relevantQuestions.keySet());
        List<String> Cluster = new LinkedList<String>();
        List<String> indirectNeighbours;

        if (neighbours.size() < qMinPts) {

            tList.remove(corePoint);
            sList.remove(corePoint);
            noise.add(corePoint);

            return Cluster;

        } else {

            Cluster.addAll(neighbours);
            tList.removeAll(neighbours);
            sList.removeAll(neighbours);
            neighbours.remove(corePoint);

            while (!neighbours.isEmpty()) {

                Question = neighbours.remove(0);
                range = new RangeQuery(root, 3.1, query, Question, tfidf, mapping, 0.5);
                indirectNeighbours = new LinkedList(range.relevantQuestions.keySet());

                if (indirectNeighbours.size() > qMinPts) {
                    if (noise.contains(corePoint)) {
                        Cluster.add(corePoint);
                    } else {
                        if (!Cluster.contains(corePoint)) {
                            Cluster.add(corePoint);
                            tList.remove(corePoint);
                            sList.remove(corePoint);
                            neighbours.add(corePoint);
                        }
                    }
                }
            }
        }
        return Cluster;
    }
}