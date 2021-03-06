package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    // Locals variables.
    Student student = null;
    
    public StudentService(String name, String department, Student.Degree status) {
        super(name);
        this.student = new Student(name, department, status);
    }

    public void addModelToTrain(Model model) {
        this.student.addModelToTrain(model);
    }

    public Student getStudent() {
        return this.student;
    }
    
    protected void initialize() {
        super.initialize();

        // Tick Broadcast.
        super.<TickBroadcast>subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            Future<Model> future = null;
            int cnt = 0;
            public void call(TickBroadcast c) {
                cnt++;

                // Send model to train
                if (future == null) {
                    if (!student.modelQueueEmpty()) {
                        Model model = student.getModelToTrain();
                        // System.out.println(model + " dalle");
                        future = sendEvent(new TrainModelEvent(student, model));

                        if (future == null) {
                            throw new InternalError("Sending trainModelEvent failed"  + " at " + cnt + "clocks");
                        }
                    }
                } 

                // When model has trained, test it within 
                if (future != null) {
                    if (future.isDone()) {

                        // Extract model.
                        Model model = future.get();
                        if (model.getStatus() == Model.Status.TRAINED) { // Next thing is testing,
                            future = sendEvent(new TestModelEvent(student, model));
                            if (future == null) {
                                throw new InternalError("Sending testModelEvent failed" + " at " + cnt + "clocks");
                            }

                        } else if (model.getStatus() == Model.Status.TESTED) {

                            // System.out.println(model.getName() + " " + model.getResult());
                            // Send Event.
                            if (model.getResult() == Model.Result.GOOD) {
                                future = sendEvent(new PublishResultEvent(student, future.get()));                                
                                future = null;
                            } else {
                                student.addModelThatCouldntPublish(model);
                                future = null;
                            }
                            
                        } else if (model.getStatus() == Model.Status.PRE_PUBLISHED) {
                            future = null;
                        } else if (model.getStatus() == Model.Status.PUBLISHED) { // if somehow we get a publishconferencebroadcast before the next tick
                            future = null;
                        } else {
                            throw new InternalError("model status is " + model.getStatus());
                        }
                    }
                }
            }
        });

        super.<PublishConferenceBroadcast>subscribeBroadcast(PublishConferenceBroadcast.class, new Callback<PublishConferenceBroadcast>() {
            public void call(PublishConferenceBroadcast c) {
                student.readPublication(c.getModels());
            }
        });
    }
}
