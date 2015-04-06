package Workers;

import java.util.Random;

import Messages.*;


public class Worker extends Thread {

	
	private Message msg;
	
	private boolean proceed = true;

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
		
		if (!proceed) return;
		
		Message returnMsg = msg.process();
		
		if (returnMsg == null) return;
		
		returnMsg.send();
		
		unRegisterAsObserver();
		
	}

	
	private void registerAsObserver() {

		if (msg instanceof GetChunkMsg) {
			MessageFactory.attachObserverToChunkMsg(this);
		} else if (msg instanceof RemovedMsg) {
			MessageFactory.attachObserverToPutChunkMsg(this);
		}
	}
	
	private void unRegisterAsObserver() {

		if (msg instanceof GetChunkMsg) {
			MessageFactory.detachObserverFromChunkMsg(this);
		} else if (msg instanceof RemovedMsg) {
			MessageFactory.detachObserverFromPutChunkMsg(this);
		}
	}

	
	public void update(PutChunkMsg msg) {
		
		if (this.msg.ofInterest(msg)) {
			proceed = false;
			unRegisterAsObserver();
		};
		
	}
	
	public void update(ChunkMsg msg) {
		if (this.msg.ofInterest(msg)) {
			proceed = false;
			unRegisterAsObserver();
		};
		
	}

	public void update(RemovedMsg msg) {
		if (this.msg.ofInterest(msg)) {
			proceed = false;
			unRegisterAsObserver();
		};
		
	}

	public void update(GetChunkMsg msg) {
		if (this.msg.ofInterest(msg)) {
			proceed = false;
			unRegisterAsObserver();
		};
		
	}

	public void update(StoredMsg msg) {
		if (this.msg.ofInterest(msg)) {
			proceed = false;
			unRegisterAsObserver();
		};
		
	}

}
