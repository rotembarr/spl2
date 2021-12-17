package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu = null;

    public CPUService(String name, int cores) {
        super(name);
        this.cpu = new CPU(cores);
    }

    public CPU getCpu() {
        return this.cpu;
    }

    @Override
    protected void initialize() {
        super.initialize();

        // Add cpu to the cluster.
        Cluster.getInstance().addCPU(this.cpu);

        // Tick Broadcast.
        super.<TickBroadcast>subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            public void call(TickBroadcast c) {
                cpu.tickSystem();
            }
        });

    }
}
