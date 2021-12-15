package bgu.spl.mics.application.objects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bgu.spl.mics.Pair;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    // Local variables.
    private String name;
    private String department;
    private Degree status;

    // Statistics.
    private int publications;
    private int papersRead;

    // Train event variables.
    private List<Model> modelsToTrain;

    public Student(Degree status) {
        this.status = status;
    }

    public Student(String name, String department, Student.Degree status) {
        this.name = name;
        this.department = department;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        this.modelsToTrain = new LinkedList<Model>(); 
    }

    public String getName() {
        return this.name;
    }

    public String getDepartment() {
        return this.department;
    }

    public Degree getDegree() {
        return this.status;
    }

    public int getPublications() {
        return this.publications;
    }

    public int getPaperRead() {
        return this.papersRead;
    }

    public boolean modelQueueEmpty() {
        return this.modelsToTrain.size() == 0;
    }

    public void addModelToTrain(Model model) {
        this.modelsToTrain.add(model);
    }

    public Model getModelToTrain() {
        return this.modelsToTrain.remove(0);
    }
    
    public void readPublication(List<Model> publishModels) {
        for (Iterator<Model> iter = publishModels.iterator(); iter.hasNext();) {
            Model model = iter.next();

            // Model read.
            this.papersRead++;

            // If we sent this publication, advance counter.
            if (model.getStudent().hashCode() == this.hashCode()) {
                this.publications++;
            }
        }
    }
}
