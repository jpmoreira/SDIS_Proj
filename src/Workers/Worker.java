package Workers;

import java.util.Random;

import Messages.*;


public class Worker extends Thread {

	
	private Message msg;

	public Worker(Message msg) {
		this.msg = msg;
	}
	
	@Override
	public void run() {
		
		Random rand = new Random();
		
		registerAsObserver();

		try {
			this.sleep(rand.nextInt(400));
		} catch (InterruptedException e) {
			
		}
		
		Message returnMsg = msg.process();
		
		if (returnMsg == null) return;
		
		returnMsg.send();
		
	}

	
	private void registerAsObserver() {

		if (msg instanceof GetChunkMsg) {
			Scout.getMDRScout().attachObserverToChunkMsg(this);
		} else if (msg instanceof RemovedMsg) {
			Scout.getMDBScout().attachObserverToPutChunkMsg(this);
		}
	}

	public void update(PutChunkMsg msg) {
		// TODO Auto-generated method stub

		
	}
	
	public void update(ChunkMsg msg) {
		// TODO Auto-generated method stub
		
	}

}
