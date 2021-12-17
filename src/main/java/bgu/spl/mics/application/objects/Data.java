package bgu.spl.mics.application.objects;

import com.google.gson.annotations.Expose;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    final static int BATCH_SIZE = 1000;
    
    // Enum representing the Data type.
    public enum Type {
        Images, Text, Tabular
    }

    // Variables.
    @Expose(serialize = false, deserialize = false)
    private String name;
    @Expose(serialize = true, deserialize = true)
    private Type type; // Type of the data.
    @Expose(serialize = true, deserialize = true)
    private int size;

    // Internal use.
    private int nFragmantatedBatches; // The number of batches sent to be processed. 
    private int nProcessedBatches; // The number of samples wich the gpu has proccesed.
    private int nTrainedBatches; // The number of samples wich the gpu has proccesed.

    public Data(String name, Type type, int size) {
        this.name = name;
        this.type = type;
        this.size = size; 
    }
    
    /**
     * Returns the type of the data;
     * @return this.data.
     * 
     * @pre none
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    public Type getType() {
        return this.type;
    }

    /**
     * Returns the name of the data;
     * @return this.name.
     * 
     * @pre none
     * @inv calling this function doesn't change anything in the class.
     * @post none
     */
    public String getName() {
        return this.name;
    }


    /**
     * Create next batch.
     * @param gpu The GPU who created the batch
     * @return
     * @throws IllegalArgumentException
     */
    public DataBatch createBatch(GPU gpu) throws IllegalArgumentException{
        DataBatch batch = null;

        if (this.isFragmantationFinished() | gpu == null) {
            throw new IllegalArgumentException();
        }

        // Create the batch.
        batch = new DataBatch(gpu, this, this.nFragmantatedBatches*BATCH_SIZE); 
        this.nFragmantatedBatches += 1;

        return batch;
    }
    public boolean isFragmantationFinished() {
        return this.nFragmantatedBatches * BATCH_SIZE == this.size;
    }

    public void batchProcessed() throws IllegalArgumentException {
        
        if (this.isProcessingFinished() | this.nProcessedBatches >= this.nFragmantatedBatches) {
            throw new IllegalArgumentException();
        }

        this.nProcessedBatches += 1;
    }
    public boolean isProcessingFinished() {
        return this.nProcessedBatches * BATCH_SIZE == this.size;
    }

    public void batchTrained()  throws IllegalCallerException {
        
        if (this.isTrainingFinished() | this.nTrainedBatches >= this.nFragmantatedBatches | this.nTrainedBatches >= this.nProcessedBatches) {
            throw new IllegalCallerException();
        }
        this.nTrainedBatches += 1;
    }
    public boolean isTrainingFinished() {
        return this.nTrainedBatches * BATCH_SIZE == this.size;
    }
}
