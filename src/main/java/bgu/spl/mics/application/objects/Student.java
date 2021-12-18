package bgu.spl.mics.application.objects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;

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
    @Expose(serialize = true, deserialize = true)
    private String name;
    @Expose(serialize = true, deserialize = true)
    private String department;
    @Expose(serialize = true, deserialize = true)
    private Degree status;
    
    // Statistics.
    @Expose(serialize = true, deserialize = true)
    private int publications;
    @Expose(serialize = true, deserialize = true)
    private int papersRead;
    
    // Train event variables.
    @Expose(serialize = true, deserialize = true)
    private List<Model> modelsToTrain;
    @Expose(serialize = true, deserialize = true)
    private List<Model> publishedModels;
    @Expose(serialize = true, deserialize = true)
    private List<Model> unpublishedTestedModels;

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
        this.publishedModels = new LinkedList<Model>(); 
        this.unpublishedTestedModels = new LinkedList<Model>(); 
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
    /**
     * @pre none
     * @post this.modelsToTrain= pre this.modelsToTrain+1
     */
    public void addModelToTrain(Model model) {
        this.modelsToTrain.add(model);
    }
    /**
     * @pre none
     * @post this.modelsToTrain= pre this.modelsToTrain-1
     */
    public Model getModelToTrain() {
        return this.modelsToTrain.remove(0);
    }
    /**
     * @pre none
     * @post this.unpublishedTestedModels= pre this.unpublishedTestedModels+1
     */
    public void addModelThatCouldntPublish(Model model) {
        this.unpublishedTestedModels.add(model);
    }
    
    public void readPublication(List<Model> publishModels) {
        for (Iterator<Model> iter = publishModels.iterator(); iter.hasNext();) {
            Model model = iter.next();
            
            // If we sent this publication, advance counter.
            if (model.getStudent().hashCode() == this.hashCode()) {
                
                model.setStatus(Model.Status.PUBLISHED);
                this.publishedModels.add(model);
                this.publications++;

            // Model of other student read.
            } else {
                this.papersRead++;
            }
            
        }
    }
}
