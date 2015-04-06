package Workers;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import Messages.*;


public class Worker extends Thread {

	
	private Message msg;
	
	private boolean proceed = true;

	private DatagramPacket packet;
	
	
	public Worker(DatagramPacket packet) throws UnknownHostException{
		
		this.packet = packet;
		
		
		
	}
	
	
	private void obtainMessage() {
		
		/*
		try {
			if(packet.getPort() == Scout.sender.getLocalPort() && packet.getAddress().equals(InetAddress.getLocalHost())){
				
				
				byte[] byteMsg = new byte[packet.getLength()];
				System.arraycopy(packet.getData(),packet.getOffset(),byteMsg, 0, packet.getLength());
				
				Message msg = MessageFactory.processMessage(byteMsg);
				
				System.out.println("Dropping "+msg.toString());
				return;
				
			}
		} catch (UnknownHostException e) {
			return;
		}
		
		*/
		
		byte[] byteMsg = new byte[packet.getLength()];
		System.arraycopy(packet.getData(),packet.getOffset(),byteMsg, 0, packet.getLength());
		this.msg = MessageFactory.processMessage(byteMsg);
		
	}
	
	
	public Worker(Message msg) {
		this.msg = msg;
	}
	
	@Override
	public void run() {
		
		
		obtainMessage();		
		if(msg== null)return;
		
		
		//work
		
		
		
		
		Random rand = new Random();
		
		registerAsObserver();

		try {
			Thread.sleep(rand.nextInt(400));
			
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

	public void update(DeleteMsg msg) {
		if (this.msg.ofInterest(msg)) {
			proceed = false;
			unRegisterAsObserver();
		};
		
	}

}
