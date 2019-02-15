package packages.mtree;

import java.util.*;

public class sampleTree {

    private Data data;
    private List<sampleTree> children;
    private sampleTree parent;

    public sampleTree() {
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public sampleTree(Data data) {
        this.parent = null;
        this.children = new ArrayList<>();
        this.data = new Data(data);
    }

    public void addChild(sampleTree child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void addChild(Data data) {
        sampleTree newChild = new sampleTree(data);
        this.addChild(newChild);
    }

    public void addChildren(List<sampleTree> children) {
        for (sampleTree t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
    }

    public List<sampleTree> getChildren() {
        return children;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data.updateData(data);
    }

    private void setParent(sampleTree parent) {
        this.parent = parent;
        this.data.setSimiliarity(similiarity);
    }

    public sampleTree getParent() {
        return parent;
    }

    
}