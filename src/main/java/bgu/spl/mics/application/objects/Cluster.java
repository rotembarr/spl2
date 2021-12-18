package bgu.spl.mics.application.objects;

import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {


	// Local variables
	private Vector<GPU> gpus;
	private Vector<CPU> cpus;
	private Queue<DataBatch> unprocessedBatches;
	private Map<GPU, Queue<DataBatch>> gpuToProcessedBatchMap;

	// Statistics.
	private AtomicInteger nOfBatchesProcessedByCPUs;

	private static class SingleToneHolder {
		private static Cluster cluster = new Cluster();
	}

	public Cluster() {
		this.gpus = new Vector<GPU>();
		this.cpus = new Vector<CPU>();
		this.unprocessedBatches = new LinkedBlockingDeque<DataBatch>();
		this.gpuToProcessedBatchMap = new ConcurrentHashMap<GPU, Queue<DataBatch>>();
		this.nOfBatchesProcessedByCPUs = new AtomicInteger(0);
	}

	/**
	 * Add GPU
	 * @param gpu
	 * @pre none
	 * @post gpus=pre gpus+1
	 *
	 */
	public void addGPU(GPU gpu) {
		if (gpu == null) {
			return;
		}

		this.gpus.add(gpu);
		Queue<DataBatch> queue = new ArrayBlockingQueue<DataBatch>(GPU.MAX_PROCESSED_DATA_BATCH_STORED + 1);
		this.gpuToProcessedBatchMap.put(gpu, queue);
	}
	/**
	 * Add CPU
	 * @param cpu
	 * @pre none
	 * @post cpus=pre cpus+1
	 *
	 */
	public void addCPU(CPU cpu) {
		if (cpu == null) {
			return;
		}

		this.cpus.add(cpu);
	}
	
	/**
     * Retrieves the single instance of this class.
     */
	public static Cluster getInstance() {
		return SingleToneHolder.cluster;
	}

	public int getNumOfBatchesProcessedByCPUs() {
		return this.nOfBatchesProcessedByCPUs.get();
	}

	public int getNumOfBatchesWaitingToProcess() {
		return this.unprocessedBatches.size();
	}
	/**
	 * @pre none
	 * @post nOfBatchesProcessedByCPUs.get()==0;
	 */
	public void clearStatistics() {
		this.nOfBatchesProcessedByCPUs.set(0); // TODO
	}
	/**
	 * @pre none
	 * @post unprocessedBatches.size()=pre unprocessedBatches.size()+1
	 */
	public void pushBatchToProcess(DataBatch batch){
		this.unprocessedBatches.add(batch);
	}
	/**
	 * @pre none
	 * @post if exists, unprocessedBatches.size()=pre unprocessedBatches.size()-1
	 */
	public DataBatch popBatchToProcess(){
		return this.unprocessedBatches.poll();
	}
	/**
	 * @pre none
	 * @post this.gpuToProcessedBatchMap.get(batch.getGPU().size()= pre this.gpuToProcessedBatchMap.get(batch.getGPU().size()+1
	 * && nOfBatchesProcessedByCPUs= pre nOfBatchesProcessedByCPUs+1
	 */
	public void pushProcessedBatch(DataBatch batch){
		Queue<DataBatch> queue = this.gpuToProcessedBatchMap.get(batch.getGPU());
		queue.add(batch);
		this.nOfBatchesProcessedByCPUs.incrementAndGet();
	}
	/**
	 * @pre none
	 * @post this.gpuToProcessedBatchMap.get(batch.getGPU().size()= pre this.gpuToProcessedBatchMap.get(batch.getGPU().size()-1
	 */
	public DataBatch popProcessedBatch(GPU gpu){
		Queue<DataBatch> queue = this.gpuToProcessedBatchMap.get(gpu);
		return queue.poll();
	}

	public boolean processedBatchExist(GPU gpu) {
		return !this.gpuToProcessedBatchMap.get(gpu).isEmpty();
	}
}
