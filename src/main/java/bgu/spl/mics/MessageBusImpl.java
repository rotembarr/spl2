package bgu.spl.mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    /**
     * 
     * @return a singltone MessageBus object
     * @post @result != null
     *  &&   @result = @post(getInstance) 
     */
    MessageBus getInstance() {
		return null;
	}

    /**
     * Indication if {@code m} register to {@code type} event.
     * @param <T>
     * @param type
     * @param m
     * @return boolean
     * 
     * @pre {@code m} != null
     * @post none
     * @inv none
     */
    <T> boolean istSubscribedEvent(Class<? extends Event<T>> type, MicroService m) {
		return false;
	}
    

    /**
     * Indication if {@code m} register to {@code type} event.
     * @param type
     * @param m
     * @return boolean
     * 
     * @pre {@code m} != null
     * @post none
     * @inv none
     */
    boolean isSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return false;	
	}
   
	/**
     * @pre {@code m} != null
     *  &&  @pre(this.isScubscribedEvent(type,m)) = false
     * @post @post(this.isScubscribedEvent(type,m)) = true
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	/**
     * @pre {@code m} != null
     *  &&  @pre(this.isScubscribedBroadcast(type,m)) = false
     * @post @post(this.isScubscribedEvent(type,m)) = true
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	/**	
     * @pre {@code e} != null && {@code T} != null
     *  && {@code e} hasn't been completed before
     * @post @post(complete(e, result)) 
	 */
	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	/**
     * @pre {@code b != null}
     *  
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	/**	
	 * @pre {@code e} != null
	 * @post {@code }
	 */	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	boolean isSubscribedBrodcast(Class<? extends Broadcast> b, MicroService m){
		return false;
	}
	<T> boolean isSubscribedEvent(MicroService m, Event<T> e){
		return false;
	}
	boolean isRegister(MicroService m){
		return false;
	}
	

}
