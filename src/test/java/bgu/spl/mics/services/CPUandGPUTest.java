package bgu.spl.mics.services;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.GPUService;

public class CPUandGPUTest {
    MessageBusImpl messageBus = null;
    
    @Before
    public void setUp() {
        System.out.println("Cleaning messageBus");
        this.messageBus = MessageBusImpl.getInstance();
        this.messageBus.clean();
    }

    public void testCore(int nCpus, int nGpus, int nMessages, int milisec) {
        List<GPUService> gpuServiceArr = new LinkedList<GPUService>();
        List<CPUService> cpuServiceArr = new LinkedList<CPUService>();

        // Services
        for (int i = 0; i < nGpus; i++) {
            if (i%3 == 0)
                gpuServiceArr.add(new GPUService("gpu_" + Integer.toString(i), GPU.Type.GTX1080));
            else if (i%3 == 1)
                gpuServiceArr.add(new GPUService("gpu_" + Integer.toString(i), GPU.Type.RTX2080));
            else
                gpuServiceArr.add(new GPUService("gpu_" + Integer.toString(i), GPU.Type.RTX3090));
        }
        for (int i = 0; i < nCpus; i++) {
            if (i%6 == 0)
                cpuServiceArr.add(new CPUService("cpu_" + Integer.toString(i), 1));
            else if (i%6 == 1)
                cpuServiceArr.add(new CPUService("cpu_" + Integer.toString(i), 2));
            else if (i%6 == 2)
                cpuServiceArr.add(new CPUService("cpu_" + Integer.toString(i), 4));
            else if (i%6 == 3)
                cpuServiceArr.add(new CPUService("cpu_" + Integer.toString(i), 8));
            else if (i%6 == 4)
                cpuServiceArr.add(new CPUService("cpu_" + Integer.toString(i), 16));
            else
                cpuServiceArr.add(new CPUService("cpu_" + Integer.toString(i), 32));
        }
        
        
        List<Thread> gpuThreads = new LinkedList<>();
        for (int i = 0; i < gpuServiceArr.size(); i++) {
            Thread gpuThread = new Thread(gpuServiceArr.get(i));
            gpuThreads.add(gpuThread);
            gpuThread.start();
        }
        
        List<Thread> cpuThreads = new LinkedList<>();
        for (int i = 0; i < cpuServiceArr.size(); i++) {
            Thread cpuThread = new Thread(cpuServiceArr.get(i));
            cpuThreads.add(cpuThread);
            cpuThread.start();
        }

        // Tick.
        Thread tickThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.currentThread().sleep(milisec);
                    messageBus.sendBroadcast(new TickBroadcast());
                } catch (Exception e) {
                    e.printStackTrace();
                }    
            }    
        });            

        tickThread.start();


        // Sllep in order to let service regitser.
        try {
            Thread.currentThread().sleep(20);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Data
        Student student = new Student(Student.Degree.PhD);
        List<Model> modelArr = new LinkedList<Model>();
        List<TrainModelEvent> eventArr = new LinkedList<TrainModelEvent>();
        List<Future<Model>> futureArr = new LinkedList<Future<Model>>();
        for (int i = 0; i < nMessages; i++) {
            if (i%3 == 0)
                modelArr.add(new Model("model_" + Integer.toString(i),   new Data("data_" + Integer.toString(i), Data.Type.Images, 10000), student));
            else if (i%3 == 1)
                modelArr.add(new Model("model_" + Integer.toString(i), new Data("data_" + Integer.toString(i), Data.Type.Text, 10000), student));
            else
                modelArr.add(new Model("model_" + Integer.toString(i), new Data("data_" + Integer.toString(i), Data.Type.Tabular, 10000), student));
        }

        for (int i = 0; i < modelArr.size(); i++) {
            TrainModelEvent event = new TrainModelEvent(student, modelArr.get(i));

            Future<Model> future = this.messageBus.<Model>sendEvent(event);
            assertNotEquals("Future is null", null, future);
            futureArr.add(future);
        }

        // Result
        for (int i = 0; i < futureArr.size(); i++) {
            Model modelAfter = futureArr.get(i).get();
            // System.out.println("aa ");

            assertTrue("Model hasn't trained good", modelAfter.getStatus() == Model.Status.TRAINED);
        }

        this.messageBus.sendBroadcast(new TerminateBroadcast());
        try {
            for (int i = 0; i < gpuThreads.size(); i++) {
                gpuThreads.get(i).join();
            }
            for (int i = 0; i < cpuThreads.size(); i++) {
                cpuThreads.get(i).join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test__oneGPU_oneCPU() {
        System.out.println("running test__oneGPU_oneCPU");
        this.testCore(1,1,100, 1);
    }

    @Test
    public void test__5GPU_1CPU() {
        System.out.println("running test__5GPU_1CPU");
        this.testCore(1, 5, 100, 5);
    }

    @Test
    public void test__1GPU_5CPU() {
        System.out.println("running test__1GPU_5CPU");
        this.testCore(5, 1, 100, 1);
    }
    
    @Test
    public void test__8GPU_8CPU() {
        System.out.println("running test__8GPU_8CPU");
        this.testCore(8, 8, 700, 1);
    }

    @Test
    public void test__20GPU_40CPU() {
        System.out.println("running test__20GPU_40CPU");
        this.testCore(20, 40, 150, 5);
    }
}
