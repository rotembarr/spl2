package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    Data data; // The data tha batch belongs to
    int startIndex; // The index of the first sample in the batch
    int size;
    GPU gpu; // The origin GPU that sent the batch. 
    boolean doneProcessing;    

    public DataBatch(GPU gpu, Data data, int startIndex) {
        this.data = data;
        this.startIndex = startIndex;
        this.gpu = gpu;
        this.doneProcessing = false;
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
     * Returns if the batch has processed;
     * @return this.doneProcessing.
     * 
     * @pre none
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    public boolean getDoneProcessing() {
        return this.doneProcessing;
    }

}
