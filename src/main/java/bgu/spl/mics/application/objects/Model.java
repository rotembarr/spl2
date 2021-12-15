package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    // Types.
    public enum Status {PRE_TRAINED, TRAINING, TRAINED, TESTED, PUBLISHED}
    public enum Result {NONE, GOOD, BAD}

    // Variables.
    private String name; // Name of the model.
    private Data data; // The data the odel should train on.
    private Student student; // The student which created the model.
    private Status status;
    private Result result;

    public Model(String name, Data data, Student student) {
        this.name = name;
        this.data = data;
        this.student = student;
        this.status = Status.PRE_TRAINED;
        this.result = Result.NONE;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public Status getStatus() {
        return this.status;
    }

    public Data getData() {
        return this.data;
    }

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
