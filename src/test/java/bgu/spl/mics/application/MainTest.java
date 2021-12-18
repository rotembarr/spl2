package bgu.spl.mics.application;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

public class MainTest {
    public MainTest() {

    }


    @Test
    public void test() {
        CRMSRunner dut = new CRMSRunner("/users/studs/bsc/2022/gevk/prpjects/spl2/example_input.json");
        dut.run();
        dut.log("/users/studs/bsc/2022/gevk/prpjects/spl2/output.json");
    }
}