package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    final static int BATCH_SIZE = 1000;
    // Enum representing the Data type.
    enum Type {
        Images, Text, Tabular
    }

    private Type type; // Type of the data.
    private int dataSize;

    // Internal use.
    private int nFragmantatedBatches; // The number of batches sent to be processed. 
    private int nProcessedBatches; // The number of samples wich the gpu has proccesed.
    private int nTrainedBatches; // The number of samples wich the gpu has proccesed.

    public Data(Type t) {
        this.type = t;
        
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

    public DataBatch createBatch(GPU gpu) throws IllegalCallerException{
        DataBatch batch = null;

        if (this.isFragmantationFinished()) {
            throw new IllegalCallerException();
        }

        // Create the batch.
        batch = new DataBatch(gpu, this, this.nFragmantatedBatches*BATCH_SIZE); 
        this.nFragmantatedBatches += 1;

        return batch;
    }
    public boolean isFragmantationFinished() {
        return this.nFragmantatedBatches * BATCH_SIZE == this.dataSize;
    }

    public void batchProcessed() throws IllegalCallerException {
        
        if (this.isProcessingFinished() | this.nProcessedBatches >= this.nFragmantatedBatches) {
            throw new IllegalCallerException();
        }

        this.nProcessedBatches += 1;
    }
    public boolean isProcessingFinished() {
        return this.nProcessedBatches * BATCH_SIZE == this.dataSize;
    }

    public void batchTrained()  throws IllegalCallerException {
        
        if (this.isTrainingFinished() | this.nTrainedBatches >= this.nFragmantatedBatches | this.nTrainedBatches >= this.nProcessedBatches) {
            throw new IllegalCallerException();
        }
        this.nTrainedBatches += 1;
    }
    public boolean isTrainingFinished() {
        return this.nTrainedBatches * BATCH_SIZE == this.dataSize;
    }
}
