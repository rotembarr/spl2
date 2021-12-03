package bgu.spl.mics.application.objects;

import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    final static int DEFAULT_NUM_OF_CORES = 4;

    private int nCores; // Number of cores.
    private Cluster cluster; // A pointer to the singletone cluster.
    Queue<DataBatch> batches; // Max 2 batches at the same time in this CPU.
    // Container<DataBatch> datsa;

    // Statistics.
    private long nOfProcessedBatches;
    private long nOfTimePass;


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
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    public int getNumOfCores() {
        return this.nCores;
    }

    /**
     * Return number of ticks happends.
     * @return this.nOfTimePass
     * 
     * @pre none
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    public long getNumOfTimePass() {
        return this.nOfTimePass;
    }

    /**
     * Returens the number of processed batches from the start.
     * @return this.nOfProcessedBatches.
     * 
     * @pre none.
     * @inv calling this function doesn't change anything in the class.
     * @post {@return} = this.nOfProcessedBatches
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
     * @inv calling this function doesn't change anything in the class.
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
     * @inv calling this function doesn't change anything in the class.
     * @post if this.cluster.hasBatchToProcess then {@return} != null else {@return} = nul.
     */
    protected DataBatch tryToFetchBatch() {
        return null;
    }

    /**
     * This function is a placeholder function. 
     * It gets a {@type DataBatch} as param and starts process it.
     * This function actually do nothing but it illustrait that processing is taking care of.
     * Exceptions throws when input isn't valid.
     * @param data
     * 
     * @pre {@param v} != null
     * @inv TODO
     * @post none 
     */
    protected void StartProcessingBatch(DataBatch batch) throws IllegalArgumentException{
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
     *  &&  {@param batch.getIsProcessed()} = true 
     * @inv
     * @post @post(this.getNumOfProcessedBatches()) = @pre(this.getNumOfProcessedBatches())
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
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    protected void sendProcessedBatch(DataBatch batch) throws IllegalArgumentException {
    }


    /**
     * This function ticks the CPU:
     * It checks if a batch done processing. 
     * If so:
     * 1. It sends it back to the GPU
     * 2. It tries to fetch new batches to process (max of 2 batches at the same time in the CPU).
     * 3. Start process a new batch.
     * If not: just advance internal counter.
     * @pre none
     * @inv TODO
     * @post TODD
     */
    public void tickSystem() {
    }

}
