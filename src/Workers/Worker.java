package Workers;

import Messages.*;


public class Worker implements Runnable {

	
	private byte[] msg;

	public Worker(byte[] msg) {
		this.msg = msg;
	}
	
	@Override
	public void run() {
		
		Message returnMsg = MessageFactory.processMessage(msg).process();
		
		if (returnMsg == null) return;
		
		// TODO give to MARTA
		
		
	}

}
