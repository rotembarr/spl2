package bgu.spl.mics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.example.messages.ExampleBroadcast;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	// Local Variables.
	static MessageBusImpl messageBus = null;
	Map<MicroService, List<Class<? extends Message>>> serviceToMesssageMap;
	Map<Class<? extends Message>, List<MicroService>> broadcastToServiceMap;
	// Map<Class<? extends Message>, Pair<Integer, List<MicroService>>> eventToServiceMap;
	Map<MicroService, BlockingQueue<Message>> serviceToQueueMap;
	Map<Event<?>, Future<?>> eventToFutureMap;


	/**
	 * Sole constructor.
	 * @return
	 */
	public MessageBusImpl() {
		this.serviceToMesssageMap = new HashMap<MicroService, List<Class<? extends Message>> >();
		this.serviceToQueueMap = new HashMap<MicroService, BlockingQueue<Message>>();
		this.eventToFutureMap = new HashMap<Event<?>, Future<?>>();
	}

    /**
     * 
     * @return a singltone MessageBus object
     * @post @result != null
     *  &&   @result = @post(getInstance) 
     */
    public static MessageBusImpl getInstance() {
		if (messageBus == null) {
			messageBus = new MessageBusImpl();
		}
		return messageBus;
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
	public synchronized boolean isRegister(MicroService m){
		if (m == null) {
			return false;
		}
		return this.serviceToMesssageMap.containsKey(m);
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
    public synchronized <T> boolean isSubscribedEvent(Class<? extends Event<T>> type, MicroService m) {
		// First, check if the service exists.
		if (!this.isRegister(m)) {
			return false;
		}

		// Second, check if type is register
		List< Class<? extends Message> > types = this.serviceToMesssageMap.get(m);
		return types.contains(type);
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
		// First, check if the service exists.
		if (!this.isRegister(m)) {
			return false;
		}

		// Second, check if type is register
		List< Class<? extends Message> > types = this.serviceToMesssageMap.get(m);
		return types.contains(type);	}
   
	/**
     * @pre this.isRegister({@param m}) = true
     * @post @post(this.isSubscribedEvent({@param type}, {@param m})) = true 
	 */
	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// If the service haven't register - do nothing.
		if (!this.isRegister(m)) {
			return;
		}

		// If he service register - add type param
		List< Class<? extends Message> > types = this.serviceToMesssageMap.get(m);
		types.add(type);
	}

	/**
     * @pre this.isRegister({@param m}) = true
     * @post @post(this.isSubscribedBroadcast({@param type}, {@param m})) = true 
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// If the service haven't register - do nothing.
		if (!this.isRegister(m)) {
			return;
		}

		// If he service register - add type param
		List< Class<? extends Message> > types = this.serviceToMesssageMap.get(m);
		types.add(type);

	}

	/**
     * @pre none 
     * @post if {@code e} = null or !this.hashMap.find({@code e}) - do nothing, 
     *  else - resolve linked future
	 */
	@Override
	public synchronized <T> void complete(Event<T> e, T result) {
		// Check if there is event ti resolve
		if (e == null || !this.eventToFutureMap.containsKey(e)) {
			return;
		}

		// Get and remove the event.
		Future<T> future = (Future<T>)this.eventToFutureMap.get(e); // TODO - check cast
		this.eventToFutureMap.remove(e);

		// Resolve the event.
		future.resolve(result);
	}

	/**
     * @pre {@code b != null}
     *  
	 */
	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		if (b == null) {
			return;
		} // TODO

	}

	/**	
	 * @pre {@code e} != null
	 * @post {@code }
	 */	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if (e == null || this.eventToFutureMap.containsKey(e)) {
			return null;
		} // TODO

		Future<T> future = new Future<T>();
		this.eventToFutureMap.put(e, future);
		return future;
	}

	/**
	 * @pre m != null
	 * @post this.isRegiter = true
	 */
	@Override
	public synchronized void register(MicroService m) {
		// Don't register a null service or already register service.
		if (m == null || this.serviceToMesssageMap.containsKey(m)) {
			return;
		}

		// Registration.
		List<Class<? extends Message>> list = new ArrayList<Class<? extends Message>>();
		BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
		this.serviceToMesssageMap.put(m, list);
		this.serviceToQueueMap.put(m, queue);
	}

	/**
	 * @pre none
	 * @post this.isRegister(m) = false.
	 */
	@Override
	public void unregister(MicroService m) {
		// Ignore null services.
		if (m == null) {
			return;
		}

		// Unregistration.
		this.serviceToMesssageMap.remove(m);
		this.serviceToQueueMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!this.isRegister(m)) {
			throw new InterruptedException();
		}

		// Poll message.
		BlockingQueue<Message> queue = this.serviceToQueueMap.get(m);
		return queue.poll();
	}
	

}
