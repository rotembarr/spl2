package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class PublishResultEvent implements Event<Model> {
    
    private Student student;
    private Model model;

    public PublishResultEvent (Student s,Model m){//need to be pointers to the right student/model
        this.student=s;
        this.model=m;
    }

    public Student getStudent(){
        return this.student;
    }
    public Model getModel() {
        return this.model;
    }
}