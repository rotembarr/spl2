package bgu.spl.mics.application.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;


public class CPUTest extends CPU{
    static int TEST_NUM_OF_CORES = 4;
    Cluster cluster = null;

    public CPUTest() {
        super(TEST_NUM_OF_CORES);
    }

    @Before
    public void Init() {
        this.cluster = Cluster.getInstance();
    }

    @Test
    public void TestGetNumOfCores() {
        assertEquals("Bad constructor - nCores wasn't initialized",TEST_NUM_OF_CORES, super.getNumOfCores());
    }

    @Test 
    public void TestGetNuomOfTimePass() {
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
    public void TestGetNumOfProcessedBatches() {
        // Has to be 0 at first.
        assertEquals(0, super.getNumOfProcessedBatches());

        // Process one.
        super.finalizeProcessBatch(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7));
        assertEquals(1, super.getNumOfProcessedBatches());

        // Process random number.
        int n = (int)(Math.random()*(100)) + 1;
        for (int i = 0; i < n; i++) {
            super.finalizeProcessBatch(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7));
        }
        assertEquals(1+n, super.getNumOfProcessedBatches());
    }

    @Test
    public void TestTryToFetchBatch_Base() {
        DataBatch batch = null;
        Queue<DataBatch> queue = new LinkedList<DataBatch>();
        
        // Fetch null.
        assertEquals("No batch to fetch failed.", null, super.tryToFetchBatch()); 

        // Fetch First.
        batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7);
        this.cluster.pushBatchToProcess(batch);
        assertEquals("First fetch batch failed.", batch, super.tryToFetchBatch()); 

        for (int i = 1; i < 100; i++) {
            batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), i);
            queue.add(batch);
            this.cluster.pushBatchToProcess(batch);
        }

        for (int i = 1; i < 100; i++) {
            assertNotEquals("Fetch batch failed.", queue.poll(), super.tryToFetchBatch()); 
        }
    }


    @Test
    public void TestTryToFetchBatch_Adv() {
        int cnt = 0;
        DataBatch batch = null;
        Queue<DataBatch> queue = new LinkedList<DataBatch>();
        
        for (int i = 1; i < 300; i++) {
            if ((int)(Math.random()*(100)) < 50) {
                batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), i);
                queue.add(batch);
                this.cluster.pushBatchToProcess(batch);
                cnt++;
            } else {
                if (cnt <= 0) {
                    assertNotEquals("Fetch batch failed - expected null.",null, super.tryToFetchBatch()); 
                } else {
                    assertNotEquals("Fetch batch failed.", queue.poll(), super.tryToFetchBatch()); 
                }
                cnt--;
            }
        }
    }

    // Start processing
    @Test(expected = Exception.class)
    public void TestStartProcessingBatch_NullArg() {
        DataBatch batch = null;
        super.StartProcessingBatch(batch);        
    }

    @Test(expected = Exception.class)
    public void TestStartProcessingBatch_NullGPU() {
        DataBatch batch = new DataBatch(null, new Data(Data.Type.Tabular), 7);
        super.StartProcessingBatch(batch); 
    }

    @Test(expected = Exception.class)
    public void TestStartProcessingBatch_NullData() {
        DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), null, 7);
        super.StartProcessingBatch(batch); 
    }

    @Test(expected = Exception.class)
    public void TestStartProcessingBatch_SendProcessedBatch() {
        DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7);
        super.finalizeProcessBatch(batch);
        super.StartProcessingBatch(batch);
    }


    @Test(expected = Exception.class)
    public void TestStartProcessingBatch_startTwice() {
        DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7);
        super.StartProcessingBatch(batch);
        super.StartProcessingBatch(batch);
    }

    @Test(expected = Exception.class)
    public void TestStartProcessingBatch_Burst() {
        for (int i = 0; i < 100; i++) {
            DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), i);
            super.StartProcessingBatch(batch);
        }
    }

    // FinalizeProcessBatch
    @Test(expected = Exception.class)
    public void finalizeProcessBatch_NullArgs() {
        super.finalizeProcessBatch(null);
    }

    @Test(expected = Exception.class)
    public void finalizeProcessBatch_FinalizeTwice() {
        DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7);
        super.finalizeProcessBatch(batch);
        super.finalizeProcessBatch(batch);
    }

    @Test
    public void finalizeProcessBatch_Burst() {
        for (int i = 0; i < 100; i++) {
            DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), i);
            super.finalizeProcessBatch(batch);
        }
    }

    // SendProcessedBatch.
    @Test(expected = Exception.class)
    public void TestSendProcessedBatch_NullArgs() {
        super.sendProcessedBatch(null);
    }

    @Test(expected = Exception.class)
    public void TestSendProcessedBatch_UnprocessedBatch() {
        DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7);
        super.sendProcessedBatch(batch);
    }

    @Test
    public void TestSendProcessedBatch_Burst() {
        for (int i = 0; i < 100; i++) {
            DataBatch batch = new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), i);
            super.finalizeProcessBatch(batch);
            super.sendProcessedBatch(batch);
        }
    }

    // TickSystem.
    @Test
    public void TestTickSystem_noBatches() {
        for (int i = 0; i < 100; i++) {
            super.tickSystem();
        }
        assertEquals("Somehow batches sent to cluster when not need", this.cluster.getNumOfBatchesProcessedByCPUs(), 0);
    }

    @Test
    public void TestTickSystem_oneBatchEachKind() {
        this.cluster.pushBatchToProcess(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 1));  
        this.cluster.pushBatchToProcess(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Text), 2));  
        this.cluster.pushBatchToProcess(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Images), 4));  

        assertEquals("Somehow batches sent to cluster when not need", this.cluster.getNumOfBatchesProcessedByCPUs(), 0);

        super.tickSystem();
        assertEquals("Tabular hasnt processed in one clock", this.cluster.getNumOfBatchesProcessedByCPUs(), 1);

        super.tickSystem();
        assertEquals("Somehow batches sent to cluster when not need", this.cluster.getNumOfBatchesProcessedByCPUs(), 1);
        super.tickSystem();
        assertEquals("Text hasnt processed in two clock", this.cluster.getNumOfBatchesProcessedByCPUs(), 2);

        super.tickSystem();
        assertEquals("Somehow batches sent to cluster when not need", this.cluster.getNumOfBatchesProcessedByCPUs(), 2);
        super.tickSystem();
        assertEquals("Somehow batches sent to cluster when not need", this.cluster.getNumOfBatchesProcessedByCPUs(), 2);
        super.tickSystem();
        assertEquals("Somehow batches sent to cluster when not need", this.cluster.getNumOfBatchesProcessedByCPUs(), 2);
        super.tickSystem();
        assertEquals("Image hasnt processed in four clock", this.cluster.getNumOfBatchesProcessedByCPUs(), 3);
    }

    @Test
    public void TestTickSystem_testThrougput() {
        int numOfClockToProcess100Tabular = 101;

        for (int i = 1; i < 100; i++) {
            this.cluster.pushBatchToProcess(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), i));
        }  

        int cnt = 0;
        while (cnt < numOfClockToProcess100Tabular && this.cluster.getNumOfBatchesProcessedByCPUs() != 100) {
            this.tickSystem();
            cnt++;
        }

        assertEquals("Frocessing for too long", cnt, numOfClockToProcess100Tabular);

    }
}

// Thread tFetch = new Thread(()->{
//     long start = System.currentTimeMillis();
//     super.fetchBatch();
//     long end = System.currentTimeMillis();
//     assertTrue("Fetching batch takes too short", (end-start) > 50);
//     assertTrue("Fetching batch takes too long", (end-start) < 50);

// });
// Thread tSend = new Thread(()->{
//     try {
//         Thread.sleep(50);
//         this.cluster.pushBatchToProcess(new DataBatch(new GPU(GPU.Type.RTX2080), new Data(Data.Type.Tabular), 7));
//     } catch (Exception e) {
//         assertTrue("Test Failed",false==true);
//     }
// });

// tFetch.start();
// tSend.start();
// try {
//     Thread.sleep(50);
// } catch (Exception e) {
//     assertTrue("Test Failed",false==true);
// }
// tFetch.interrupt();
// tSend.interrupt();
