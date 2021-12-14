package bgu.spl.mics.application.services;

import java.util.LinkedList;
import java.util.Queue;

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
    private Queue<TrainModelEvent> trainEvents = null;

    
    public GPUService(String name, GPU.Type type) {
        super(name);
        this.gpu = new GPU(type);
        this.trainEvents = new LinkedList<TrainModelEvent>();
    }

    @Override
    protected void initialize() {
        super.initialize();

        // Train Model Event.
        super.<Model, TrainModelEvent>subscribeEvent(TrainModelEvent.class, new Callback<TrainModelEvent>() {
            public void call(TrainModelEvent c) {
                gpu.insertNewModel(c.getModel());
                trainEvents.add(c);
            }
        });
        
        // Test Model Event.
        super.<Model, TestModelEvent>subscribeEvent(TestModelEvent.class, new Callback<TestModelEvent>() {
            public void call(TestModelEvent c) {
                gpu.testModel(c.getModel());
                messageBus.complete(c, c.getModel());
            }
        });

        // Tick Broadcast.
        super.<TickBroadcast>subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            public void call(TickBroadcast c) {
                gpu.tickSystem();
                if (trainEvents.size() > 0 && trainEvents.peek().getModel().getStatus() == Model.Status.TRAINED) {
                    TrainModelEvent event = trainEvents.poll();
                    messageBus.complete(event, event.getModel());
                }
            }
        });
        

    }
}
