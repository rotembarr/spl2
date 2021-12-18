package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

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

    // Internal use - there is no good reason it will be atomic, but when it wasn't. it cause a bug.
    private AtomicInteger nFragmantatedBatches; // The number of batches sent to be processed. 
    private AtomicInteger nProcessedBatches; // The number of samples witch the gpu has proccesed.
    private AtomicInteger nTrainedBatches; // The number of samples witch the gpu has proccesed.

    public Data(String name, Type type, int size) {
        this.name = name;
        this.type = type;
        this.size = size; 
        this.nFragmantatedBatches = new AtomicInteger();
        this.nProcessedBatches = new AtomicInteger();
        this.nTrainedBatches = new AtomicInteger();
    }

    public void recreateAtomicIntegres() {
        this.nFragmantatedBatches = new AtomicInteger();
        this.nProcessedBatches = new AtomicInteger();
        this.nTrainedBatches = new AtomicInteger();
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
     * @pre gpu != null && !this.isFragmantationFinished()
     * @post nFragmantatedBatches= pre nFragmantatedBatches+1
     */
    public DataBatch createBatch(GPU gpu) throws IllegalArgumentException{
        DataBatch batch = null;

        if (this.isFragmantationFinished() | gpu == null) {
            throw new IllegalArgumentException();
        }

        // Create the batch.
        batch = new DataBatch(gpu, this, this.nFragmantatedBatches.intValue()*BATCH_SIZE); 
        this.nFragmantatedBatches.incrementAndGet();

        return batch;
    }
    public boolean isFragmantationFinished() {
        return this.nFragmantatedBatches.intValue() * BATCH_SIZE == this.size;
    }
    /**
     * @pre !this.isProcessingFinished() && this.nProcessedBatches < this.nFragmantatedBatches
     * @post nProcessedBatches= pre nProcessedBatches+1
     */
    public void batchProcessed() throws IllegalArgumentException {
        
        if (this.isProcessingFinished() | this.nProcessedBatches.intValue() >= this.nFragmantatedBatches.intValue()) {
            throw new IllegalArgumentException();
        }

        this.nProcessedBatches.incrementAndGet();
    }
    public boolean isProcessingFinished() {
        return this.nProcessedBatches.intValue() * BATCH_SIZE == this.size;
    }
    /**
     * @pre !this.isTrainingFinished() && this.nTrainedBatches < this.nFragmantatedBatches
     * @post nTrainedBatches= pre nTrainedBatches+1
     */
    public void batchTrained()  throws IllegalArgumentException {
        
        if (this.isTrainingFinished() | this.nTrainedBatches.intValue() >= this.nFragmantatedBatches.intValue() | this.nTrainedBatches.intValue() >= this.nProcessedBatches.intValue()) {
            System.out.println("bug " + this.getName() + " " + this.nFragmantatedBatches + " " + this.nProcessedBatches + " " + this.nTrainedBatches);

            throw new IllegalArgumentException(this.isTrainingFinished()+ " " + (this.nTrainedBatches.intValue() >= this.nFragmantatedBatches.intValue()) + " " + (this.nTrainedBatches.intValue() >= this.nProcessedBatches.intValue()));
            
        }
        this.nTrainedBatches.incrementAndGet();
    }
    public boolean isTrainingFinished() {
        return this.nTrainedBatches.intValue() * BATCH_SIZE == this.size;
    }
}
