package bgu.spl.mics.application.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

public class GPUTest {
    GPU gpu;
    Cluster cluster = null;
    Model imageModel = null;
    Model tabularModel = null;
    Model textModel = null;
    Model imageModel2 = null;
    Model textModel2 = null;


    public GPUTest() {
        this.cluster = Cluster.getInstance();
    }
    
    @Before
    public void setUp() {
        this.gpu = new GPU (GPU.Type.RTX2080);
        this.imageModel = new Model("a", new Data("Image", Data.Type.Images, 1000000), new Student(Student.Degree.MSc));
        this.tabularModel = new Model("b", new Data("Tabular", Data.Type.Tabular, 1000000), new Student(Student.Degree.MSc));
        this.textModel = new Model("c", new Data("Text", Data.Type.Text, 1000000), new Student(Student.Degree.MSc));
        this.imageModel2 = new Model("d", new Data("Image", Data.Type.Images, 1000000), new Student(Student.Degree.MSc));
        this.textModel2 = new Model("e", new Data("Text", Data.Type.Text, 1000000), new Student(Student.Degree.MSc));
        this.cluster.addGPU(gpu);
        this.cluster.clearStatistics();
        DataBatch batch = null;
        while ((batch = this.cluster.popBatchToProcess()) != null){} 
        while ((batch = this.cluster.popProcessedBatch(gpu)) != null){} 
    }
    
    @Test
    public void TestGetModelNames() {
        List<String> names = new LinkedList<String>();

        assertTrue("names vector doesnt starts empty", names.equals(this.gpu.getModelNames()));

        this.gpu.insertNewModel(imageModel);
        this.gpu.insertNewModel(tabularModel);
        this.gpu.insertNewModel(textModel);

        names.add("a");
        names.add("b");
        names.add("c");
        assertTrue("names vector error", names.equals(this.gpu.getModelNames()));

    }

    @Test
    public void TestGetNumOfTimePass() {
        // Has to be 0 at first.
        assertEquals(0, this.gpu.getNumOfTimePass());

        // advance random time.
        int n = (int)(Math.random()*(100)) + 1;
        for (int i = 0; i < n; i++) {
            this.gpu.tickSystem();
        }
        assertEquals(n, this.gpu.getNumOfTimePass());
    }

    @Test
    public void TestTestResult_NullArg() {
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.testModel(null);});
    }

    @Test
    public void TestTestResult_NotTrainedModel() {
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.testModel(this.imageModel);});
    }

    @Test
    public void TestTestResult_batch() {
        for (int i = 0; i < 100; i++) {
            Model model = new Model("a", new Data("Image", Data.Type.Images, 1000000), new Student(Student.Degree.MSc));
            this.gpu.insertNewModel(model);
            while(!model.getData().isTrainingFinished()) {
                DataBatch batch = model.getData().createBatch(gpu);
                batch.setAsProcessed();
                batch.setAsTrained();
            }
            this.gpu.doneTrainingModel(model);
            this.gpu.testModel(model);
            assertFalse("bad test", (model.getResult() == Model.Result.NONE) || model.getStatus() != Model.Status.TESTED) ;
        }
    }

    @Test
    public void TestInsertNewModel_NullArg() { 
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.insertNewModel(null);});
    }

    @Test
    public void TestInsertNewModel_TwiceSameModel() {
        this.gpu.insertNewModel(this.imageModel);
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.insertNewModel(this.imageModel);});
    }
    
    @Test
    public void TestInsertNewModel_batch() {
        for (int i = 0; i < 300; i++) {
            Model model = new Model("a", new Data("Image", Data.Type.Images, 1000000), new Student(Student.Degree.MSc));
            this.gpu.insertNewModel(model);
        }
        assertEquals("Insert models failure", 300, this.gpu.getModelsQueueSize());
    }

    @Test
    public void TestSendBatchToProcess_NullArg() {
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.sendBatchToProcess(null);});
    }

    @Test
    public void TestSendBatchToProcess_ProcessedBatch() {
        DataBatch batch = this.imageModel.getData().createBatch(this.gpu);
        batch.setAsProcessed();
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.sendBatchToProcess(batch);});
    }


    @Test
    public void TestSendBatchToProcess_Base() {
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());
        DataBatch batch = this.imageModel.getData().createBatch(this.gpu);
        this.gpu.sendBatchToProcess(batch);
        assertEquals("bad numbers of batches in cluster", 1, this.cluster.getNumOfBatchesWaitingToProcess());

        for (int i = 0; i < 99; i++) {
            batch = this.imageModel.getData().createBatch(this.gpu);
            this.gpu.sendBatchToProcess(batch);
        }
        assertEquals("bad numbers of batches in cluster", 100, this.cluster.getNumOfBatchesWaitingToProcess());

    }

    @Test
    public void TestTryToFetchProcessedBatch_Base() {
        DataBatch batch = null;
        assertEquals("Somehow processed batches sent to cluster when not need", null, this.gpu.tryToFetchProcessedBatch());
        
        
        batch = this.imageModel.getData().createBatch(this.gpu);
        batch.setAsProcessed();
        this.cluster.pushProcessedBatch(batch);
        assertEquals("Wring fetched processed batch", batch, this.gpu.tryToFetchProcessedBatch());
        assertEquals("Somehow processed batches sent to cluster when not need", null, this.gpu.tryToFetchProcessedBatch());
    }


    @Test
    public void TestTryToFetchProcessedBatch_Burst() {
        DataBatch batch = null;
        Queue<DataBatch> queue = new LinkedList<DataBatch>();

        assertEquals("Somehow processed batches sent to cluster when not need", null, this.gpu.tryToFetchProcessedBatch());
        
        for (int i = 0; i< GPU.MAX_PROCESSED_DATA_BATCH_STORED; i++) {
            batch = this.imageModel.getData().createBatch(this.gpu);
            batch.setAsProcessed();
            queue.add(batch);
            this.cluster.pushProcessedBatch(batch);
        }
        
        for (int i = 0; i< GPU.MAX_PROCESSED_DATA_BATCH_STORED; i++) {
            DataBatch b = queue.poll();
            assertEquals("Wrong fetched processed batch", b, this.gpu.tryToFetchProcessedBatch());
        }
        assertEquals("Wrong fetched processed batch", null, this.gpu.tryToFetchProcessedBatch());        
    }    
    
    // Function for test use
    private void trainBatch() {
        DataBatch batch = this.cluster.popBatchToProcess();
        batch.setAsProcessed();
        this.cluster.pushProcessedBatch(batch);
        assertTrue("couldnt fetch batch", batch == this.gpu.tryToFetchProcessedBatch());
        assertFalse("Trainig executed when not needed", this.gpu.isTraining());
        this.gpu.startTrainBatch(batch);   
        assertTrue("Trainig not executed", this.gpu.isTraining());
        this.gpu.finalizeTrainBatch(batch);
        assertFalse("Trainig still executed", this.gpu.isTraining());
    }

    @Test
    public void TestStartTrainBatchExcep() {
        GPU badGpu = new GPU(GPU.Type.GTX1080);
        DataBatch badGPUBatch = this.imageModel.getData().createBatch(badGpu);
        DataBatch okBatch = this.imageModel.getData().createBatch(this.gpu);

        assertThrows(IllegalArgumentException.class, () -> {this.gpu.startTrainBatch(null);});
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.startTrainBatch(badGPUBatch);});
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.startTrainBatch(okBatch);}); // havnet processed
        
        
        assertFalse("train shouldn't be start", this.gpu.isTraining());
        okBatch.setAsProcessed();
        this.gpu.startTrainBatch(okBatch); // This call is ok.
        assertTrue("train should have start", this.gpu.isTraining());
        
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.startTrainBatch(okBatch);});
    }

    @Test
    public void TestFinalizeTrainBatchExcep() {
        GPU badGpu = new GPU(GPU.Type.GTX1080);
        DataBatch badGPUBatch = this.imageModel.getData().createBatch(badGpu);
        DataBatch otherBatch = this.imageModel.getData().createBatch(this.gpu);
        DataBatch okBatch = this.imageModel.getData().createBatch(this.gpu);

        assertThrows(IllegalArgumentException.class, () -> {this.gpu.finalizeTrainBatch(null);});
        
        badGPUBatch.setAsProcessed();
        // this.gpu.startTrainBatch(badGPUBatch); should be here but this function already throhs exception.
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.finalizeTrainBatch(badGPUBatch);});


        otherBatch.setAsProcessed();
        otherBatch.setAsTrained();
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.finalizeTrainBatch(otherBatch);}); // check when GPU isn't training.

        assertFalse("train shouldn't be start", this.gpu.isTraining());
        okBatch.setAsProcessed();
        this.gpu.startTrainBatch(okBatch);
        assertTrue("train should have start", this.gpu.isTraining());
        this.gpu.finalizeTrainBatch(okBatch); // This call is ok.
        assertFalse("train should done", this.gpu.isTraining());
        
        assertThrows(IllegalArgumentException.class, () -> {this.gpu.finalizeTrainBatch(okBatch);});
    }

    // fragmentizeBatchesToProcess
    @Test
    public void TestFragmentizeBatchesToProcess_SingleModel() {
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());

        this.gpu.insertNewModel(this.imageModel);

        this.gpu.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", this.gpu.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        
        this.trainBatch();
        this.trainBatch();
        this.gpu.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", this.gpu.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.gpu.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", this.gpu.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());

        while (!this.imageModel.getData().isFragmantationFinished()) {
            this.trainBatch();
            this.gpu.fragmentizeBatchesToProcess();
            assertEquals("bad numbers of batches in cluster", this.gpu.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        } 

        int cnt = this.gpu.vRAMsizeInBatches();
        while (!this.imageModel.getData().isProcessingFinished()) {
            this.trainBatch();
            cnt--;
            this.gpu.fragmentizeBatchesToProcess();
            assertEquals("bad numbers of batches in cluster", cnt, this.cluster.getNumOfBatchesWaitingToProcess());
        } 

        assertEquals("bad numbers of batches in cluster", 0, this.cluster.getNumOfBatchesWaitingToProcess());
    }

    @Test
    public void TestFragmentizeBatchesToProcess_MultipleModel() {
        Queue<Model> queue = new LinkedList<Model>();

        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());        
        
        queue.add(this.imageModel);
        queue.add(this.tabularModel);
        queue.add(this.textModel);
        queue.add(this.imageModel2);
        this.gpu.insertNewModel(this.imageModel);
        this.gpu.insertNewModel(this.tabularModel);
        this.gpu.insertNewModel(this.textModel);
        this.gpu.insertNewModel(this.imageModel2);

        this.gpu.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", this.gpu.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        
        for (int i = 0; i < 4; i++) {
            while (queue.peek().getData().isFragmantationFinished()) {
                int n = (int)(Math.random()*(100)) + 1;
                if (n < 70) {
                    int k = (int)(Math.random()*(10)) + 1;
                    for (int j = 0; j < k; j++) {
                        this.trainBatch();
                    }
                }                
                this.gpu.fragmentizeBatchesToProcess();
                assertEquals("bad numbers of batches in cluster", this.gpu.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
            } 
        }
    }

    // TODO add test of hot swap in fragment
    @Test
    public void TestTickSystem_Sanity() {
        Queue<Model> queue = new LinkedList<Model>();
        CPU cpu = new CPU(32);

        queue.add(this.textModel);
        this.gpu.insertNewModel(this.textModel);

        // latency
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();

        for (int i = 0; i < 1000; i++) {
            this.gpu.tickSystem(); // Should train 500 batches in 1000 clocks.
            cpu.tickSystem(); // Should process 1000 batches in 1000 clocks.
        }

        assertEquals("Test failed", 500, this.gpu.getNumOfTrainedBatches());

    }

    @Test
    public void TestTickSystem_2Models() { 
        Queue<Model> queue = new LinkedList<Model>();
        CPU cpu = new CPU(32);

        queue.add(this.textModel);
        queue.add(this.textModel2);
        this.gpu.insertNewModel(this.textModel);
        this.gpu.insertNewModel(this.textModel2);

        // latency
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();
        this.gpu.tickSystem();
        cpu.tickSystem();

        for (int i = 0; i < 3000; i++) {
            this.gpu.tickSystem(); // Should train 1500 batches in 1000 clocks.
            cpu.tickSystem(); // Should process 3000 batches in 1000 clocks.
        }

        assertEquals("Test failed", 1500, this.gpu.getNumOfTrainedBatches());
    }


    // @Test
    public void TestTickSystem_testThrougput() {
        Queue<Model> queue = new LinkedList<Model>();
        Object object = new Object();
        final Boolean[] waitExep = {false,false};

        queue.add(this.imageModel);
        queue.add(this.tabularModel);
        queue.add(this.textModel);
        queue.add(this.imageModel2);
        this.gpu.insertNewModel(this.imageModel);
        this.gpu.insertNewModel(this.tabularModel);
        this.gpu.insertNewModel(this.textModel);
        this.gpu.insertNewModel(this.imageModel2);

        Thread cpuThread = new Thread(()-> {
            CPU cpu = new CPU();

            while (true) {
                try {
                    object.wait();
                } catch (Exception e) {
                    waitExep[0] = true;
                }
                cpu.tickSystem();
            }
        });

        Thread tickThread = new Thread(()-> {
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    waitExep[1] = true;
                }
                object.notifyAll();
            }
        });
        Thread gpuThread = new Thread(()-> {
            while (true) {
                try {
                    object.wait();
                } catch (Exception e) {
                    waitExep[0] = true;
                }
                this.gpu.tickSystem();
            }
        });
        
        cpuThread.start();
        gpuThread.start();
        tickThread.start();

        // Wait enough time
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            assertEquals("sleep failed", true, false);
        }

        if(waitExep[0] == true || waitExep[1] == true) {
            try {
                cpuThread.join();
                gpuThread.join();
                tickThread.join();
            } catch (Exception e) {
                assertEquals("sleep failed", true, false);
            }
            assertTrue("waiting machanizem failed", false);
        } else {
            cpuThread.interrupt();
            gpuThread.interrupt();
            tickThread.interrupt();
        }



        assertEquals("Test failed", 5000, this.gpu.getNumOfTrainedBatches());

    }
}
