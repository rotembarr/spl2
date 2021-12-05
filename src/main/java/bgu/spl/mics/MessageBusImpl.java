package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;

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
    public MessageBus getInstance() {
		return null;
	}


    /**
     * Indication if {@code m} register.
     * @param m
     * @return boolean
     * 
     * @pre none
     * @post trivial
	 * 
	 */
	boolean isRegister(MicroService m){
		return false;
	}

    /**
     * Indication if {@code m} register to {@code type} event.
     * @param <T>
     * @param type
     * @param m
     * @return boolean
     * 
     * @pre none
     * @post trivial
     */
    public <T> boolean isSubscribedEvent(Class<? extends Event<T>> type, MicroService m) {
		return false;
	}
    

    /**
     * Indication if {@code m} register to {@code type} event.
     * @param type
     * @param m
     * @return boolean
     * 
     * @pre none
     * @post trivial
     */
    public boolean isSubscribedBroadcast(Class<? extends Broadcast> type, MicroService m) {
		return false;	
	}
   
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

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
		return new ExampleBroadcast("asdasdas");
	}
	

}
