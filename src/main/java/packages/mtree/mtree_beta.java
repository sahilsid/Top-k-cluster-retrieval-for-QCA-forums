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

    public void addChild(mtree_beta child, Tfidf tfidf) {
        if (child == null)
            return;
        child.setParent(this, tfidf);
        if (child.data != null && this.data != null)
            System.out.println("Setting parent of " + child.data.id + " to " + this.data.id);
        this.children.add(child);
    }

    public void addChild(Data data, Tfidf tfidf) {
        mtree_beta newChild = new mtree_beta(data);
        this.addChild(newChild, tfidf);
    }

    public void addChildren(List<mtree_beta> children, Tfidf tfidf) {
        for (mtree_beta t : children) {
            t.setParent(this, tfidf);
        }
        this.children.addAll(children);
    }

    public List<mtree_beta> getChildren() {
        return children;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data.updateData(data);
    }

    private void setParent(mtree_beta parent, Tfidf tfidf) {
        if (parent == null)
            return;
        this.parent = parent;
        if (parent.data != null)
            this.data.setSimiliarity(tfidf.similiarity(parent.data.id, this.data.id));
    }

    public mtree_beta getParent() {
        return parent;
    }
}