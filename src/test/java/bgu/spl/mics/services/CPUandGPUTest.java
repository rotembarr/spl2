package bgu.spl.mics.services;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.ExampleMicroService;

public class CPUandGPUTest {
    GPUService gpuService = null;
    CPUService cpuService = null;
    MessageBusImpl messageBus = null;

    @Before
    public void setUp() {
        this.gpuService = new GPUService("gpu", GPU.Type.GTX1080);
        this.cpuService = new CPUService("cpu", 8);
        this.messageBus = MessageBusImpl.getInstance();
    }

    @Test
    public void test() {
        Student student = new Student(Student.Degree.PhD);
        Model model1 = new Model("a", new Data("data-a", Data.Type.Images, 50000), student);
        Model model2 = new Model("b", new Data("data-b", Data.Type.Tabular, 10000), student);

        Thread tickThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.currentThread().sleep(10);
                    messageBus.sendBroadcast(new TickBroadcast());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Thread gpuThread = new Thread(gpuService);
        Thread cpuThread = new Thread(cpuService);

        tickThread.start();
        gpuThread.start();
        cpuThread.start();

        TrainModelEvent event1 = new TrainModelEvent(student, model1);
        TrainModelEvent event2 = new TrainModelEvent(student, model2);

        Future<Model> future1 = this.messageBus.<Model>sendEvent(event1);
        Future<Model> future2 = this.messageBus.<Model>sendEvent(event2);

        Model model1After = future1.get();
        Model model2After = future2.get();

        assertTrue("Model hasn't trained", model1After.getStatus() == Model.Status.TRAINED);
        assertTrue("Model hasn't trained", model2After.getStatus() == Model.Status.TRAINED);

    }
}
