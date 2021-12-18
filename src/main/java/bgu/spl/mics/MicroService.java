package bgu.spl.mics;

import java.util.HashMap;
import java.util.Map;

import bgu.spl.mics.application.messages.TerminateBroadcast;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 * Derived classes of MicroService should never directly touch the message-bus.
 * Instead, they have a set of internal protected wrapping methods (e.g.,
 * {@link #sendBroadcast(bgu.spl.mics.Broadcast)}, {@link #sendBroadcast(bgu.spl.mics.Broadcast)},
 * etc.) they can use. When subscribing to message-types,
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the micro-service
 * message-queue (see {@link MessageBus#register(bgu.spl.mics.MicroService)}
 * method). The abstract MicroService stores this callback together with the
 * type of the message is related to.
 * 
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class MicroService implements Runnable {
    
    // Global varivables.
    protected MessageBus messageBus = null;
    private final String name;

    // Variables.
    private boolean terminated = false;
	private Map<Class<? extends Message>, Callback<? extends Message>> messageToCallbackMap;

    // Statistics.
    private int sentEvent;
    private int receivedEvent;
    private int sentBroadcast;
    private int receivedBroadcast;
    private int completedEvent;


    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public MicroService(String name) {
        this.terminated = false;
        this.name = name;
        this.messageToCallbackMap = new HashMap<Class<? extends Message>, Callback<? extends Message>>();
        this.messageBus = MessageBusImpl.getInstance();
        
        this.sentEvent = 0;
        this.receivedEvent = 0;
        this.sentBroadcast = 0;
        this.receivedBroadcast = 0;
        this.completedEvent = 0;
    }

    /**
     * Checks if the given type subscribed.
     * @param <T>
     * @param <E>
     * @param type
     * @return
     */
    public <T, E extends Event<T>> boolean isSubscribedEvent(Class<E> type) {
        return this.messageToCallbackMap.containsKey(type);
    }

    /**
     * Checks if the given type subscribed.
     * @param <T>
     * @param <E>
     * @param type
     * @return
     */
    public <B extends Broadcast> boolean isSubscribedBroadcast(Class<B> type) {
        return this.messageToCallbackMap.containsKey(type);
    }

    /**
     * Subscribes to events of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to events in the singleton event-bus using the supplied
     * {@code type}
     * 2. Store the {@code callback} so that when events of type {@code type}
     * are received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <E>      The type of event to subscribe to.
     * @param <T>      The type of result expected for the subscribed event.
     * @param type     The {@link Class} representing the type of event to
     *                 subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     * @pre none
     * @post isSubscribedEvent(Class<E> type)==True
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
        if (this.messageToCallbackMap.containsKey(type)) {
            return;
        }

        this.messageToCallbackMap.put(type, callback);
        this.messageBus.subscribeEvent(type, this);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton event-bus using the
     * supplied {@code type}
     * 2. Store the {@code callback} so that when broadcast messages of type
     * {@code type} received it will be called.
     * <p>
     * For a received message {@code m} of type {@code type = m.getClass()}
     * calling the callback {@code callback} means running the method
     * {@link Callback#call(java.lang.Object)} by calling
     * {@code callback.call(m)}.
     * <p>
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     * @pre none
     * @post isSubscribedBroadcast(Class<B> type)==True
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        if (this.messageToCallbackMap.containsKey(type)) {
            return;
        }

        this.messageToCallbackMap.put(type, callback);
        this.messageBus.subscribeBroadcast(type, this);
    }

    /**
     * Sends the event {@code e} using the message-bus and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			micro-service processing this event.
     * 	       			null in case no micro-service has subscribed to {@code e.getClass()}.
     * @pre {@code e}!=null
     * @post sendEvent=pre sendEvent+1
     */
    protected final <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = this.messageBus.sendEvent(e);
        this.sentEvent++;
        return future;
    }

    /**
     * A Micro-Service calls this method in order to send the broadcast message {@code b} using the message-bus
     * to all the services subscribed to it.
     * <p>
     * @param b The broadcast message to send
     * @pre {@code b}!=null
     * @post sendBroadcast=pre sendBroadcast+1
     */
    protected final void sendBroadcast(Broadcast b) {
        this.messageBus.sendBroadcast(b);
        this.sentBroadcast++;
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the message-bus.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     * @pre none
     * @post completedEvent= pre completedEvent+1
     */
    protected final <T> void complete(Event<T> e, T result) {
        this.messageBus.complete(e, result);
        this.completedEvent++;
    }

    /**
     * this method is called once when the event loop starts.
     * @pre none
     * @post messageBus.isRegister(this)==True&&
     * this.isSubscribedBroadcast(TerminateBroadcast)==True
     *
     */
    protected void initialize() {
        // Register to message bus.
        this.messageBus.register(this);

        // All the services will register this msg and shut themselves down when it comes.
        this.subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
            public void call(TerminateBroadcast b) {
                terminate();
            }
        });
        
    }

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     * @pre none
     * @post messageBus.isRegister(this)==False && terminated=True
     */
    protected final void terminate() {
        // Unrigster this service from message bus and exit.
        this.messageBus.unregister(this);
        this.terminated = true;
    }

    /**
     * @return the name of the service - the service name is given to it in the
     *         construction time and is used mainly for debugging purposes.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * The entry point of the micro-service. TODO: you must complete this code
     * otherwise you will end up in an infinite loop.
     * @pre none
     * @post before terminated:
     *      messageBus.isRegister(this)==True&&
     *      this.isSubscribedBroadcast(TerminateBroadcast)==True&&
     *      receivedBroadcast=pre receivedBroadcast+1
     *      receivedEvent=pre receivedEvent+1
     *      after terminated:
     *      messageBus.isRegister(this)==False && terminated=True
     */
    @Override
    public final void run() {
        initialize();
        while (!terminated) {
            Message message = null;
            
            // Get messages untill end of process.
            try {
                message = this.messageBus.awaitMessage(this);
                
                // Calback handling.
                if (this.messageToCallbackMap.containsKey(message.getClass())) {
                    
                    if (message instanceof Broadcast) {
                        Callback<Broadcast> callback = (Callback<Broadcast>)(Callback<?>)this.messageToCallbackMap.get(message.getClass());
                        callback.call((Broadcast)message);
                        this.receivedBroadcast++;
                    } else {
                        Callback<Event<?>> callback = (Callback<Event<?>>)(Callback<?>)this.messageToCallbackMap.get(message.getClass());
                        callback.call((Event<?>)message);
                        this.receivedEvent++;
                    }
                }

            } catch (InterruptedException e) {
                this.terminate();
            }
        }            
    }

}
