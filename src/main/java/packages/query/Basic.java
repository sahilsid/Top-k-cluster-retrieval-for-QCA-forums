package packages.query;

import java.util.*;
import java.util.stream.Collectors;

import packages.mtree.*;
import packages.textprocess.Tfidf;
import packages.query.*;
import packages.userinteraction.Mapping;

import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

/**
 * Basic Query
 */
public class Basic {
    List<String> qids;
    List<String> tList;
    List<String> sList;
    List<List<String>> rList;
    Map<String, Integer> interactionLevel;
    List<String> noise;
    Float Tau;
    Float bound;
    Mtree root;
    Mapping mapping;
    String query;
    String queryUserId;
    Tfidf tfidf;
    Float alpha;
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
        this.interactionLevel = new HashMap<String, Integer>();
        this.Tau = Float.POSITIVE_INFINITY;
        this.tfidf = tfidf;
        this.queryUserId = mapping.QidUid.get(query);
        this.relevantQuestions = new loadRelevantQuestions(root, 3.1f, q, tfidf);
        qids.addAll(relevantQuestions.getRelevantQids().keySet());
        for (String qid : qids) {
            interactionLevel.put(mapping.QidUid.get(qid),
                    mapping.getInteractionLevel(mapping.QidUid.get(qid), queryUserId));
        }

        interactionLevel = interactionLevel.entrySet().stream().sorted(comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

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

        Float sb;
        Float tb;

        do {

            question = (String) iter_tlist.next();
            List cluster = FindCluster(question, 5);

            if (!cluster.isEmpty()) {
                rList.add(cluster);
                Tau = (alpha * avgSocialDistance(cluster)) + ((1 - alpha) * avgTextualDistance(cluster));
            }

            uid = mapping.QidUid.get((String) iter_slist.next());
            System.out.println(uid);

            sb = mapping.getSocialDistance(uid, queryUserId);
            tb = relevantQuestions.getRelevance((String) iter_tlist.next());
            bound = (alpha * (sb)) + ((1 - alpha) * (1 - tb));

        } while (bound >= Tau || !tList.isEmpty());

    }

    public Float avgSocialDistance(List cList) {
        String uid;
        Float socialDistance = 0.0f;
        Float sum = 0.0f;
        for (Object ques : cList) {
            uid = mapping.QidUid.get((String) ques);
            socialDistance = mapping.getSocialDistance(uid, queryUserId);
            sum += socialDistance;
        }
        return !cList.isEmpty() ? (Float) sum / cList.size() : 0.0f;
    }

    public Float avgTextualDistance(List cList) {
        Float text_distance = 0.0f;
        for (Object ques : cList) {
            text_distance += relevantQuestions.relevantQuestions.get((String) ques);
        }
        return  cList.isEmpty() ? 0.0f : text_distance / cList.size();
    }

    public void initSList() {
        sList = new LinkedList<String>();
        for (String user : interactionLevel.keySet()) {
            sList.addAll(qids.stream().filter(mapping.UidQid.get(user)::contains).collect(Collectors.toList()));
        }
        System.out.println("sList Generated : " + sList);
    }

    public void initTList() {

        // List<Float> sorted_value_list = new LinkedList<Float>(relevantQuestions.getRelevantQids().values());
        // Collections.sort(sorted_value_list);
        // for (Object val : sorted_value_list) {
        //     for (Object qid : relevantQuestions.getRelevantQids().keySet()) {
        //         if (relevantQuestions.relevantQuestions.get(qid) == val) {
        //             tList.add((String) qid);
        //         }
        //     }

        // }
        tList = new LinkedList<String>(qids);
        System.out.println("tList Generated : " + tList);
    }

    public List FindCluster(String corePoint, Integer qMinPts) {

        String Question;

        RangeQuery range = new RangeQuery(root, 3.1f, query, corePoint, tfidf, mapping, 0.5f);

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
                range = new RangeQuery(root, 3.1f, query, Question, tfidf, mapping, 0.5f);
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