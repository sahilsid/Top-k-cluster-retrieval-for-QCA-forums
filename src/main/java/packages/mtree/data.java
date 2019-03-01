package packages.mtree;

import java.util.*;

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
    public void display(){
        System.out.print(" " + this.id + " ( " + this.parentSimiliarity + " ) " );
    }

}
