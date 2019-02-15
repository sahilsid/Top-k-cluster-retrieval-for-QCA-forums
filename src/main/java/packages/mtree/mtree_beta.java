package packages.mtree;

import java.util.*;
import packages.textprocess.*;

public class mtree_beta {

    private Data data;
    private List<mtree_beta> children;
    private mtree_beta parent;

    public mtree_beta() {
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public mtree_beta(Data data) {
        this.parent = null;
        this.children = new ArrayList<>();
        this.data = new Data(data);
    }

    public void addChild(mtree_beta child , Tfidf tfidf) {
        child.setParent(this,tfidf);
        this.children.add(child);
    }

    public void addChild(Data data, Tfidf tfidf) {
        mtree_beta newChild = new mtree_beta(data);
        this.addChild(newChild,tfidf);
    }

    public void addChildren(List<mtree_beta> children , Tfidf tfidf) {
        for (mtree_beta t : children) {
            t.setParent(this,tfidf);
        }
        this.children.addAll(children);
    }

    public List<mtree_beta> getChildren() {
        return children;
    }

    public void display() {
        System.out.println("Parent : " + this.data.id);
        for ( mtree_beta child : this.children) {
            System.out.println("Children : " + child.data.id + " Similiarity with parent : " + child.data.parentSimiliarity);
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data.updateData(data);
    }

    private void setParent(mtree_beta parent, Tfidf tfidf) {
        this.parent = parent;
        this.data.setSimiliarity(tfidf.getSimiliarity(parent.data.id,this.data.id));
    }

    public mtree_beta getParent() {
        return parent;
    }
}