package bgu.spl.mics.example;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;

public class ExampleMicroService extends MicroService{

    public ExampleMicroService(String name) {
        super(name);
    }

    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
    };
    
}
