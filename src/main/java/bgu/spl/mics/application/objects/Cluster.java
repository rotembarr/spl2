package bgu.spl.mics.application.objects;


/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	static Cluster cluster;
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		if (cluster == null) {
			cluster = new Cluster();
		}
		return cluster;
	}

	public int getNumOfBatchesProcessedByCPUs() {
		return 0;
	}

	public int getNumOfBatchesWaitingToProcess() {
		return 0;
	}

	public void pushBatchToProcess(DataBatch batch){
		// TODO
	}

	public DataBatch popBatchToProcess(){
		// TODO
		return null;
	}

	public void pushProcessedBatch(DataBatch batch){
	}

	public DataBatch popProcessedBatch(GPU gpu){
		// TODO
		return null;
	}

	public boolean processedBatchExist(GPU gpu) {
		return false;
	}
}
