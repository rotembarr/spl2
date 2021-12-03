package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }

    private Type type; // Type of the data.
    private int dataSize;

    // 
    private int nProcessedData; // The number of samples wich the gpu has proccesed.
    private int nSentBatches; // The number of batches sent to be processed. 


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
}
