import org.junit.Before;
import org.junit.Test;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.example.ExampleMicroService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleBroadcast2;

public class MicroServiceTest {
    MicroService ms = null;

    @Test
    public void setUp() {
        this.ms = new ExampleMicroService("blabla");

        Callback<? extends Message> callback = new Callback<ExampleBroadcast>() {
            public void call(ExampleBroadcast c) {
                System.out.println(((ExampleBroadcast)c).getSenderId());
            }
        };
        ExampleBroadcast b = new ExampleBroadcast("10");
        Callback<ExampleBroadcast> callback2 = callback;
        callback2.call(b);
    }

    @Test
    public void aa() {
        this.ms = new ExampleMicroService("blabla");

        Callback<Message> callback = new Callback<Message>() {
            public void call(Message c) {
                System.out.println(((ExampleBroadcast)c).getSenderId());
            }
        };
        ExampleBroadcast b = new ExampleBroadcast("10");
        callback.call((Message)b);
    
    }
}
