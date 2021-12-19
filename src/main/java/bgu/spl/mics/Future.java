package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	boolean done;
	T returnObj;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.done = false;
		this.returnObj = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
	 * 
	 * @pre none
	 * @post {@return} = this.returnObj.
     */
	public synchronized T get() {
		if (!this.isDone()) {
			try {
				this.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		return this.returnObj;
	}
	
	/**
     * Resolves the result of this Future object.
	 * 
	 * @pre this.isDone() = false
	 * @post if (!this.isDone()) this.get() = {@param result}
     */
	public synchronized void resolve (T result) {
		if (!this.isDone()) {
			this.returnObj = result;
		}
		this.done = true;
		this.notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
	 * @pre none
	 * @post @pre(this.done) = @post(this.done)
     */
	public synchronized boolean isDone() {
		return this.done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
	 * 
	 * @pre none
	 * @post {@return} = this.returnObj.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		long waitTime = TimeUnit.MILLISECONDS.convert(timeout, unit);

		if (!this.isDone()) {
			try {
				this.wait(waitTime);
			} catch (Exception e) {
				e.printStackTrace(); // TODO
			}
		}

		return this.returnObj;		
	}

}
