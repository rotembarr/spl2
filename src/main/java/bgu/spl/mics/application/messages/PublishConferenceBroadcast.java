package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.Vector;

public class PublishConferenceBroadcast implements Broadcast {
    //fields
    private Vector<Model> modelsToPublish;

    public PublishConferenceBroadcast(Vector<Model> modelsToPublish){
        this.modelsToPublish = modelsToPublish;
    }

    public Vector<Model> getModels() {
        return this.modelsToPublish;
    }
}