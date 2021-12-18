package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data; // The data tha batch belongs to
    private int startIndex; // The index of the first sample in the batch
    private int size;
    private GPU gpu; // The origin GPU that sent the batch. 

    // Statistics.
    private boolean doneProcessing;    
    private boolean doneTraining;    

    public DataBatch(GPU gpu, Data data, int startIndex) {
        this.data = data;
        this.startIndex = startIndex;
        this.gpu = gpu;
        this.doneProcessing = false;
        this.doneTraining = false;
        this.size = 1000;
    } 

    /**
     * Returns the type of the origin data;
     * @return this.data.getType()
     * 
     * @pre this.data != null
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    public Data.Type getType() {
        return this.data.getType();
    }



    /**
     * Returns starting index;
     * @return this.startIndex.
     * 
     * @pre none
     * @post trivial
     */
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * Returns if the batch has processed;
     * @return this.doneProcessing.
     * 
     * @pre none
     * @post trivial
     */
    public boolean isDoneProcessing() {
        return this.doneProcessing;
    }


    /**
     * Returns if the batch has trained
     * @return this.doneTraining.
     * 
     * @pre none
     * @post trivial
     */
    public boolean isDoneTraining() {
        return this.doneTraining;
    }
    /**
     * @pre none
     * @post isDoneProcessing()==True
     */
    public void setAsProcessed() {
        this.doneProcessing = true;
        this.data.batchProcessed();
    }
    /**
     * @pre none
     * @post isDoneTraining()==True
     */
    public void setAsTrained() {
        this.doneTraining = true;
        this.data.batchTrained();
    }

    public GPU getGPU() {
        return this.gpu;
    }

    public Data getData() {
        return this.data;
    }
}
