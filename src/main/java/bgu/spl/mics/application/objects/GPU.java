package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.Queue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private Type type;
    private Cluster cluster;
    private Queue<Model> batchesToProcess; // Has no size limit.
    private Queue<DataBatch> processedbatches; // Up to 32

    // Statistics.
    private Queue<String> modelNames;
    private long nOfTimePass;

    public GPU(Type type) {
        this.type = type;
        this.cluster = Cluster.getInstance();
        // modelsName TODO
    }

    /**
     * Returns a copy of model names.
     * @return copy of this.modelNames
     * 
     * @pre none
     * @inv calling this function doesn't change anything in the class.
     * @post {@result} != this.modelNames.
     */
    public Queue<String> getModelNames() {
        return null;
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
     * Calculate the number of ticks process a batch takes according to its type:
     *     - 3090 - 1 ticks.
     *     - 2080 - 2 ticks.
     *     - 1080 - 4 ticks.
     * @param type
     * @return number of ticks.
     * 
     * @pre {@param type} is a valid type
     * @inv calling this function doesn't change anything in the class.
     * @post {@return} != 0
     */
    private int numOfTickToProcess(GPU.Type type) {
        return 0;
    }


}
