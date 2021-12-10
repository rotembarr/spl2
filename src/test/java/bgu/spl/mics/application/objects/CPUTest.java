package bgu.spl.mics.application.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;


public class CPUTest extends CPU{
    static int TEST_NUM_OF_CORES = 4;
    Cluster cluster = null;
    GPU gpu1 = null;
    Data data1 = null;

    public CPUTest() {
        super(TEST_NUM_OF_CORES);
    }

    @Before
    public void Init() {
        this.cluster = Cluster.getInstance();
        this.cluster.clearStatistics();
        this.gpu1 = new GPU(GPU.Type.RTX3090);
        this.cluster.addCPU(this);
        this.cluster.addGPU(gpu1);
        DataBatch batch = null;
        while ((batch = this.cluster.popBatchToProcess()) != null){} 
        while ((batch = this.cluster.popProcessedBatch(gpu1)) != null){} 
        data1 = new Data("Zazu", Data.Type.Images, 1000000);
    }

    @Test
    public void TestGetNumOfCores() {
        assertEquals("Bad constructor - nCores wasn't initialized",TEST_NUM_OF_CORES, super.getNumOfCores());
    }

    @Test 
    public void TestGetNuomOfTimePass() {
        // Has to be 0 at first.
        assertEquals(0, super.getNumOfTimeUsed());

        // advance random time.
        int n = (int)(Math.random()*(100)) + 1;
        for (int i = 0; i < n; i++) {
            super.tickSystem();
        }
        assertEquals(n, super.getNumOfTimeUsed());
    }

    @Test 
    public void TestGetNumOfProcessedBatches() {
        DataBatch batch = null;
        // Has to be 0 at first.
        assertEquals(0, super.getNumOfProcessedBatches());

        // Process one.
        Data data = new Data("Zazu", Data.Type.Images, 1000000);
        batch = data.createBatch(gpu1);
        super.StartProcessingBatch(batch);
        super.finalizeProcessBatch(batch);
        assertEquals(1, super.getNumOfProcessedBatches());

        // Process random number.
        int n = (int)(Math.random()*(100)) + 1;
        for (int i = 0; i < n; i++) {
            batch = data1.createBatch(gpu1);
            super.StartProcessingBatch(batch);
            super.finalizeProcessBatch(batch);
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
        batch = data1.createBatch(gpu1);
        this.cluster.pushBatchToProcess(batch);
        assertEquals("First fetch batch failed.", batch, super.tryToFetchBatch()); 

        for (int i = 1; i < 100; i++) {
            batch = data1.createBatch(gpu1);
            queue.add(batch);
            this.cluster.pushBatchToProcess(batch);
        }

        for (int i = 1; i < 100; i++) {
            assertEquals("Fetch batch failed.", queue.poll(), super.tryToFetchBatch()); 
        }
    }


    @Test
    public void TestTryToFetchBatch_Adv() {
        int cnt = 0;
        DataBatch batch = null;
        Queue<DataBatch> queue = new LinkedList<DataBatch>();
        
        for (int i = 1; i < 100; i++) {
            if ((int)(Math.random()*(100)) < 50) {
                batch = data1.createBatch(gpu1);
                queue.add(batch);
                this.cluster.pushBatchToProcess(batch);
                cnt++;
            } else {
                if (cnt <= 0) {
                    assertEquals("Fetch batch failed - expected null.",null, super.tryToFetchBatch()); 
                } else {
                    assertEquals("Fetch batch failed.", queue.poll(), super.tryToFetchBatch()); 
                }
                if (cnt > 0) {
                    cnt--;
                }
            }
        }
    }

    // Start processing
    @Test
    public void TestStartProcessingBatch_NullArg() {
        DataBatch batch = null;
        assertThrows(IllegalArgumentException.class, () -> {super.StartProcessingBatch(batch);});
    }

    @Test
    public void TestStartProcessingBatch_SendProcessedBatch() {
        DataBatch batch = data1.createBatch(gpu1);
        super.StartProcessingBatch(batch);
        super.finalizeProcessBatch(batch);
        assertThrows(IllegalArgumentException.class, () -> {super.StartProcessingBatch(batch);});
    }


    @Test
    public void TestStartProcessingBatch_startTwice() {
        DataBatch batch = data1.createBatch(gpu1);
        super.StartProcessingBatch(batch);
        assertThrows(IllegalArgumentException.class, () -> {super.StartProcessingBatch(batch);});
    }

    @Test
    public void TestStartProcessingBatch_Burst() {
        for (int i = 0; i < 100; i++) {
            DataBatch batch = data1.createBatch(gpu1);
            super.StartProcessingBatch(batch);
            super.finalizeProcessBatch(batch);
        }
    }

    // FinalizeProcessBatch
    @Test
    public void finalizeProcessBatch_NullArgs() {
        assertThrows(IllegalArgumentException.class, () -> {super.finalizeProcessBatch(null);});
    }

    @Test
    public void finalizeProcessBatch_FinalizeTwice() {
        DataBatch batch = data1.createBatch(gpu1);
        super.StartProcessingBatch(batch);
        super.finalizeProcessBatch(batch);
        assertThrows(IllegalArgumentException.class, () -> {super.finalizeProcessBatch(batch);});
    }

    @Test
    public void finalizeProcessBatch_FinalizedWithoutStart() {
        DataBatch batch = data1.createBatch(gpu1);
        assertThrows(IllegalArgumentException.class, () -> {super.finalizeProcessBatch(batch);});
    }


    @Test
    public void finalizeProcessBatch_Burst() {
        for (int i = 0; i < 100; i++) {
            DataBatch batch = data1.createBatch(gpu1);
            super.StartProcessingBatch(batch);
            super.finalizeProcessBatch(batch);
        }
        assertEquals("Somehow batches proceseed", 100, super.getNumOfProcessedBatches());

    }

    // SendProcessedBatch.
    @Test
    public void TestSendProcessedBatch_NullArgs() {
        assertThrows(IllegalArgumentException.class, () -> {super.sendProcessedBatch(null);});
    }

    @Test
    public void TestSendProcessedBatch_UnprocessedBatch() {
        DataBatch batch = data1.createBatch(gpu1);
        assertThrows(IllegalArgumentException.class, () -> {super.sendProcessedBatch(batch);});
    }

    @Test
    public void TestSendProcessedBatch_Burst() {
        for (int i = 0; i < 100; i++) {
            DataBatch batch = data1.createBatch(gpu1);
            super.StartProcessingBatch(batch);
            super.finalizeProcessBatch(batch);
            super.sendProcessedBatch(batch);
            this.cluster.popProcessedBatch(gpu1);
        }
        assertEquals("Bad num of processed batches recieved in cluster", 100, this.cluster.getNumOfBatchesProcessedByCPUs());
    }

    // TickSystem.
    @Test
    public void TestTickSystem_noBatches() {
        for (int i = 0; i < 100; i++) {
            super.tickSystem();
        }
        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesProcessedByCPUs());
        assertEquals("Somehow batches proceseed", 0, super.getNumOfProcessedBatches());
        assertEquals("Somehow batches proceseed", 100, super.getNumOfTimeUsed());
    }

    @Test
    public void TestTickSystem_oneBatchEachKind() {
        Data tabular = new Data("tabular", Data.Type.Tabular, 1000);
        Data Text = new Data("text", Data.Type.Text, 1000);
        Data image = new Data("Images", Data.Type.Images, 1000);
        this.cluster.pushBatchToProcess(tabular.createBatch(gpu1));  
        this.cluster.pushBatchToProcess(Text.createBatch(gpu1));  
        this.cluster.pushBatchToProcess(image.createBatch(gpu1));  

        assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesProcessedByCPUs());

        // Latency tick.
        super.tickSystem();

        for (int i = 0; i < 32/4; i++) {
            assertEquals("Somehow batches sent to cluster when not need", 0, this.cluster.getNumOfBatchesProcessedByCPUs());
            super.tickSystem();
        }
        assertEquals("Tabular hasnt processed in 32/ncores clock", 1, this.cluster.getNumOfBatchesProcessedByCPUs());

        for (int i = 0; i < 2* 32/4; i++) {
            assertEquals("Somehow batches sent to cluster when not need", 1, this.cluster.getNumOfBatchesProcessedByCPUs());
            super.tickSystem();
        }
        assertEquals("Text hasnt processed in 2 * 32/cores clock", 2, this.cluster.getNumOfBatchesProcessedByCPUs());
        
        for (int i = 0; i < 4*32/4; i++) {
            assertEquals("Somehow batches sent to cluster when not need", 2, this.cluster.getNumOfBatchesProcessedByCPUs());
            super.tickSystem();
        }
        assertEquals("Image hasnt processed in 4 * 32/cores clock", 3, this.cluster.getNumOfBatchesProcessedByCPUs());
    }

    @Test
    public void TestTickSystem_testThrougput() {
        int numOfClockToProcess100Image = 3200;

        for (int i = 0; i < 100; i++) {
            this.cluster.pushBatchToProcess(data1.createBatch(gpu1));
        }  

        // Latenct clock 
        this.tickSystem();

        int cnt = 0;
        while (cnt < numOfClockToProcess100Image && this.cluster.getNumOfBatchesProcessedByCPUs() != 100) {
            for (int i = 0; i < 32; i++) {
                assertEquals("Somehow batches sent to cluster when not need", cnt, this.cluster.getNumOfBatchesProcessedByCPUs());
                this.tickSystem();
            }

            cnt++;
            this.cluster.popProcessedBatch(gpu1);
        }

        assertEquals("Frocessing for too long", cnt*32, numOfClockToProcess100Image);
        assertEquals("Somehow batches proceseed", 100, super.getNumOfProcessedBatches());
        assertEquals("cluster havnt recieved processed batch", 100, this.cluster.getNumOfBatchesProcessedByCPUs());
    }
}
