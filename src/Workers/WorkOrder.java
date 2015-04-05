package Workers;

import java.util.TimerTask;

public abstract class WorkOrder extends Thread{

	@Override
	public abstract void run();

}
