package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {
    
    @Expose(serialize = true, deserialize = true)
    private String name;

    @Expose(serialize = true, deserialize = true)
    private int date;

    @Expose(serialize = true, deserialize = true)
    private List<Model> publications;

    public ConfrenceInformation(String name, int date) {
        this.publications = new LinkedList<Model>();
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public int getDate() {
        return this.date;
    }

    public void addModel(Model model) {
        this.publications.add(model);
    }

    public List<Model> getModels() {
        return this.publications;
    }
}
