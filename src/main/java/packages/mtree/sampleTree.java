package packages.mtree;

import java.util.*;

public class sampleTree {

    private List<sampleTree> children;
    private sampleTree parent;
    private Data data;

    class Data {
        String id = null;
        Double parentSimiliarity = null;

        public Data(String id) {
            this.id = id;
        }

        public Data(Data data) {
            this.id = data.id;
            this.parentSimiliarity = data.parentSimiliarity;
        }

        public Data getData() {
            return this;
        }

        public void updateData(Data data) {
            this.id = data.id;
            this.parentSimiliarity = data.parentSimiliarity;
        }

        public void setSimiliarity(Double similiarity) {
            this.parentSimiliarity = similiarity;
        }

    }

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
    }

    public sampleTree getParent() {
        return parent;
    }

    public static void main(String[] args) {
        sampleTree<String> root = new sampleTree<>("Root");

        sampleTree<String> child1 = new sampleTree<>("Child1");
        child1.addChild("Grandchild1");
        child1.addChild("Grandchild2");

        sampleTree<String> child2 = new sampleTree<>("Child2");
        child2.addChild("Grandchild3");

        root.addChild(child1);
        root.addChild(child2);
        root.addChild("Child3");

        root.addChildren(
                Arrays.asList(new sampleTree<>("Child4"), new sampleTree<>("Child5"), new sampleTree<>("Child6")));

        for (sampleTree node : root.getChildren()) {
            System.out.println(node.getData());
        }
    }
}