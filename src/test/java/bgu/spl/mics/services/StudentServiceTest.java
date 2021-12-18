package bgu.spl.mics.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model.Status;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.example.ExampleMicroService;

public class StudentServiceTest {
    
    MessageBusImpl messageBus = null;

    public class DemeTrainMicroService extends MicroService {
        public int numOfMessages = 0;

        public int getMsgCnt() {
            return this.numOfMessages;
        }
        
        public DemeTrainMicroService(String name) {
            super(name);
        }

        protected void initialize() {
            super.initialize();

            subscribeEvent(TrainModelEvent.class, new Callback<TrainModelEvent>() {
                @Override
                public void call(TrainModelEvent c) {
                    c.getModel().setStatus(Model.Status.TRAINED);
                    complete(c, c.getModel());
                    numOfMessages++;
                }
            });
        };
    };

    public class DemeTestMicroService extends MicroService {
        public int numOfMessages = 0;
        
        public int getMsgCnt() {
            return this.numOfMessages;
        }
        
        public DemeTestMicroService(String name) {
            super(name);
        }

        protected void initialize() {
            super.initialize();

            subscribeEvent(TestModelEvent.class, new Callback<TestModelEvent>() {
                @Override
                public void call(TestModelEvent c) {
                    c.getModel().setStatus(Model.Status.TESTED);

                    
                    // Testing (pay attention to <).
                    int chance = 50;
                    boolean answer = ((int)(Math.random() * 100)) < chance ? true : false;                    
                    if (answer) {
                        c.getModel().setResult(Model.Result.GOOD);
                    } else {
                        c.getModel().setResult(Model.Result.BAD);
                    }
                    c.getModel().setStatus(Model.Status.TESTED);
            
                    complete(c, c.getModel());
                    numOfMessages++;
                }
            });
        };
    };


    public class DemePublishMicroService extends MicroService {
        public int numOfMessages = 0;
        
        public int getMsgCnt() {
            return this.numOfMessages;
        }

        public DemePublishMicroService(String name) {
            super(name);
        }

        protected void initialize() {
            super.initialize();

            subscribeEvent(PublishResultEvent.class, new Callback<PublishResultEvent>() {
                @Override
                public void call(PublishResultEvent c) {
                    c.getModel().setStatus(Model.Status.PRE_PUBLISHED);
                    complete(c, c.getModel());
                    List<Model> modelsToPublish = new LinkedList<Model>();
                    modelsToPublish.add(c.getModel());
                    sendBroadcast(new PublishConferenceBroadcast(modelsToPublish));
                    numOfMessages++;
                }
            });
        };
    };

    @Before
    public void setUp() {
        this.messageBus = MessageBusImpl.getInstance();
    }

    public void testCore(int nStudents, int nMessages, int milisec) {
       
        List<StudentService> studentServiceArr = new LinkedList<StudentService>();
        for (int i = 0; i < nStudents; i++) {
            StudentService studentService = new StudentService("rotem", "cs", Student.Degree.PhD);
            
            for (int j = 0; j < nMessages; j++) {
                if (j%3 == 0)
                    studentService.addModelToTrain(new Model("model_" + Integer.toString(j),   new Data("data_" + Integer.toString(j), Data.Type.Images, 10000), studentService.getStudent()));
                else if (j%3 == 1)
                    studentService.addModelToTrain(new Model("model_" + Integer.toString(j), new Data("data_" + Integer.toString(j), Data.Type.Text, 10000), studentService.getStudent()));
                else
                    studentService.addModelToTrain(new Model("model_" + Integer.toString(j), new Data("data_" + Integer.toString(j), Data.Type.Tabular, 10000), studentService.getStudent()));
            }
            studentServiceArr.add(studentService);            
        }

        List<Thread> studentThreadArr = new LinkedList<Thread>();
        for (int i = 0; i < studentServiceArr.size(); i++) {
            Thread studentThread = new Thread(studentServiceArr.get(i));
            studentThreadArr.add(studentThread); 
            studentThread.start();
        }

        
        // Validation.
        DemeTrainMicroService trainService = new DemeTrainMicroService("train");
        DemeTestMicroService testService = new DemeTestMicroService("test");
        DemePublishMicroService publishService = new DemePublishMicroService("publish");
        Thread trainThread = new Thread(trainService);
        Thread testThread = new Thread(testService);
        Thread publishThread = new Thread(publishService);
        trainThread.start();
        testThread.start();
        publishThread.start();
        
        // Tick.
        TimeService timeService = new TimeService("time", milisec, nStudents*nMessages);
        Thread tickThread = new Thread(timeService);
        tickThread.start();
        
        try {
            tickThread.join();
            for(int i = 0; i < studentThreadArr.size(); i++) {
                studentThreadArr.get(i).join();
            }
            assertEquals(nMessages*nStudents, trainService.getMsgCnt());
            assertEquals(nMessages*nStudents, testService.getMsgCnt());

            int cnt = 0;
            for (int i = 0; i < studentServiceArr.size(); i++) {
                cnt += studentServiceArr.get(i).getStudent().getPublications();
            }
            assertEquals(cnt, publishService.getMsgCnt());
            
            for (int i = 0; i < studentServiceArr.size(); i++) {
                int cnt2 = studentServiceArr.get(i).getStudent().getPaperRead() + studentServiceArr.get(i).getStudent().getPublications();
                assertEquals(cnt2, publishService.getMsgCnt());
            }
        } catch (Exception e) {
            e.printStackTrace();;
        }

    }

    // @Test TODO
    // public void test_1Student() {
    //     testCore(1, 100, 1);
    // }
    @Test
    public void test_100Student() {
        testCore(30, 100, 8);
    }
}
