package bgu.spl.mics.application.objects;

import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    final static int DEFAULT_NUM_OF_CORES = 4;

    private int nCores; // Number of cores. can be {1,2,4,8,16,32} by defenition.
    private Cluster cluster; // A pointer to the singletone cluster.
    Queue<DataBatch> batches; // Max 2 batches at the same time in this CPU.
    // Container<DataBatch> datsa;

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
        this.cluster = null;
        this.batches = null;
    }

    /**
     * Constructor.
     * @param nCores
     * 
     * @pre 1 <= {@param nCores} <= 32
     */
    public CPU(int nCores) {
        this.nCores = nCores;
        this.cluster = null;
        this.batches = null;
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
    public int isProcessing() {
        return this.isProcessing();
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
        return 0;
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
        return null;
    }

    /**
     * This function is a placeholder function. 
     * It gets a {@type DataBatch} as param and starts processing it.
     * This function actually do nothing but it illustrait that processing is taking care of.
     * @param data
     * 
     * @pre {@param v} != null
     *  &&  this.isProcessing() = false
     * @post resets this.processingCnt. 
     */
    protected void StartProcessingBatch(DataBatch batch) throws IllegalArgumentException{

        // TODO checks

        // this.isProcessing = true;
        // this.processingCnt = 0;
    }

    
    /**
     * This function gets a {@type DataBatch} as param and checks if processing is done.
     * @param data
     * 
     * @pre {@param v} != null
     * @post {@return} = (this.processingCnt == this.numOfTickToProcess(batch.getType())).
     */
    protected boolean isDoneProcessing(DataBatch batch) {
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
    }

}
