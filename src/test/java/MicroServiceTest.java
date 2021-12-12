import static org.junit.Assert.assertTrue;

import org.junit.Test;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleBroadcast2;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.messages.ExampleEvent2;

public class MicroServiceTest extends MicroService{

    private MessageBusImpl messageBus = null;

    public MicroServiceTest(String name) {
        super(name);
        this.messageBus = MessageBusImpl.getInstance();
    }

    protected void initialize() {
        this.messageBus.register(this);
        assertTrue("Couldnt subscribe event", this.messageBus.isRegister(this));
        
    }


    @Test
    public void testSanity() {
        System.out.println("sss");
    }

    @Test
    public void testSubscribe() {
        System.out.println("sss");
        super.<String, ExampleEvent>subscribeEvent(ExampleEvent.class, new Callback<ExampleEvent>() {
            public void call(ExampleEvent c) {
                System.out.println(c.getSenderName());
            }
        });
        super.<String, ExampleEvent2>subscribeEvent(ExampleEvent2.class, new Callback<ExampleEvent2>() {
            public void call(ExampleEvent2 c) {
                System.out.println(c.getSenderName());
            }
        });
        super.<ExampleBroadcast>subscribeBroadcast(ExampleBroadcast.class, new Callback<ExampleBroadcast>() {
            public void call(ExampleBroadcast c) {
                System.out.println(c.getSenderId());
            }
        });
        super.<ExampleBroadcast2>subscribeBroadcast(ExampleBroadcast2.class, new Callback<ExampleBroadcast2>() {
            public void call(ExampleBroadcast2 c) {
                System.out.println(c.getSenderId());
            }
        });
        assertTrue("Couldnt subscribe event", super.isSubscribedEvent(ExampleEvent.class));
        assertTrue("Couldnt subscribe event", super.isSubscribedEvent(ExampleEvent2.class));
        assertTrue("Couldnt subscribe event", this.messageBus.isSubscribedEvent(ExampleEvent.class, this));
        assertTrue("Couldnt subscribe event", this.messageBus.isSubscribedEvent(ExampleEvent2.class, this));
        assertTrue("Couldnt subscribe Broadcast", super.isSubscribedBroadcast(ExampleBroadcast.class));
        assertTrue("Couldnt subscribe Broadcast", super.isSubscribedBroadcast(ExampleBroadcast2.class));
        assertTrue("Couldnt subscribe Broadcast", this.messageBus.isSubscribedBroadcast(ExampleBroadcast.class, this));
        assertTrue("Couldnt subscribe Broadcast", this.messageBus.isSubscribedBroadcast(ExampleBroadcast2.class, this));
    }
}
