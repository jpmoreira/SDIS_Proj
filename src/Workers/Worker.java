package Workers;

import Messages.*;


public class Worker extends Thread {

	
	private Message msg;

	public Worker(Message msg) {
		this.msg = msg;
	}
	
	@Override
	public void run() {
		
		Message returnMsg = msg.process();
		
		if (returnMsg == null) return;
		
		returnMsg.send();
		
	}

	
	public void update(Message msg) {
		// TODO Auto-generated method stub
		
	}

}
