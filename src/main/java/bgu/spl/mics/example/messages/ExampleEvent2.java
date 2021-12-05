package bgu.spl.mics.example.messages;

import bgu.spl.mics.Event;

public class ExampleEvent2 implements Event<String>{

    private String senderName;

    public ExampleEvent2(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }
}