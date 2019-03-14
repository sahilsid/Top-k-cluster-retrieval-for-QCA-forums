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
public class DBSCAN  {
    Mtree root;
    basicAlgo1 b= new basicAlgo1();
   
    public static void main(String[] args) throws Exception {
        DBSCAN d =new DBSCAN(b.p,q,r,qid_sort_slist,qid_sort_tlist);
    }
    DBSCAN(String p, String q, Mtree r, List qid_sort_tlist, List qid_sort_slist) throws Exception{
        this.root = r;
    }
    FindCluster
    public List FindCluster(String p, String q, Mtree r, List qid_sort_tlist, List qid_sort_slist){
    List<String> C;
    List<String> neighbors=rangeQuery(b.q,b.p);
   
    if (neighbors.size()<3){
        qid_sort_tlist.remove(p);
        qid_sort_slist.remove(p);
        //mark as noise
        return C;
        
    }
    else{

    }
    }

}