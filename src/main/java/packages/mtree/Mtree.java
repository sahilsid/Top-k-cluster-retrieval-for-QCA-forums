package packages.mtree;

import java.util.*;
import packages.textprocess.*;

public class Mtree {

    private Data data;
    private LinkedList<Mtree> children;
    private Mtree parent;
    private double maxDistanceChild;
    private double radius;

    public Mtree() {
        this.parent = null;
        this.children = new LinkedList<>();
        this.maxDistanceChild = 0.0;
        this.radius = 0.0;
    }

    public Mtree(Data data) {
        this.parent = null;
        this.children = new LinkedList<>();
        this.data = new Data(data);
    }

    public void addChild(Mtree child, Tfidf tfidf) {
        if (child == null)
            return;
        child.setParent(this, tfidf);
        if (child.data != null && this.data != null)
            System.out.println("Setting parent of " + child.data.id + " to " + this.data.id);
        this.children.add(child);
    }

    public void addChild(Data data, Tfidf tfidf) {
        Mtree newChild = new Mtree(data);
        this.addChild(newChild, tfidf);
    }

    public void addChildren(List<Mtree> children, Tfidf tfidf) {
        for (Mtree t : children) {
            t.setParent(this, tfidf);
        }
        this.children.addAll(children);
    }

    public LinkedList<Mtree> getChildren() {
        return children;
    }

    public Data getData() {
        return data;
    }
    public String getQid() {
        return data.getDataQid();
    }

    public Double getRadius() {
        return radius;
    }

    public void setData(Data data) {
        this.data.updateData(data);
    }

    private void setParent(Mtree parent, Tfidf tfidf) {
        if (parent == null)
            return;
        this.parent = parent;
        if (parent.data != null) {
            this.data.setSimiliarity(tfidf.similiarity(parent.data.id, this.data.id));
            if (parent.maxDistanceChild < 2 * Math.acos(tfidf.similiarity(parent.data.id, this.data.id))) {
                parent.maxDistanceChild = 2 * Math.acos(tfidf.similiarity(parent.data.id, this.data.id));
                parent.radius = this.radius + parent.maxDistanceChild;
            }
        }
    }

    public void display() {
        Queue<Mtree> node = new LinkedList<Mtree>();
        node.add(this);
        int i = 0, j = 0;
        while (node.size() > 0) {
            Mtree temp = node.remove();
            if (temp != null && temp.data != null) {
                temp.data.display();
                // System.out.print(" maxDistanceChild : " + temp.maxDistanceChild + " ");
            }
            if (i == j) {

            }
            for (Mtree next : temp.children) {
                if (next != null)
                    node.add(next);
            }
        }
    }

    public void displaytree() {
        Queue<Mtree> parent = new LinkedList<Mtree>();
        Queue<Mtree> childrennode = new LinkedList<Mtree>();
        parent.add(this);
        int i = 0, j = 0;
        while (parent.size() > 0) {
            System.out.print("\n Level : " + i + " :\t  ");

            while (parent.size() > 0) {

                Mtree temp = parent.remove();
                if (temp != null && temp.data != null) {
                    temp.data.display();
                     System.out.print("("); temp.childrenDisplay(); System.out.println(  ") ");
                }
                // System.out.print("\t"+temp.data.id);

                for (Mtree next : temp.children) {
                    if (next != null)
                        childrennode.add(next);
                }
            }
            while (childrennode.size() > 0) {
                Mtree temp = childrennode.remove();
                if (temp != null)
                    parent.add(temp);
            }
            System.out.println("\n_____________________________________________________________________");
            i++;
        }
    }

    public void childrenDisplay() {
      for(Mtree a : this.children){
          if(a!=null  && a.data!=null)
            a.data.display();
      }
    }
    public void dataDisplay() {
            if(this!=null  && this.data!=null)
              this.data.display();
      }
    public Boolean isLeaf() {
        return this.children.getLast()!=null ? this.children.getLast().radius==0.0 : true;
    }

    public Mtree getParent() {
        return parent;
    }
}