package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private Vector<Model> modelsToPublish;
    private String name;
    private int date;
    private int cnt;

    public ConfrenceInformation(String name, int date) {
        this.modelsToPublish = new Vector<Model>();
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
        return this.date >= this.cnt;
    }

    public void addModel(Model model) {
        this.modelsToPublish.add(model);
    }

    public Vector<Model> getModels() {
        return this.modelsToPublish;
    }
}
