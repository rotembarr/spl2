package bgu.spl.mics.application.objects;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    final static int DEFAULT_NUM_OF_CORES = 4;

    private int nCores; // Number of cores. can be {1,2,4,8,16,32} by defenition.
    private int n32DivCores; // 32/this.nCore.
    private Cluster cluster; // A pointer to the singletone cluster.
    Queue<DataBatch> batchesQueue; // Max 2 batches at the same time in this CPU.

    // Internal use.
    boolean isProcessing;
    int processingCnt;

    // Statistics.
    private long nOfProcessedBatches;
    private long nOfTimeUsed;


    // public CPU(CPU other) {
    //     this.nCores = other.nCores;
    //     this.cluster = Cluster.getInstance();
    //     this.batches = null;

    // }

    /**
     * Constructor.
     * @param nCores
     * 
     * @pre 1 <= {@param nCores} <= 32
     */
    public CPU() {
        this.nCores = CPU.DEFAULT_NUM_OF_CORES;
        this.n32DivCores = 32/nCores;
        this.cluster = Cluster.getInstance();
        this.batchesQueue = new ArrayDeque<DataBatch>();
        this.isProcessing = false;
        this.processingCnt = 0;
        this.nOfProcessedBatches = 0;
        this.nOfTimeUsed = 0;
    }
    
    /**
     * Constructor.
     * @param nCores
     * 
     * @pre 1 <= {@param nCores} <= 32
     */
    public CPU(int nCores) {
        this.nCores = nCores;
        this.n32DivCores = 32/nCores;
        this.cluster = Cluster.getInstance();
        this.batchesQueue = new ArrayDeque<DataBatch>();
        this.isProcessing = false;
        this.processingCnt = 0;
        this.nOfProcessedBatches = 0;
        this.nOfTimeUsed = 0;
    }

    /**
     * Return number of cores
     * @return this.nCores
     * 
     * @pre none
     * @post trivial.
     */
    public int getNumOfCores() {
        return this.nCores;
    }

    /**
     * Return if we are currentky proessing a batch.
     * @return this.isProcessing
     * 
     * @pre none
     * @post trivial.
     */
    public boolean isProcessing() {
        return this.isProcessing;
    }

    /**
     * Return number of ticks happends.
     * @return this.nOfTimeUsed
     * 
     * @pre none
     * @post trivial
     */
    public long getNumOfTimeUsed() {
        return this.nOfTimeUsed;
    }

    /**
     * Returens the number of processed batches from the start.
     * @return this.nOfProcessedBatches.
     * 
     * @pre none.
     * @post trivial
     */
    public long getNumOfProcessedBatches() {
        return this.nOfProcessedBatches;
    }

    /**
     * Calculate the number of ticks process a batch takes according to his type:
     *     - Images - (32/this.nCores) * 4 ticks.
     *     - Text   - (32/this.nCores) * 2 ticks.
     *     - Table  - (32/this.nCores) * 1 ticks.
     * @param type
     * @return number of ticks.
     * 
     * @pre {@param type} is a valid type
     * @post {@return} != 0
     */
    private int numOfTickToProcess(Data.Type type) {
        if (type == Data.Type.Images) {
            return (n32DivCores*4);
        } else if (type == Data.Type.Text) {
            return (n32DivCores*2);
        } else {
            return (n32DivCores*1);
        }
    }

    /**
     * This is a non-blocking function.
     * It tries to fetch a batch from this.cluster.
     * if no batch is available to process it return null.
     * @return DataBatch
     * 
     * @pre none
     * @post if this.cluster.hasBatchToProcess then {@return} != null else {@return} = null.
     */
    protected DataBatch tryToFetchBatch() {
        DataBatch batch = this.cluster.popBatchToProcess();
        return batch;
        // return this.cluster.popBatchToProcess();
    }

    /**
     * This function is a placeholder function. 
     * It gets a {@type DataBatch} as param and starts processing it.
     * This function actually do nothing but it illustrait that processing is taking care of.
     * @param data
     * 
     * @pre {@param batch} != null
     *  &&  this.isProcessing() = false
     * @post resets this.processingCnt. 
     */
    protected void StartProcessingBatch(DataBatch batch) throws IllegalArgumentException{
        if (batch == null || batch.isDoneProcessing() | this.isProcessing() ) {
            throw new IllegalArgumentException();
        }

        this.processingCnt = 0;
        this.isProcessing = true;
    }

    
    /**
     * This function gets a {@type DataBatch} as param and checks if processing is done.
     * @param data
     * 
     * @pre {@param v} != null
     * @post {@return} = (this.processingCnt == this.numOfTickToProcess(batch.getType())).
     */
    protected boolean isDoneProcessing(DataBatch batch) {
        int a = this.numOfTickToProcess(batch.getType());
        return this.processingCnt == this.numOfTickToProcess(batch.getType());
    }

    /**
     * This is a nn-blocking function.
     * It does all the finalization of batch processing:
     * 1. Mark it as processed.
     * 2. Update statistics.
     * Exceptions throws when input isn't valid.
     * @param batch
     * 
     * @pre {@param batch} !- null
     *  &&  {@param batch.getIsProcessed()} = false
     *  &&  this.isProcessing() = false
     * @post @post(this.getNumOfProcessedBatches()) = 1 +@pre(this.getNumOfProcessedBatches())
     *  &&  {@param batch.getIsProcessed()} = true
     */
    protected void finalizeProcessBatch(DataBatch batch) throws IllegalArgumentException {
        if (!this.isProcessing() | batch == null || batch.isDoneProcessing()) {
            throw new IllegalArgumentException();
        }

        batch.setAsProcessed();
        this.nOfProcessedBatches++;
        this.isProcessing = false;
    }
    
    /**
     * This function is a non-blocking function. 
     * It gets a processed batch and send it to the cluster.
     * Exceptions throws when input isn't valid.
     * @param batch
     * 
     * @pre {@param batch} !- null
     *  &&  {@param batch.getIsProcessed()} = true 
     * @post this.cluster.getNumOfProcessedBatch = 1 + @pre(this.cluster.getNumOfProcessedBatch)
     */
    protected void sendProcessedBatch(DataBatch batch) throws IllegalArgumentException {
        if (batch == null || !batch.isDoneProcessing()) {
            throw new IllegalArgumentException();
        }

        this.cluster.pushProcessedBatch(batch);
    }

    /**
     * This function ticks the CPU:
     * Advance tick counter, and processingCnt;
     * It checks if a batch done processing.
     * If so:
     * 1. It sends it back to the GPU.
     * 2. It tries to fetch new batches to process (max of 2 batches at the same time in the CPU).
     * 3. If there is a batch to process, it starts a new batch processing.
     * If not: Do nothing.
     * @pre none
     * @post all of the above
     */
    public void tickSystem() {
        this.nOfTimeUsed++;
        this.processingCnt++;

        try {

            // Done processing.
            if (this.batchesQueue.size() > 0 && this.isDoneProcessing(this.batchesQueue.peek())) {
                this.finalizeProcessBatch(this.batchesQueue.peek());
                this.sendProcessedBatch(this.batchesQueue.poll());
            }

            // Poll new batches.
            DataBatch batch = null;
            while (this.batchesQueue.size() < 2 && ((batch = this.tryToFetchBatch()) != null)) {
                if (batch.getGPU() == null || batch.getData() == null) {
                    throw new IllegalCallerException();
                } else {
                    this.batchesQueue.add(batch);    
                }
            }

            // Start new processing.
            if (!this.isProcessing() & this.batchesQueue.size() > 0) {
                this.StartProcessingBatch(this.batchesQueue.peek());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
