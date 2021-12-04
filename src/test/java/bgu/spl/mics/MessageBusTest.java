package bgu.spl.mics;

import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleMessageSenderService; 
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MessageBusTest extends MessageBusImpl {


    @Test
    public void TestsubscribeEvent() {

    }

    @Test
    public void TestsubscribeBroadcast_Base() {
        String[] stringTest=null;

        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);

        super.subscribeBroadcast(ExampleBroadcast.class,M);
        assertFalse(super.isSubscibedBrodcast(ExampleBroadcast.class,M));

        super.register(M);
        assertFalse(super.isSubscibedBrodcast(ExampleBroadcast.class,M));

        super.subscribeBroadcast(ExampleBroadcast.class,M);
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,M));

        Broadcast b= new ExampleBroadcast("2");
        super.sendBroadcast(b);

        Broadcast ans = null;
        try {
            ans = (Broadcast)(super.awaitMessage(M));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }
        assertEquals(b, ans);

        super.unregister(M);
        assertFalse(super.isSubscibedBrodcast(ExampleBroadcast.class,M));
    }


    @Test
    public void TestsubscribeBroadcast_Multiple() {
        String[] stringTest=null;

        MicroService ms1= new ExampleBroadcastListenerService("ms1",stringTest);
        MicroService ms2= new ExampleBroadcastListenerService("ms2",stringTest);
        MicroService ms3= new ExampleBroadcastListenerService("ms3",stringTest);
        MicroService ms4= new ExampleBroadcastListenerService("ms4",stringTest);

        super.register(ms1);
        super.register(ms2);
        super.register(ms3);
        super.register(ms4);

        super.subscribeBroadcast(ExampleBroadcast.class, ms1);
        super.subscribeBroadcast(ExampleBroadcast.class, ms2);
        super.subscribeBroadcast(ExampleBroadcast.class, ms3);
        super.subscribeBroadcast(ExampleBroadcast.class, ms4);
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms1));
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms2));
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms3));
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms4));

        Broadcast b = new ExampleBroadcast("2");
        super.sendBroadcast(b);

        Broadcast ans1 = null;
        Broadcast ans2 = null;
        Broadcast ans3 = null;
        Broadcast ans4 = null;

        try {
            ans1 = (Broadcast)(super.awaitMessage(ms1));
            ans2 = (Broadcast)(super.awaitMessage(ms2));
            ans3 = (Broadcast)(super.awaitMessage(ms3));
            ans4 = (Broadcast)(super.awaitMessage(ms4));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }

        assertEquals(b, ans1);
        assertEquals(b, ans2);
        assertEquals(b, ans3);
        assertEquals(b, ans4);
    }

    // SubscribedEvent.
    @Test
    public void TestsubscribeEvent_Base() {
        String[] stringTest=null;

        MicroService M = new ExampleMessageSenderService("mor",stringTest);

        super.subscribeEvent(ExampleEvent.class,M);
        assertFalse(super.isSubscribedEvent(ExampleEvent.class,M));

        super.register(M);
        assertFalse(super.isSubscribedEvent(ExampleEvent.class,M));

        super.subscribeBroadcast(ExampleEvent.class,M);
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,M));

        Broadcast b= new ExampleMessageSenderService("2");
        super.sendBroadcast(b);

        Broadcast ans = null;
        try {
            ans = (Broadcast)(super.awaitMessage(M));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }
        assertEquals(b, ans);

        super.unregister(M);
        assertFalse(super.isSubscribedEvent(ExampleBroadcast.class,M));
    }


    @Test
    public void TestsubscribeBroadcast_Multiple() {
        String[] stringTest=null;

        MicroService ms1= new ExampleBroadcastListenerService("ms1",stringTest);
        MicroService ms2= new ExampleBroadcastListenerService("ms2",stringTest);
        MicroService ms3= new ExampleBroadcastListenerService("ms3",stringTest);
        MicroService ms4= new ExampleBroadcastListenerService("ms4",stringTest);

        super.register(ms1);
        super.register(ms2);
        super.register(ms3);
        super.register(ms4);

        super.subscribeBroadcast(ExampleBroadcast.class, ms1);
        super.subscribeBroadcast(ExampleBroadcast.class, ms2);
        super.subscribeBroadcast(ExampleBroadcast.class, ms3);
        super.subscribeBroadcast(ExampleBroadcast.class, ms4);
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms1));
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms2));
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms3));
        assertTrue(super.isSubscibedBrodcast(ExampleBroadcast.class,ms4));

        Broadcast b = new ExampleBroadcast("2");
        super.sendBroadcast(b);

        Broadcast ans1 = null;
        Broadcast ans2 = null;
        Broadcast ans3 = null;
        Broadcast ans4 = null;

        try {
            ans1 = (Broadcast)(super.awaitMessage(ms1));
            ans2 = (Broadcast)(super.awaitMessage(ms2));
            ans3 = (Broadcast)(super.awaitMessage(ms3));
            ans4 = (Broadcast)(super.awaitMessage(ms4));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }

        assertEquals(b, ans1);
        assertEquals(b, ans2);
        assertEquals(b, ans3);
        assertEquals(b, ans4);
    }




    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void Testregister() {
        String[] stringTest=null;
        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);
        assertFalse(super.isRegister(M));
        super.register(M);
        assertTrue(super.isRegister(M));
        super.register(M);
        assertTrue(super.isRegister(M));


    }

    @Test
    public void Testunregister() {
        String[] stringTest=null;
        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M1= new ExampleBroadcastListenerService("mor",stringTest);
        super.unregister(M1);//check if we can unregister a microservice without register it first
        super.register(M);
        assertTrue(super.isRegister(M));
        super.unregister(M);
        assertFalse(super.isRegister(M));
        super.unregister(M);
        assertFalse(super.isRegister(M));


    }
    
    @Test
    public void TestMultiRegister() {
        String[] stringTest=null;
        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M1= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M2= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M3= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M4= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M5= new ExampleBroadcastListenerService("mor",stringTest);
        super.register(M);
        super.register(M1);
        super.register(M2);
        super.register(M3);
        super.register(M4);
        super.register(M5);
        assertTrue(super.isRegister(M));
        assertTrue(super.isRegister(M1));
        assertTrue(super.isRegister(M2));
        assertTrue(super.isRegister(M3));
        assertTrue(super.isRegister(M4));
        assertTrue(super.isRegister(M5));
        super.unregister(M2);
        super.unregister(M4);
        assertTrue(super.isRegister(M));
        assertTrue(super.isRegister(M1));
        assertFalse(super.isRegister(M2));
        assertTrue(super.isRegister(M3));
        assertFalse(super.isRegister(M4));
        assertTrue(super.isRegister(M5));
        
        
    }

    @Test
    public void TestawaitMessage() {
    }
}