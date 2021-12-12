package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu = null;
    
    public GPUService(String name, GPU.Type type) {
        super(name);
        this.gpu = new GPU(type);
    }

    @Override
    protected void initialize() {

        // Train Model Event.
        super.<Model, TrainModelEvent>subscribeEvent(TrainModelEvent.class, new Callback<TrainModelEvent>() {
            public void call(TrainModelEvent c) {
                gpu.insertNewModel(c.getModel());
            }
        });

        // Test Model Event.
        super.<Model, TestModelEvent>subscribeEvent(TestModelEvent.class, new Callback<TestModelEvent>() {
            public void call(TestModelEvent c) {
                gpu.testModel(c.getModel());
            }
        });

        // Tick Broadcast.
        super.<TickBroadcast>subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            public void call(TickBroadcast c) {
                gpu.tickSystem();
            }
        });
        

    }
}
