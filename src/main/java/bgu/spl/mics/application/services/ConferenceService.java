package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    
    // Variables.
    private ConfrenceInformation confrenceInformation = null;

    public ConferenceService(String name, int date) {
        super(name);
        this.confrenceInformation = new ConfrenceInformation(name, date); 
    }

    public ConfrenceInformation getInformation() {
        return this.confrenceInformation;
    }

    
    @Override
    protected void initialize() {
        super.initialize();
                
        super.<Model, PublishResultEvent>subscribeEvent(PublishResultEvent.class, new Callback<PublishResultEvent>() {
            public void call(PublishResultEvent p) {
                Model model = p.getModel();
                model.setStatus(Model.Status.PRE_PUBLISHED);
                confrenceInformation.addModel(model);
                complete(p, model);
            }
        });

        super.<TickBroadcast>subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            int cnt = 0;
            public void call(TickBroadcast t) {

                // Tick system.
                this.cnt++;

                // Send broadcast and finish the job.
                if (this.cnt >= confrenceInformation.getDate()) {
                    PublishConferenceBroadcast p = new PublishConferenceBroadcast(confrenceInformation.getModels());
                    sendBroadcast(p);
                    terminate();
                }
            }
        });
    }
}
