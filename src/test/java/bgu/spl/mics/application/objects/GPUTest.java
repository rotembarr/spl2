package bgu.spl.mics.application.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;
import org.junit.runner.notification.RunListener.ThreadSafe;

public class GPUTest extends GPU{
    Cluster cluster = null;


    public GPUTest() {
        super(GPU.Type.RTX2080);
        this.cluster = new Cluster();
    }

    
    @Test
    public void TestGetModelNames() {
        Queue<String> names = new LinkedList<String>();

        assertTrue("names vector doesnt starts empty", names == super.getModelNames());

        super.insertNewModel(new Model("a", new Data(Data.Type.Images), new Student()));
        super.insertNewModel(new Model("b", new Data(Data.Type.Images), new Student()));
        super.insertNewModel(new Model("c", new Data(Data.Type.Images), new Student()));

        names.add("a");
        names.add("b");
        names.add("c");
        assertTrue("names vector error", names.equals(getModelNames()));

    }

    @Test
    public void TestGetNumOfTimePass() {
        // Has to be 0 at first.
        assertEquals(0, super.getNumOfTimePass());

        // advance random time.
        int n = (int)(Math.random()*(100)) + 1;
        for (int i = 0; i < n; i++) {
            super.tickSystem();
        }
        assertEquals(n, super.getNumOfTimePass());
    }

    @Test
    public void TestTestResult_NullArg() {
        assertThrows(IllegalArgumentException.class, () -> {super.testResult(null);});
    }

    @Test(expected = Exception.class)
    public void TestTestResult_NotTrainedModel() {
        Model model = new Model("a", new Data(Data.Type.Images), new Student());
        assertThrows(IllegalArgumentException.class, () -> {super.testResult(model);});
    }

    @Test
    public void TestTestResult_batch() {
        for (int i = 1; i < 300; i++) {
            Model model = new Model("a", new Data(Data.Type.Images), new Student());
            super.insertNewModel(model);
            super.doneTrainingModel(model);
            Model.Result result = super.testResult(model);
            assertTrue("bad test", (result == Model.Result.GOOD) || result == Model.Result.BAD); ;
        }
    }

    @Test
    public void TestInsertNewModel_NullArg() { 
        assertThrows(IllegalArgumentException.class, () -> {super.insertNewModel(null);});
    }

    @Test
    public void TestInsertNewModel_TwiceTrainedModel() {
        Model model = new Model("a", new Data(Data.Type.Images), new Student());        
        super.insertNewModel(model);
        super.doneTrainingModel(model);
        assertThrows(IllegalArgumentException.class, () -> {super.doneTrainingModel(model);});
    }
    
    @Test
    public void TestInsertNewModel_batch() {
        for (int i = 1; i < 300; i++) {
            Model model = new Model("a", new Data(Data.Type.Images), new Student());
            super.insertNewModel(model);
        }
        assertEquals("Insert models failure", 300, super.getModelsQueueSize());
    }

    @Test
    public void TestSendBatchToProcess_NullArg() {
        assertThrows(IllegalArgumentException.class, () -> {super.sendBatchToProcess(null);});
    }

    @Test
    public void TestSendBatchToProcess_ProcessedBatch() {
        DataBatch batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
        batch.setAsProcessed();
        assertThrows(IllegalArgumentException.class, () -> {super.sendBatchToProcess(batch);});
    }


    @Test
    public void TestSendBatchToProcess_Base() {
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());
        DataBatch batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
        super.sendBatchToProcess(batch);
        assertEquals("bad numbers of batches in cluster", 1, this.cluster.getNumOfBatchesWaitingToProcess());

        for (int i = 0; i < 99; i++) {
            batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
            super.sendBatchToProcess(batch);
        }
        assertEquals("bad numbers of batches in cluster", 100, this.cluster.getNumOfBatchesWaitingToProcess());

    }

    @Test
    public void TestTryToFetchProcessedBatch_Base() {
        DataBatch batch = null;
        assertEquals("Somehow processed batches sent to cluster when not need", null, super.tryToFetchProcessedBatch());
        
        
        batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
        batch.setAsProcessed();
        this.cluster.pushProcessedBatch(batch);
        assertEquals("Wring fetched processed batch", batch, super.tryToFetchProcessedBatch());
        assertEquals("Somehow processed batches sent to cluster when not need", null, super.tryToFetchProcessedBatch());
    }


    @Test
    public void TestTryToFetchProcessedBatch_Burst() {
        DataBatch batch = null;
        Queue<DataBatch> queue = new LinkedList<DataBatch>();

        assertEquals("Somehow processed batches sent to cluster when not need", null, super.tryToFetchProcessedBatch());
        
        for (int i = 0; i< 100; i++) {
            batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
            batch.setAsProcessed();
            queue.add(batch);
            this.cluster.pushProcessedBatch(batch);
        }
        
        for (int i = 0; i< 100; i++) {
            DataBatch b = queue.poll();
            assertEquals("Wrong fetched processed batch", b, super.tryToFetchProcessedBatch());
            super.finalizeTrainBatch(b);
        }
        assertEquals("Wrong fetched processed batch", null, super.tryToFetchProcessedBatch());        
    }    
    
    // Function for test use
    private void trainBatch() {
        DataBatch batch = this.cluster.popBatchToProcess();
        batch.setAsProcessed();
        assertFalse("Trainig executed when not needed", super.isTraining());
        super.startTrainBatch(batch);   
        assertTrue("Trainig not executed", super.isTraining());
        super.finalizeTrainBatch(batch);
        assertFalse("Trainig still executed", super.isTraining());
    }

    @Test
    public void TestStartTrainBatchExcep() {
        DataBatch badGPUBatch = new DataBatch(new GPU(GPU.Type.GTX1080), new Data(Data.Type.Tabular), 9);
        DataBatch okBatch = new DataBatch(this, new Data(Data.Type.Tabular), 9);

        assertThrows(IllegalArgumentException.class, () -> {super.startTrainBatch(null);});
        assertThrows(IllegalArgumentException.class, () -> {super.startTrainBatch(badGPUBatch);});
        assertThrows(IllegalArgumentException.class, () -> {super.startTrainBatch(okBatch);}); // havnet processed
        
        assertFalse("train shouldn't be start", super.isTraining());
        okBatch.setAsProcessed();
        super.startTrainBatch(okBatch); // This call is ok.
        assertTrue("train should have start", super.isTraining());
        
        assertThrows(IllegalArgumentException.class, () -> {super.startTrainBatch(okBatch);});
    }

    @Test
    public void TestFinalizeTrainBatchExcep() {
        DataBatch badGPUBatch = new DataBatch(new GPU(GPU.Type.GTX1080), new Data(Data.Type.Tabular), 9);
        DataBatch otherBatch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
        DataBatch okBatch = new DataBatch(this, new Data(Data.Type.Tabular), 9);

        assertThrows(IllegalArgumentException.class, () -> {super.finalizeTrainBatch(null);});
        
        badGPUBatch.setAsProcessed();
        // super.startTrainBatch(badGPUBatch); should be here but this function already throhs exception.
        assertThrows(IllegalArgumentException.class, () -> {super.finalizeTrainBatch(badGPUBatch);});


        otherBatch.setAsProcessed();
        otherBatch.setAsTrained();
        assertThrows(IllegalArgumentException.class, () -> {super.finalizeTrainBatch(otherBatch);}); // check when GPU isn't training.

        assertFalse("train shouldn't be start", super.isTraining());
        super.startTrainBatch(okBatch);
        assertTrue("train should have start", super.isTraining());
        super.finalizeTrainBatch(okBatch); // This call is ok.
        assertFalse("train should done", super.isTraining());
        
        assertThrows(IllegalArgumentException.class, () -> {super.finalizeTrainBatch(okBatch);});
    }

    // fragmentizeBatchesToProcess
    @Test
    public void TestFragmentizeBatchesToProcess_SingleModel() {
        assertEquals("function havent implemented so test stuck. TODO - delete",false,true);
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());

        Model model = new Model("a", new Data(Data.Type.Images), new Student());
        super.insertNewModel(model);

        super.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        
        this.trainBatch();
        this.trainBatch();
        super.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        this.trainBatch();
        super.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());

        while (!model.getData().isFragmantationFinished()) {
            this.trainBatch();
            super.fragmentizeBatchesToProcess();
            assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
        } 

        int cnt = super.vRAMsizeInBatches();
        while (!model.getData().isProcessingFinished()) {
            this.trainBatch();
            cnt--;
            super.fragmentizeBatchesToProcess();
            assertEquals("bad numbers of batches in cluster", cnt, this.cluster.getNumOfBatchesWaitingToProcess());
        } 

        assertEquals("bad numbers of batches in cluster", 0, this.cluster.getNumOfBatchesWaitingToProcess());
    }

    @Test
    public void TestFragmentizeBatchesToProcess_MultipleModel() {
        Queue<Model> queue = new LinkedList<Model>();

        assertEquals("function havent implemented so test stuck. TODO - delete",false,true);
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());
        for (int i = 0; i < 5; i++) {
            Model model = new Model("a", new Data(Data.Type.Images), new Student());
            queue.add(model);
            super.insertNewModel(model);
        }

        super.fragmentizeBatchesToProcess();
        assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());        
        
        for (int i = 0; i < 5; i++) {
            while (queue.peek().getData().isFragmantationFinished()) {
                int n = (int)(Math.random()*(100)) + 1;
                if (n < 70) {
                    int k = (int)(Math.random()*(10)) + 1;
                    for (int j = 0; j < k; j++) {
                        this.trainBatch();
                    }
                }                
                super.fragmentizeBatchesToProcess();
                assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
            } 
        }
    }

    @Test
    public void TestTickSystem_testThrougput() {
        Queue<Model> queue = new LinkedList<Model>();
        Object object = new Object();
        final Boolean[] waitExep = {false,false};

        for (int i = 0; i < 5; i++) {
            Model model = new Model("a", new Data(Data.Type.Images), new Student());
            queue.add(model);
            super.insertNewModel(model);
        }

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
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    waitExep[1] = true;
                }
                object.notify();
            }
        });
        Thread gpuThread = new Thread(()-> {
            while (true) {
                try {
                    object.wait();
                } catch (Exception e) {
                    waitExep[0] = true;
                }
                super.tickSystem();
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



        assertEquals("Test failed", 5000, super.getNumOfTrainedBatches());

    }
}
