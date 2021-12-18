package bgu.spl.mics.application.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.Excluder;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    // Types.
    public enum Status {PRE_TRAINED, TRAINING, TRAINED, TESTED, PRE_PUBLISHED, PUBLISHED}
    public enum Result {NONE, GOOD, BAD}

    // Variables.
    @Expose(serialize = true, deserialize = true)
    private String name; // Name of the model.
    @Expose(serialize = true, deserialize = true)
    private Status status;     
    @Expose(serialize = true, deserialize = true)
    private Data data; // The data the model should train on.
    @Expose(serialize = true, deserialize = true)
    private Result result;
    
    @Expose(serialize = false, deserialize = false)
    private Student student; // The student which created the model.
    

    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = Status.PRE_TRAINED;
        this.result = Result.NONE;
    }
    /**
     * @pre none
     * @post this.getStatus=status
     */
    public void setStatus(Status status) {
        this.status = status;
    }
    public Status getStatus() {
        return this.status;
    }

    public Data getData() {
        return this.data;
    }
    /**
     * @pre none
     * @post this.getResult=result
     */
    public void setResult(Result result) {
        this.result = result;
    }
    public Result getResult() {
        return this.result;
    }

    public Student getStudent() {
        return this.student;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
