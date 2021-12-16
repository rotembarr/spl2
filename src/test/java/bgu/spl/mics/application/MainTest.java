package bgu.spl.mics.application;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

public class MainTest {
    public MainTest() {

    }


    @Test
    public void test() {
        CRMSRunner dut = new CRMSRunner("/home/rotem/projects/spl2/example_input.json");
        dut.run();
    }
}