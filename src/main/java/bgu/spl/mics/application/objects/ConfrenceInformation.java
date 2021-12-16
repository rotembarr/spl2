package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private List<Model> modelsToPublish;
    private String name;
    private int date;
    private int cnt;

    public ConfrenceInformation(String name, int date) {
        this.modelsToPublish = new LinkedList<Model>();
        this.name = name;
        this.date = date;
        this.cnt = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getDate() {
        return this.date;
    }

    public void advanceCnt() {
        this.cnt++;
    }

    public boolean dateHasCome() {
        return this.cnt >= this.date;
    }

    public void addModel(Model model) {
        this.modelsToPublish.add(model);
    }

    public List<Model> getModels() {
        return this.modelsToPublish;
    }
}
