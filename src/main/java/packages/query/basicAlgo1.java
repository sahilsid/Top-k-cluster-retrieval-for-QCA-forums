package packages.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.*;
import packages.mtree.*;
import packages.textprocess.Tfidf;
import packages.query.*;
import packages.userinteraction.mapping;

/**
 * Basic Query
 */
public class basicAlgo1 {
    // LinkedList<String> relevantQuestionsQ;
    List<String> qids;
    Map<String, String> qid_uid_sort;
    public List<String> qid_sort_tlist;
    public List<String> qid_sort_slist;
    List<List> rlist;
    Double Tau = Double.POSITIVE_INFINITY;
    Double bound;
    Mtree root;
    generate g;
    mapping m;
    loadRelevantQuestions l;
    public String q;
    public String q_userid;
    Double alpha;

    public static void main(String[] args) throws Exception {
        basicAlgo1 d = new basicAlgo1();
        d.makeSlist();
        d.makeTlist();
        d.mainBody(); 

    }

    basicAlgo1() throws Exception {
        g = new generate(4);
        m = new mapping();
        qids = new LinkedList<String>();
        q = "<p>I want to do a comparison of different types of configurations of roms kernels etc. What are the best tools that I can use to do this.</p>\n\n<p>I know this cannot be done with one tool, seperate is fine. The Tools that I am using now are as follow:</p>\n\n<p>Rom/kernel: Quadrant Standard Edition, Linpack for Android\nwifi: wifi-analizer\nwifi/3g: speetest.net app</p>\n\n<p>But are these tools good to do benchmarking, or are there other tools that do a better job. I heard that some of the rom/kernels produce fake results for these tools.</p>\n";
        q_userid = "5832";
        l = new loadRelevantQuestions(g.root, 3.1, q, g.tfidf);

    }

    public List mainBody() {
        String p;
        Iterator iter_tlist = qid_sort_tlist.iterator();
        Iterator iter_slist = qid_sort_slist.iterator();
        Integer sb;
        Double tb;
        String uid;
        Integer c_questions;
        // Iterator<String> nodeIterator = qid_sort_tlist;
        do {
            // if((String)iter_slist.next()==(String)iter_tlist.next()){
            p = (String) iter_tlist.next();
            List c = FindCluster(p, q, root, qid_sort_tlist, qid_sort_slist);
            if (!c.isEmpty()) {
                rlist.add(c);
                Tau = (alpha * avgSocialDistQueryObjects(c)) + ((1 - alpha) * avgTextDistQueryObjects(c));
            }

            uid = m.QidUid.get((String) iter_slist.next());
            System.out.println(uid);
            if (m.UidUid_commonQs_sorted.containsKey(uid + "." + q_userid)) {
                sb = m.UidUid_commonQs_sorted.get(uid + "." + q_userid);
            } else if ((m.UidUid_commonQs_sorted.containsKey(q_userid + "." + uid))) {
                sb = m.UidUid_commonQs_sorted.get(uid + "." + q_userid);
            } else {
                sb = 0;
            }
            tb = l.relevantQuestions.get((String) iter_tlist.next());
            bound = (alpha * (sb)) + ((1 - alpha) * (1 - tb));

        } while (bound >= Tau || qid_sort_slist.isEmpty() || qid_sort_tlist.isEmpty());
        
        return rlist;

    }

    public double avgSocialDistQueryObjects(List cList) {
        String uid;
        Integer c_questions = 0;
        Integer total_c_questions = 0;
        Double avg_c_questions = 0.0;
        for (Object ques : cList) {
            uid = m.QidUid.get((String)ques);
            if (m.UidUid_commonQs_sorted.containsKey(uid + "." + q_userid)) {
                c_questions = m.UidUid_commonQs_sorted.get(uid + "." + q_userid);
            }
            if (m.UidUid_commonQs_sorted.containsKey(q_userid + "." + uid)) {
                c_questions = m.UidUid_commonQs_sorted.get(uid + "." + q_userid);
            }
            total_c_questions = total_c_questions + c_questions;
        }
        avg_c_questions = (double) total_c_questions / cList.size();
        return avg_c_questions;

    }

    public Double avgTextDistQueryObjects(List cList) {
        Double text_distance = 0.0;
        Double avg_text_distance;
        for (Object ques : cList) {
            text_distance = text_distance + l.relevantQuestions.get((String)ques);
        }
        avg_text_distance = text_distance / cList.size();
        return avg_text_distance;
    }

    public void makeSlist() {

        for (String Qid : l.relevantQuestions.keySet()) {
            qids.add(Qid);

        }

        for (String Uid : m.UidUid_commonQs_sorted.keySet()) {
            StringTokenizer token = new StringTokenizer(Uid, ".");
            String user1 = token.nextToken();
            String user2 = token.nextToken();
            List<String> qid_list1 = m.UidQid.get(user1);
            List<String> qid_list2 = m.UidQid.get(user2);
            for (String Qid : qids) {
                if (qid_list1.contains(Qid) || qid_list2.contains(Qid)) {
                    qid_uid_sort.put(Qid, m.QidUid.get(Qid));
                    qid_sort_slist.add(Qid);
                }
            }
        }
        // System.out.println(qid_uid_sort);
        System.out.println(qid_sort_slist);
    }

    public void makeTlist() {

        List<Double> sorted_value_list = new LinkedList<Double>(l.relevantQuestions.values());
        Collections.sort(sorted_value_list);
        for (Object val : sorted_value_list) {
            for (Object qid : l.relevantQuestions.keySet()) {
                if (l.relevantQuestions.get(qid) == val) {
                    qid_sort_tlist.add((String) qid);
                }
            }

        }
        // System.out.println(qid_sort_tlist);
    }
    public List FindCluster(String p, String q, Mtree r, List qid_sort_tlist, List qid_sort_slist){
        List<String> C;
        String Question;
        List<String> neighbours2;
        List<String> neighbors=rangeQuery(q,p);
        if (neighbors.size()<3){
            qid_sort_tlist.remove(p);
            qid_sort_slist.remove(p);
            //mark as noise
            return C;
            
        }
        else{
            C.addAll(neighbors);
            qid_sort_tlist.remove(neighbors);
            qid_sort_slist.remove(neighbors);
            neighbors.remove(p);
            while(!neighbors.isEmpty()){
                Question=neighbors.remove(0);//check if removes first elemet
                neighbours2=rangeQuery(q,Question);
                if(neighbors2.size())

            }

    
        }
   
    
    }

}