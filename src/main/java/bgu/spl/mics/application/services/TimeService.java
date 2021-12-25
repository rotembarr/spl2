package bgu.spl.mics.application.services;

import java.util.Timer;
import java.util.TimerTask;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	int clockFreq;
	int duration;
	
	
	public TimeService(String name, int clockFreq, int duration) {
		super(name);
		this.clockFreq = clockFreq;
		this.duration = duration;
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		// Create ticks.
		Timer timer = new Timer();

		timer.schedule(new TimerTask() {
			int timeCnt = 0;
			
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast());
				timeCnt++;
				
				if (timeCnt >= duration) {

					System.out.println("Time service call shut down at " + timeCnt + " clocks");
					sendBroadcast(new TerminateBroadcast());

					timer.cancel();
				}
			}
		}, 1000, clockFreq);
	}
}
