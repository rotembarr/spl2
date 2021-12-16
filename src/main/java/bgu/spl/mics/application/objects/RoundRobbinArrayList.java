package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

public class RoundRobbinArrayList<T> {
    List <T> list = null;
    int index;

    public RoundRobbinArrayList() {
        this.list = new ArrayList<T>();
        this.index = 0;
    }

    public int size() {
        return this.list.size();
    }
    public boolean add(T arg) {
        return this.list.add(arg);
    }

    public T next() {
        if (index >= this.list.size()) {
            throw new IllegalArgumentException("index-"+index+"; list size-"+this.list.size()+";");
        }
        T next = this.list.get(index);
        this.index = (this.index + 1) % this.list.size();
        return next;
    }

    public T remove(T arg) {
        int delIndex = this.list.indexOf(arg);
        if (delIndex >= 0 && delIndex < this.index) {
            this.index--;
        }
        return this.list.remove(delIndex);
    }

    public boolean contains(T arg) {
        return this.list.contains(arg);
    }
}
