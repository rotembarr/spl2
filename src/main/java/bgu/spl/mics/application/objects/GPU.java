package bgu.spl.mics.application.objects;

import java.util.Queue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    final static int MAX_PROCESSED_DATA_BATCH_STORED = 32;
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Cluster cluster;
    private Queue<Model> modelsToProcess; // Has no size limit.
    
    // Internal use.
    private Queue<DataBatch> processedbatches; // Up to 32
    private long numOfBatchesSentToProcess;
    private int trainingCnt; // Number of ticks the current processed batch is training. 
    private boolean isTraining;
    
    // Statistics.
    private Queue<String> modelNames;
    private long numOfTrainedBatches;
    private long nOfTimePass;

    public GPU(Type type) {
        this.type = type;
        this.cluster = Cluster.getInstance();
        this.modelsToProcess = null;
        this.processedbatches = null;
        this.numOfBatchesSentToProcess = 0;
        this.trainingCnt = 0;
        this.isTraining = false;
        this.modelNames = null;
        this.nOfTimePass = 0;
    }

    /**
     * Returns a copy of model names.
     * @return copy of this.modelNames
     * 
     * @pre none
     * @post {@result} = this.modelNames.
     */
    public Queue<String> getModelNames() {
        return null;
    }        

    /**
     * Return number of ticks happends.
     * @return this.nOfTimePass
     * 
     * @pre none
     * @post trivial
     */
    public long getNumOfTimePass() {
        return this.nOfTimePass;
    }

    
    /**
     * Return if GPU trains.
     * @return this.isTraining
     * 
     * @pre none
     * @post trivial
     */
    public boolean isTraining() {
        return this.isTraining;
    }
    
    /**
     * Return number of trained batches.
     * @return this.numOfTrainedBatches
     * 
     * @pre none
     * @post none
     */
    public long getNumOfTrainedBatches() {
        return this.numOfTrainedBatches;
    }
    /**
     * Calculate the number of ticks process a batch takes according to its type:
     *     - 3090 - 1 ticks.
     *     - 2080 - 2 ticks.
     *     - 1080 - 4 ticks.
     * @param type
     * @return number of ticks.
     * 
     * @pre {@param type} is a valid type
     * @post {@return} != 0
     */
    protected int numOfTickToTrain() {
        return 0;
    }

    protected int vRAMsizeInBatches() {
        return 0;
    }


    /**
     * Return how many models are waiting/currently processing.
     * @return
     */
    public int getModelsQueueSize() {
        return this.modelsToProcess.size();
    }

    /**
     * This function is non-blocking.
     * It handles student request and tests the Model.
     * The function test the model immidiatly.
     * Basically it returns 'GOOD' whith probabolity of 0.1 for MSc student,
     * and of 0.2 for PhD student.
     * @param model
     * @return result describrd above.
     * 
     * @pre {@param model} != null 
     * &&   {@param model.getStatus() = TRAINED}
     * @post {@param model.getStatus() = TESTED} and return {@result} as described above.
     */
    public Model.Result testResult (Model model) throws IllegalArgumentException{
        return Model.Result.NONE;
    }

    /**
     * This function is non-blocking function.
     * It inserts a new model to train.
     * The function saves the model in a queue,
     * and the GPU will train this model when the it done 
     * training all other Models that recieved so far.
     * 
     * @param model The Model.
     * @throws IllegalArgumentException
     * 
     * @pre {@param model} != null 
     * && {@param model.getStatus()} = PRE_TRAINED
     * && {@param model} not inside this.modelsToProcess
     * @post @post(this.modelsToProcess.size) = 1 + @pre(this.modelsToProcess.size)
     */
    public void insertNewModel(Model model) throws IllegalArgumentException {
    }

    /**
     * This is a non-blocking function.
     * It sends {@param batch} to the cluster, 
     * which then will pass it to one of the CPUs.
     * In addition it updates to relevant counters.
     * 
     * @param batch The batch to send.
     * 
     * @pre {@param batch} != null
     *  && {@param batch.isDoneProcessing()} = false.
     * @post this.cluster.getNumOfBatchesWaitingToProcess = 1 + @pre(getNumOfBatchesWaitingToProcess)
     */    
    protected void sendBatchToProcess(DataBatch batch) throws IllegalArgumentException{
    }
        
    /**
     * This is a non-blocking function.
     * It creates and sends batches to the 'cluster' for processing by the CPUs.
     * It does all the calculation according to:
     * 1. How many batches already sent and havn't recieved.
     * 2. How many batches are waiting to be train in this GPU.
     * 3. The type of this GPU and how much time it takes to train a batch.  
     * This function never resolve an error.
     * This function uses createNextBatch() and sends itto the cluster.
     * 
     * @return how many batches sent.

     * @pre none
     * @post all of the above
     */
    protected int fragmentizeBatchesToProcess() {
        return 0;
    }


    /**
     * This function is a non-blocking function.
     * It tries to fetch proccessed batch from the cluster.
     * It doesnt care of vRAM size.
     * @return Fetched batch. If no batch fetched, return null.
     * 
     * @pre none
     * @post if we can fetch, {@return} = @pre(cluster.getProcessedBatch(this))
     *       else, {@return} = null.
     *  && {@return}.getGPU() = this.
     */
    protected DataBatch tryToFetchProcessedBatch() {
        // if (this.processedbatches.size() >= 32)
        return null;
    }

    /**
     * This is a non-blocking function.
     * Each batch should be train after it being processed.
     * Here we just resets this.trainingCnt and sets this.isTrainig
     * and not performing a real training.
     * @param batch
     * 
     * @pre {@param batch} != null
     *  && {@param batch.isDoneProcessing()} = true.
     *  && {@param batch.getGPU()} = this.
     *  && this.isTraining = false
     * @post @post(this.isTraining) = true.
     *  &&   @post(this.trainingCnt) = 0.
     */
    protected void startTrainBatch(DataBatch batch) throws IllegalArgumentException {
    }

    /**
     * This is a non-blocking function.
     * it checks if at this moment batch has finished his training.
     * @return boolean answer
     * 
     * @pre none
     * @post {@return} = (this.isTraining & this.trainingCnt >= this.numOfTickToTrain())
     */
    protected boolean doesTrainingBatchFinished() {
        return (this.isTraining & this.trainingCnt >= this.numOfTickToTrain());
    }

    /**
     * This function is a non-blocking function.
     * It reset this.trainingCnt and resets this.isTraining.
     * @param batch
     * 
     * @pre {@param batch} != null
     *  && {@param batch.isDoneProcessing()} = true.
     *  && {@param batch.isDoneTrainig()} = false.
     *  && {@param batch.getGPU()} = this.
     *  && this.isTraining = true
     * @post {@param batch}.isDoneTraining() = true
     *  && this.isTraining = false
     */
    protected void finalizeTrainBatch(DataBatch batch) throws IllegalArgumentException {
    }

    /**
     * This is a non-blocking function.
     * It gets a Model that finish his training, and finished the event of 'TrainModel'.
     * It sets the {@param model} as TRAINED, 
     * and tells the service to finish the event. aka resolve.
     * @param model
     * 
     * @pre {@param model} != null
     *  &&  {@param model.getData().isTrainingFinished() = true}
     *  &&  {@param model} inside this.modelsToProcess
     * @post @post(this.modelsToProcess.size()) = @pre(this.modelsToProcess.size()) -1
     * 
     */
    protected void doneTrainingModel(Model model) throws IllegalArgumentException {
    }

    /**
     * This is a non-blocking function.
     * First, it fragmantizes all the batches it can in order to let the CPUs  be busy all the time.
     * Then it tries to fetch all the processed data (Up to the limit of the VRAM).
     * After, it checks if the training of the current batch has finished.
     * If so it tells the data of the batch that a batch has trained,
     * and after, it starts training a new batch and resets this.trainingCnt.
     * If the current batch haven't finished his trainig we will advance this.trainintCnt. 
     * Lastly, it checks if a model finish his training, and if so it call the doneTrainingModel function.
     * This function never reslove an error.
     * 
     * @pre none
     * @post all of the above
     */
    public void tickSystem() {

    }
}
