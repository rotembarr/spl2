package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.Theories;

import javax.swing.event.MouseInputListener;
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
        future.resolve("The future is resolve");
        assertTrue(future.isDone());

    }


    @Test
    public void testGet() throws InterruptedException {
        Thread resolve = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.resolve("The future is resolve");
        });
        Thread get = new Thread(() -> {
            assertEquals("The future is resolve", future.get());
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        resolve.start();
        get.start();

    }


    @Test
    public void testGetWithTimeOut() {
        Thread resolve = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            future.resolve("The future is resolve");
        });
        Thread get = new Thread(() -> {
            assertNull("The future is resolve", future.get(100, TimeUnit.MILLISECONDS));
            assertEquals("The future is resolve", future.get(600, TimeUnit.MILLISECONDS));
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        resolve.start();
        get.start();

    }
}