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
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Model.Status;
import bgu.spl.mics.application.services.StudentService;
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
                    c.getModel().setStatus(Model.Status.PUBLISHED);
                    complete(c, c.getModel());
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

        for (int i = 0; i < studentServiceArr.size(); i++) {
            Thread studentThread = new Thread(studentServiceArr.get(i));
            studentThread.start();
        }

        // Tick.
        Thread tickThread = new Thread(() -> {
            for (int i =0; i < 10 *nMessages; i++) {
                try {
                    Thread.currentThread().sleep(milisec);
                    messageBus.sendBroadcast(new TickBroadcast());
                } catch (Exception e) {
                    e.printStackTrace();
                }    
            }    
        });            
        tickThread.start();
        

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

        try {
            tickThread.join();
            assertEquals(nMessages*nStudents, trainService.getMsgCnt());
            assertEquals(nMessages*nStudents, testService.getMsgCnt());
            assertEquals(nMessages*nStudents, publishService.getMsgCnt());
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
        testCore(100, 100, 1);
    }
}
