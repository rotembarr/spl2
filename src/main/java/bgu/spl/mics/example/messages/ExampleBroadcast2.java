package bgu.spl.mics.example.messages;

import bgu.spl.mics.Broadcast;

public class ExampleBroadcast2 implements Broadcast {

    private String senderId;

    public ExampleBroadcast2(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}
