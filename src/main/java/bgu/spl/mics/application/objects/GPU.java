package bgu.spl.mics.application.objects;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
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
    public enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Cluster cluster;
    private List<Model> modelsToTrain; // Has no size limit.
    
    // Internal use.
    private Queue<DataBatch> processedBatchesQueue; // Up to 32
    private int nProcessingBatches;
    private int trainingCnt; // Number of ticks the current processed batch is training. 
    private boolean isTraining;
    
    // Statistics.
    private Queue<String> modelNames;
    private int numOfTrainedBatches;
    private int nTimePass;
    private int nTimeUsed;

    public GPU(Type type) {
        this.type = type;
        this.cluster = Cluster.getInstance();
        this.modelsToTrain = new LinkedList<Model>();
        this.processedBatchesQueue = new ArrayDeque<DataBatch>();
        this.nProcessingBatches = 0;
        this.trainingCnt = 0;
        this.isTraining = false;
        this.modelNames = new LinkedList<String>();
        this.nTimePass = 0;
        this.nTimeUsed = 0;
    }

    /**
     * Returns a copy of model names.
     * @return copy of this.modelNames
     * 
     * @pre none
     * @post {@result} = this.modelNames.
     */
    public List<String> getModelNames() {
        return new LinkedList<String>(this.modelNames);
    }        

    /**
     * Return number of ticks happends.
     * @return this.nTimePass
     * 
     * @pre none
     * @post trivial
     */
    public int getNumOfTimePass() {
        return this.nTimePass;
    }

    /**
     * Return number of ticks CPU processed.
     * @return this.nOfTimeUsed
     * 
     * @pre none
     * @post trivial
     */
    public int getNumOfTimeUsed() {
        return this.nTimeUsed;
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
    public int getNumOfTrainedBatches() {
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
        if (this.type == Type.RTX3090) {
            return 1;
        } else if (this.type == Type.RTX2080) {
            return 2;
        } else {
            return 4;
        }
    }

    /**
     * Return the size of internal VRAM:
     *     - 3090 - 32 batches.
     *     - 2080 - 16 batches.
     *     - 1080 - 8 batches.
     * @param type
     * @return vram size.
     * 
     * @pre {@param type} is a valid type
     * @post {@return} != 0
     */
    protected int vRAMsizeInBatches() {
        if (this.type == Type.RTX3090) {
            return 32;
        } else if (this.type == Type.RTX2080) {
            return 16;
        } else {
            return 8;
        }
    }


    /**
     * Return how many models are waiting/currently processing.
     * @return
     */
    public int getModelsQueueSize() {
        return this.modelsToTrain.size();
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
     * &&   {@param model.getStatus() != TRAINED}
     * @post {@param model.getStatus() = TESTED} and return {@result} as described above.
     */
    public void testModel (Model model) throws IllegalArgumentException {

        // Checkers.
        if (model == null || model.getStatus() != Model.Status.TRAINED) {
            throw new IllegalArgumentException();
        }

        // Set chances of success.
        int chance = 0;
        if (model.getStudent().getDegree() == Student.Degree.MSc) {
            chance = 60;
        } else { // PhD
            chance = 80;
        } 

        // Testing (pay attention to <).
        boolean answer = ((int)Math.random() * 100) < chance ? true : false;
        if (answer) {
            model.setResult(Model.Result.GOOD);
        } else {
            model.setResult(Model.Result.BAD);
        }
        model.setStatus(Model.Status.TESTED);
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
     * && {@param model} not inside this.modelsToTrain
     * @post @post(this.modelsToTrain.size) = 1 + @pre(this.modelsToTrain.size)
     */
    public void insertNewModel(Model model) throws IllegalArgumentException {

        // Checkers.
        if (model == null || model.getStatus() != Model.Status.PRE_TRAINED || this.modelsToTrain.contains(model)) {
            throw new IllegalArgumentException();
        }

        // Add model.
        this.modelsToTrain.add(model);
        this.modelNames.add(model.getName());
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

        // Checkers.
        if (batch == null || batch.isDoneProcessing()) {
            throw new IllegalArgumentException();
        }

        // Send batch to the cluster.
        this.cluster.pushBatchToProcess(batch);
        this.nProcessingBatches++;
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
        // Variables.
        int cnt = 0;
        int modelIndex = 0;
        
        // Search the first model who can send something
        while (modelIndex < this.modelsToTrain.size() && this.modelsToTrain.get(modelIndex).getData().isFragmantationFinished()) {
            modelIndex++;
        }

        while (this.nProcessingBatches < this.vRAMsizeInBatches() && modelIndex < this.modelsToTrain.size() && !this.modelsToTrain.get(modelIndex).getData().isFragmantationFinished()) {

            // Creae and send batch
            DataBatch newBatch = this.modelsToTrain.get(modelIndex).getData().createBatch(this);
            this.cluster.pushBatchToProcess(newBatch);
            this.nProcessingBatches++;

            // Search the first model who can send something - hot swap.
            while (modelIndex < this.modelsToTrain.size() && this.modelsToTrain.get(modelIndex).getData().isFragmantationFinished()) {
                modelIndex++;
            }

        }
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

        // Cannot import more than vRAM size. IT shouldnt happend anyway!!
        if (this.processedBatchesQueue.size() >= this.vRAMsizeInBatches()) {
            return null;
        }

        // Poll from cluster in non blocking manner.
        DataBatch batch = this.cluster.popProcessedBatch(this);
        if (batch != null) {
            this.nProcessingBatches--;
        }
        return batch;
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

        // Checkers
        if (batch == null || (!batch.isDoneProcessing() | batch.getGPU() != this | this.isTraining)) {
            throw new IllegalArgumentException();            
        }

        // Start train.
        this.isTraining = true;
        this.trainingCnt = 0;
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
        
        // Checkers
        if (batch == null || (!batch.isDoneProcessing() | batch.getGPU() != this | !this.isTraining)) {
            throw new IllegalArgumentException();            
        }

        // Stop train.
        batch.setAsTrained();
        this.isTraining = false;
        this.trainingCnt = 0;
        this.numOfTrainedBatches++;
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
     *  &&  {@param model} inside this.modelsToTrain
     * @post @post(this.modelsToTrain.size()) = @pre(this.modelsToTrain.size()) -1
     * 
     */
    protected void doneTrainingModel(Model model) throws IllegalArgumentException {
        
        // Checkers
        if (model == null || (!model.getData().isTrainingFinished() | !this.modelsToTrain.contains(model))) {
            throw new IllegalArgumentException();            
        }

        if (model.getStatus() == Model.Status.TRAINED) {
            throw new IllegalArgumentException();            
        }

        model.setStatus(Model.Status.TRAINED);
    }

    /**
     * This is a non-blocking function.
     * First, it tries to fetch all the processed data (Up to the limit of the VRAM).
     * Then, it fragmantizes all the batches it can in order to let the CPUs process and be busy all the time.
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
        this.nTimePass++;
        this.trainingCnt++;
        if (this.isTraining) {
            this.nTimeUsed++;
        }

        if (this.processedBatchesQueue.size() > this.vRAMsizeInBatches()) {
            throw new InternalError();
        }
        
        // Train current batch.
        if (this.doesTrainingBatchFinished()) {
            this.finalizeTrainBatch(this.processedBatchesQueue.peek());
            this.processedBatchesQueue.poll();
        }

        // TODO
        // check if a model finish his training.
        if (this.modelsToTrain.size() > 0 && this.modelsToTrain.get(0).getData().isTrainingFinished()) {
            this.doneTrainingModel(this.modelsToTrain.get(0));
            this.modelsToTrain.remove(0);
        }

        // Fetch as much as we can. tryToFetchProcessedBatch func will handle capacity.
        DataBatch processedBatch = null;
        while ((processedBatch = this.tryToFetchProcessedBatch()) != null) {
            this.processedBatchesQueue.add(processedBatch);
        }

        // Send batches to cpu. max can be vRAM size batches 'in the air'.
        this.fragmentizeBatchesToProcess();

        // Start training new batch.
        if (!this.isTraining && this.processedBatchesQueue.size() > 0) {
            this.startTrainBatch(this.processedBatchesQueue.peek());
        }

    }
}
