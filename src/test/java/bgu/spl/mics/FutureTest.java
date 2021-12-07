package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private static Future<String> future;

    @Before
    public void setUp() throws Exception {
        future = new Future<String>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        future.resolve("The future is resolve");
        assertTrue(future.isDone());
    }

    @Test
    public void testResolve() {
        String ans = "The future is resolve";
        String badAns = "bla bla";

        future.resolve(ans);
        assertTrue(future.isDone());
        assertEquals(ans, future.get());

        // Resolve again and see whats happend. 
        future.resolve(badAns);
        assertTrue(future.isDone());
        assertEquals(ans, future.get());
    }


    @Test
    public void testGet() throws InterruptedException {
        String ans = "The future is resolve";
        String[] ret = {null};
        Thread resolve = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.resolve(ans);
        });
        Thread get = new Thread(() -> {
            ret[0] = future.get();
        });

        resolve.start();
        get.start();
        get.join();
        assertEquals(ans, ret[0]);
    }


    @Test
    public void testGetWithTimeOut() {
        String ans = "The future is resolve";
        final String[] ret = {null,null};
        Thread resolve = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                assertTrue("Test failed - sleep exception", false);
            }
            future.resolve(ans);
        });
        Thread get = new Thread(() -> {
            ret[0] = future.get(10, TimeUnit.MILLISECONDS);
            ret[1] = future.get(60, TimeUnit.MILLISECONDS);
        });
        resolve.start();
        get.start();
        
        try {
            get.join();
        } catch (InterruptedException e) {
            assertTrue("Test failed - thread join exception", false);
        }

        assertNull("The future is resolve", ret[0]);
        assertEquals("The future is resolve", ret[1]);

    }
}