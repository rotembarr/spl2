package bgu.spl.mics.application.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

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
        assertTrue("names vector error", names == super.getModelNames());

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

    @Test(expected = Exception.class)
    public void TestTestResult_NullArg() {
        super.testResult(null);
    }

    @Test(expected = Exception.class)
    public void TestTestResult_NotTrainedModel() {
        Model model = new Model("a", new Data(Data.Type.Images), new Student());
        super.testResult(model);
    }

    @Test
    public void TestTestResult_batch() {
        for (int i = 1; i < 300; i++) {
            Model model = new Model("a", new Data(Data.Type.Images), new Student());
            super.insertNewModel(model);
            super.doneTrainingModel(model);
            super.testResult(model);
        }
    }

    @Test(expected = Exception.class)
    public void TestInsertNewModel_NullArg() { 
        super.insertNewModel(null);
    }

    @Test(expected = Exception.class)
    public void TestInsertNewModel_TwiceTrainedModel() {
        Model model = new Model("a", new Data(Data.Type.Images), new Student());        
        super.insertNewModel(model);
        super.doneTrainingModel(model);
        super.doneTrainingModel(model);
    }

    @Test
    public void TestInsertNewModel_batch() {
        for (int i = 1; i < 300; i++) {
            Model model = new Model("a", new Data(Data.Type.Images), new Student());
            super.insertNewModel(model);
        }
        assertEquals("Insert models failure", 300, super.getModelsQueueSize());
    }

    @Test(expected = Exception.class)
    public void TestSendBatchToProcess_NullArg() {
        super.sendBatchToProcess(null);
    }

    @Test(expected = Exception.class)
    public void TestSendBatchToProcess_ProcessedBatch() {
        DataBatch batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
        batch.setAsProcessed();
        super.sendBatchToProcess(batch);
    }


    @Test
    public void TestSendBatchToProcess_Base() {
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesWaitingToProcess());
        DataBatch batch = new DataBatch(this, new Data(Data.Type.Tabular), 9);
        super.sendBatchToProcess(batch);
        assertEquals("bad numbers of batches in cluster", 1, this.cluster.getNumOfBatchesWaitingToProcess());
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

    // Thread tFetch = new Thread(() -> {
    // });

    // tFetch.start();
    // try {
    //     Thread.sleep(50);
    // } catch (Exception e) {
    //     assertTrue("Test Failed",false==true);
    // }
    
    // tFetch.interrupt();
    
    
    // try {
    //     Thread.sleep(50);
    // } catch (Exception e) {
    //     assertTrue("Test Failed",false==true);
    // }
    // assertEquals("bad numbers of batches in cluster", super.vRAMsizeInBatches(), this.cluster.getNumOfBatchesWaitingToProcess());
    private void trainBatch() {
        DataBatch batch = this.cluster.popBatchToProcess();
        super.startTrainBatch(batch);   
        super.finalizeTrainBatch(batch);
    }

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
                    System.out.println("Test failed");
                    return;
                }
                cpu.tickSystem();
            }
        });
        Thread tickThread = new Thread(()-> {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.out.println("Test failed");
                    return;
                }
                object.notifyAll();
            }
        });
        Thread gpuThread = new Thread(()-> {
            while (true) {
                try {
                    object.wait();
                } catch (Exception e) {
                    System.out.println("Test failed");
                    return;
                }
                super.tickSystem();
            }
        });
        
        cpuThread.start();
        gpuThread.start();
        tickThread.start();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("Test failed");
            return;
        }

        cpuThread.interrupt();
        gpuThread.interrupt();
        tickThread.interrupt();

        assertEquals("Test failed", 5000, super.getNumOfTrainedBatches());

    }
}
