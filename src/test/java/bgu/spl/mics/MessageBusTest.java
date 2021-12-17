package bgu.spl.mics;

import bgu.spl.mics.example.ExampleMicroService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleBroadcast2;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.messages.ExampleEvent2;
import bgu.spl.mics.example.services.ExampleMessageSenderService; 
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.Future;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class MessageBusTest extends MessageBusImpl {

    @Test
    public void TestsubscribeBroadcast_Base() {
        String[] stringTest={"10"};

        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);

        super.subscribeBroadcast(ExampleBroadcast.class,M);
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class,M));

        super.register(M);
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class,M));

        super.subscribeBroadcast(ExampleBroadcast.class,M);
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,M));

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
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class,M));
    }


    @Test
    public void TestsubscribeBroadcast_Multiple() {
        String[] stringTest={"10"};

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
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms1));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms2));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms3));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms4));

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
        String[] stringTest={"event"};

        MicroService M = new ExampleMessageSenderService("mor",stringTest);

        super.subscribeEvent(ExampleEvent.class,M);
        assertFalse(super.isSubscribedEvent(ExampleEvent.class,M));

        super.register(M);
        assertFalse(super.isSubscribedEvent(ExampleEvent.class,M));

        super.subscribeEvent(ExampleEvent.class,M);
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,M));

        Event<String> b= new ExampleEvent("2");
        super.sendEvent(b);

        Event<String> ans = null;
        try {
            ans = (Event<String>)(super.awaitMessage(M));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }
        assertEquals(b, ans);

        super.unregister(M);
        assertFalse(super.isSubscribedEvent(ExampleEvent.class,M));
    }


    @Test
    public void TestsubscribeEvent_MultipleServices() {
        String[] stringTest={"event"};

        MicroService ms1= new ExampleMessageSenderService("ms1",stringTest);
        MicroService ms2= new ExampleMessageSenderService("ms2",stringTest);
        MicroService ms3= new ExampleMessageSenderService("ms3",stringTest);

        super.register(ms1);
        super.register(ms2);
        super.register(ms3);

        super.subscribeEvent(ExampleEvent.class, ms1);
        super.subscribeEvent(ExampleEvent.class, ms2);
        super.subscribeEvent(ExampleEvent.class, ms3);
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms1));
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms2));
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms3));

        ExampleEvent e1 = new ExampleEvent("e1");
        ExampleEvent e2 = new ExampleEvent("e2");
        ExampleEvent e3 = new ExampleEvent("e3");
        ExampleEvent e4 = new ExampleEvent("e4");
        ExampleEvent e5 = new ExampleEvent("e5");
        ExampleEvent e6 = new ExampleEvent("e6");
        super.sendEvent(e1);
        super.sendEvent(e2);
        super.sendEvent(e3);
        super.sendEvent(e4);
        super.sendEvent(e5);
        super.sendEvent(e6);

        try {
            assertEquals(e1, (ExampleEvent)(super.awaitMessage(ms1)));
            assertEquals(e2, (ExampleEvent)(super.awaitMessage(ms2)));
            assertEquals(e3, (ExampleEvent)(super.awaitMessage(ms3)));
            assertEquals(e4, (ExampleEvent)(super.awaitMessage(ms1)));
            assertEquals(e5, (ExampleEvent)(super.awaitMessage(ms2)));
            assertEquals(e6, (ExampleEvent)(super.awaitMessage(ms3)));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }
    }



    @Test
    public void TestsubscribeEvent_MultipleEventTypes() {
        String[] stringTest={"event"};

        MicroService ms= new ExampleMessageSenderService("ms1",stringTest);
        super.register(ms);

        super.subscribeEvent(ExampleEvent.class, ms);
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms));

        super.subscribeEvent(ExampleEvent2.class, ms);
        assertTrue(super.isSubscribedEvent(ExampleEvent2.class,ms));
        
        ExampleEvent  e1 = new ExampleEvent("e1");
        ExampleEvent2 e2 = new ExampleEvent2("e2");
        ExampleEvent  e3 = new ExampleEvent("e3");
        ExampleEvent2 e4 = new ExampleEvent2("e4");
        
        super.sendEvent(e1);
        super.sendEvent(e2);
        super.sendEvent(e3);
        super.sendEvent(e4);

        try {
            assertEquals(e1, (super.awaitMessage(ms)));
            assertEquals(e2, (super.awaitMessage(ms)));
            assertEquals(e3, (super.awaitMessage(ms)));
            assertEquals(e4, (super.awaitMessage(ms)));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }

    }

    @Test
    public void TestComplete() {
        List<String> results = new ArrayList<String>();
        List<ExampleEvent> events = new ArrayList<ExampleEvent>();
        List<Future<String>> futures = new ArrayList<Future<String>>();

        // Try to complete null event - its just shouldnt crash.
        super.complete(null, null);
        
        ExampleMicroService m = new ExampleMicroService("m");
        super.register(m);
        super.subscribeEvent(ExampleEvent.class, m);

        // Try to complete non existing event - its just shouldnt crash.
        super.complete(new ExampleEvent("morosh"), null);

        // Standrad complete
        for (int i = 0; i < 100; i++) {
            ExampleEvent event = new ExampleEvent("morosh " + String.valueOf(i));
            events.add(event);
            futures.add(super.sendEvent(event));
            results.add(String.valueOf(i));
        }

        for (int i = 0; i < 100; i++) {
            super.complete(events.get(i), results.get(i));
            assertEquals("Complete function error", results.get(i), futures.get(i).get());
        }
    }

    @Test
    public void sendBroadcast() {
        String[] stringTest = {"10"};

        // Try to send null message - its just shouldnt crash.
        super.sendBroadcast(null);
        
        // Try to send unsubscribed message - its just shouldnt crash.
        super.sendBroadcast(new ExampleBroadcast("2"));

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
        super.subscribeBroadcast(ExampleBroadcast2.class, ms1);
        super.subscribeBroadcast(ExampleBroadcast2.class, ms2);

        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms1));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms2));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms3));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class,ms4));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast2.class,ms1));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast2.class,ms2));

        Broadcast b = new ExampleBroadcast("1");        
        super.sendBroadcast(b);
        try {
            assertEquals(b, (Broadcast)(super.awaitMessage(ms1)));
            assertEquals(b, (Broadcast)(super.awaitMessage(ms2)));
            assertEquals(b, (Broadcast)(super.awaitMessage(ms3)));
            assertEquals(b, (Broadcast)(super.awaitMessage(ms4)));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }


        Broadcast b2 = new ExampleBroadcast2("2");        
        super.sendBroadcast(b2);
        try {
            assertEquals(b2, (Broadcast)(super.awaitMessage(ms1)));
            assertEquals(b2, (Broadcast)(super.awaitMessage(ms2)));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }

    }

    @Test
    public void sendEvent() {
        String[] stringTest = {"event"};

        // Try to send null message - its just shouldnt crash.
        super.sendEvent(null);
        
        // Try to send unsubscribed message - its just shouldnt crash.
        super.sendEvent(new ExampleEvent("1"));


        MicroService ms1= new ExampleMessageSenderService("ms1",stringTest);
        MicroService ms2= new ExampleMessageSenderService("ms2",stringTest);
        MicroService ms3= new ExampleMessageSenderService("ms3",stringTest);
        super.register(ms1);
        super.register(ms2);
        super.register(ms3);

        super.subscribeEvent(ExampleEvent.class, ms1);
        super.subscribeEvent(ExampleEvent.class, ms2);
        super.subscribeEvent(ExampleEvent.class, ms3);
        super.subscribeEvent(ExampleEvent2.class, ms1);


        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms1));
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms2));
        assertTrue(super.isSubscribedEvent(ExampleEvent.class,ms3));
        assertTrue(super.isSubscribedEvent(ExampleEvent2.class,ms1));

        List<Event<String>> events = new ArrayList<Event<String>>();
        Event<String> event = null;
        for (int i = 0; i < 100; i++) {
            if ((int)(Math.random()*100) + 1 < 50) {
                event = new ExampleEvent("e" + String.valueOf(i));
            } else {
                event = new ExampleEvent2("e" + String.valueOf(i));
            }
            events.add(event);
            super.sendEvent(event);
        }

        try {
            int event1Cnt = 0;
            for (int i = 0; i < 100; i++) {
                if (events.get(i) instanceof ExampleEvent) {
                    if (event1Cnt % 3 == 0) {
                        assertEquals(events.get(i), (ExampleEvent)(super.awaitMessage(ms1)));
                    } else if (event1Cnt % 3 == 1) {
                        assertEquals(events.get(i), (ExampleEvent)(super.awaitMessage(ms2)));
                    } else {
                        assertEquals(events.get(i), (ExampleEvent)(super.awaitMessage(ms3)));
                    }                    
                    event1Cnt++;
                } else {
                    assertEquals(events.get(i), (ExampleEvent2)(super.awaitMessage(ms1)));
                }
            }

        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }

    }

    @Test
    public void TestRegister() {
        String[] stringTest={"10"};
        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);
        
        assertFalse(super.isRegister(M));
        
        super.register(M);
        assertTrue(super.isRegister(M));
        super.register(M);
        
        assertTrue(super.isRegister(M));
    }

    @Test
    public void TestUnregister() {
        String[] stringTest={"10"};
        MicroService M= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M1= new ExampleBroadcastListenerService("mor",stringTest);
        MicroService M2= new ExampleBroadcastListenerService("mor",stringTest);

        super.unregister(M1);//check if we can unregister a microservice without register it first

        super.register(M);
        super.subscribeBroadcast(ExampleBroadcast.class, M);
        assertTrue(super.isRegister(M));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast.class, M));
        super.register(M2);
        super.subscribeBroadcast(ExampleBroadcast2.class, M2);
        assertTrue(super.isRegister(M2));
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class, M2));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast2.class, M2));

        super.unregister(M);
        assertFalse(super.isRegister(M));
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class, M));
        assertTrue(super.isRegister(M2));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast2.class, M2));
        super.unregister(M);
        assertFalse(super.isRegister(M));
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class, M));
        assertTrue(super.isRegister(M2));
        assertTrue(super.isSubscribedBroadcast(ExampleBroadcast2.class, M2));
        super.unregister(M2);
        assertFalse(super.isRegister(M));
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast.class, M));
        assertFalse(super.isRegister(M2));
        assertFalse(super.isSubscribedBroadcast(ExampleBroadcast2.class, M2));
    }
    
    @Test
    public void TestMultiRegister() {
        String[] stringTest={"10"};
 
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
    public void TestAwaitMessage_NullArguent() {
        assertThrows(IllegalArgumentException.class, ()->{
            super.awaitMessage(null);
        });
    }


    @Test
    public void TestAwaitMessage_UnregisterService() {
        assertThrows(IllegalArgumentException.class, ()->{
            String[] args = {"10"};
            super.awaitMessage(new ExampleBroadcastListenerService("mor",args));
        });
    }


    @Test
    public void TestAwaitMessage_Interupt() {
        final Boolean[] exceptionDetected = {false};
        
        Thread msg = new Thread(() -> {
            boolean d = false;
            try {
                String[] args = {"10"};
                super.awaitMessage(new ExampleBroadcastListenerService("mor",args));
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    d = true;
                }
            }
            exceptionDetected[0] = d;
        });

        Thread sleep = new Thread(()->{
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                assertTrue("Test failed - sleep exception", false);
            }
            msg.interrupt();
        });

        sleep.start();
        msg.start();

        try {
            msg.join();
        } catch (Exception e) {
            assertTrue("Test failed - join exception", false);
        }
        
        assertTrue("InterruptedException wasnt detedcted", exceptionDetected[0]);

    }

    @Test // awaitMessage function tested in all the other tests.
    public void TestAwaitMessage_Delay() {
        String[] args = {"10"};
        
        MicroService M = new ExampleBroadcastListenerService("mor",args);
        super.register(M);
        super.subscribeBroadcast(ExampleBroadcast.class, M);
        
        Broadcast b = new ExampleBroadcast("morosh");
        Broadcast ans = null; 

        Thread wait = new Thread(()->{
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                throw new InternalError();
            }

            super.sendBroadcast(b);
        });

        wait.start();
        // try {
        //     wait.join();
        // } catch (Exception e) {
        //     throw new InternalError();
        // }
        
        try {
            assertEquals(b, (Broadcast)super.awaitMessage(M));
        } catch (Exception e) {
            assertTrue("Test failed - await message exception", false);
        }


    }
}