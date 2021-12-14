package bgu.spl.mics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class SingletoneHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	// Local Variables.
	static MessageBusImpl messageBus = null;

	// Registration Handling.
	Map<MicroService, List<Class<? extends Message>>> serviceToMesssageMap;
	Map<MicroService, BlockingQueue<Message>> serviceToQueueMap;
	
	// Future Handling.
	Map<Event<?>, Future<?>> eventToFutureMap;
	
	// Sending Handling.
	Map<Class<? extends Message>, Pair<Integer, List<MicroService>>> eventToServiceMap;
	Map<Class<? extends Message>, List<MicroService>> broadcastToServiceMap;

	/**
	 * Sole constructor.
	 * @return
	 */
	public MessageBusImpl() {
		this.serviceToMesssageMap = new HashMap<MicroService, List<Class<? extends Message>> >();
		this.serviceToQueueMap = new HashMap<MicroService, BlockingQueue<Message>>();
		this.eventToFutureMap = new HashMap<Event<?>, Future<?>>();

		this.eventToServiceMap = new HashMap<Class<? extends Message>, Pair<Integer, List<MicroService>>> ();
		this.broadcastToServiceMap = new HashMap<Class<? extends Message>, List<MicroService>> ();
	}

    /**
     * 
     * @return a singltone MessageBus object
     * @post @result != null
     *  &&   @result = @post(getInstance) 
     */
    public static MessageBusImpl getInstance() {
		return SingletoneHolder.instance;
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
		return types.contains(type);	
	}
   
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
		List<Class<? extends Message> > types = this.serviceToMesssageMap.get(m);
		types.add(type);


		Pair<Integer, List<MicroService>> entry = null;
		if (this.eventToServiceMap.containsKey(type)) {
			entry = this.eventToServiceMap.get(type);
		} else {
			entry = new Pair<Integer, List<MicroService>>(0, new LinkedList<MicroService>());
			this.eventToServiceMap.put(type, entry);
		}
		entry.getSecond().add(m);
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

		List<MicroService> list = null;
		if (this.broadcastToServiceMap.containsKey(type)) {
			list = this.broadcastToServiceMap.get(type);
		} else {
			list = new LinkedList<MicroService>();
			broadcastToServiceMap.put(type, list);
		}
		list.add(m);
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
        System.out.println("event " + e + " completed");
		future.resolve(result);
	}

	/**
     * @pre {@code b != null}
     *  
	 */
	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (b == null) {
			return;
		} // TODO

		if (!this.broadcastToServiceMap.containsKey(b.getClass())) {
			return;
		}

		List<MicroService> list = this.broadcastToServiceMap.get(b.getClass());
		for (Iterator<MicroService> iter = list.iterator(); iter.hasNext();) {
			MicroService microService = iter.next();
			this.serviceToQueueMap.get(microService).add(b);
		}
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


		if (!this.eventToServiceMap.containsKey(e.getClass())) {
			return null;
		} // TODO

		// Create future.
		Future<T> future = new Future<T>();
		this.eventToFutureMap.put(e, future);
		
		// Send the event to one of the queues and advance round robbin counter.
		Pair<Integer, List<MicroService>> pair = this.eventToServiceMap.get(e.getClass());
		MicroService microService =  pair.getSecond().get(pair.getFirst());
		this.serviceToQueueMap.get(microService).add(e);
		Integer cnt = pair.getFirst();
		pair.setFirst((cnt + 1)%pair.getSecond().size());

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


		// Remove m's futures.
		Set<Event<?>> eventSet = eventToFutureMap.keySet();
		for (Iterator<Event<?>> iter = eventSet.iterator(); iter.hasNext();) {
			Event<?> event = iter.next();
			// TODO - need to delete.
		}

		// Remove m's events subsribtion.
		Collection<Pair<Integer, List<MicroService>>> allEventsMicroServices = this.eventToServiceMap.values();
		for (Iterator<Pair<Integer, List<MicroService>>> iter = allEventsMicroServices.iterator(); iter.hasNext();) {
			Pair<Integer, List<MicroService>> pair = iter.next();

			List<MicroService> list = pair.getSecond();
			if (list.contains(m)) {
				int index = list.indexOf(m);
				
				// Update round robbin counter
				if (pair.getFirst() > index) {
					pair.setFirst(pair.getFirst() - 1); // Note that cnt > 0 so its fine just to sub.
				}

				// Remove from list.
				list.remove(index);
			}
		}

		// Remove m's broadcast subsribtion.
		Collection<List<MicroService>> allBroadcastsMicroServices = this.broadcastToServiceMap.values();
		for (Iterator<List<MicroService>> iter = allBroadcastsMicroServices.iterator(); iter.hasNext();) {
			List<MicroService> list = iter.next();
			list.remove(m); // Remove from list (if exists).
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		if (m == null || !this.isRegister(m)) {
			throw new InterruptedException();
		}

		// Poll message.
		BlockingQueue<Message> queue = this.serviceToQueueMap.get(m);
		return queue.take();
	}
	

}
