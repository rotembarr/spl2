package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;

public class PublishConferenceBroadcast implements Broadcast {

    // local variables.
    private List<Model> modelsToPublish;

    public PublishConferenceBroadcast(List<Model> modelsToPublish){
        this.modelsToPublish = modelsToPublish;
    }

    public List<Model> getModels() {
        return this.modelsToPublish;
    }
}