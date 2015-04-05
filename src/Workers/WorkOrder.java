package Workers;

import java.util.TimerTask;

public abstract class WorkOrder extends TimerTask{

	@Override
	public abstract void run();

}
