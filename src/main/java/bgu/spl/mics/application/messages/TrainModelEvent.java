package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TrainModelEvent implements Event<Model> {

    // Variables.
    private Student student;
    private Model model;

    public TrainModelEvent(Student student, Model model) {
        this.student = student;
        this.model = model;
    }

    public Student getStudent() {
        return this.student;
    }

    public Model getModel() {
        return this.model;
    }
}