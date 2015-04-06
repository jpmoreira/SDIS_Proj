package Workers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import Main.Gui;
import Messages.*;

public class Scout extends Thread{


	public static final int BUFFERSIZE = 70000;
	private InetAddress address;
	private MulticastSocket socket;


	private static Scout mdbScout = null;
	private static Scout mdrScout = null;
	private static Scout mcScout = null;


	private ArrayList<Worker> putChunkList = new ArrayList<Worker>();
	private ArrayList<Worker> storedList = new ArrayList<Worker>();
	private ArrayList<Worker> getChunkList = new ArrayList<Worker>();
	private ArrayList<Worker> chunkList = new ArrayList<Worker>();
	private ArrayList<Worker> deleteList = new ArrayList<Worker>();
	private ArrayList<Worker> removeList = new ArrayList<Worker>();



	private Scout(int port, String ip) {

		try {

			address = InetAddress.getByName(ip);
			socket = new MulticastSocket(port);
			socket.joinGroup(address);

		}
		catch (IOException e) {
			System.out.println("Adress/ Socket Error " + e.getMessage());
		} 

	}




	@Override
	public void run() {

		try {

			while (true) {

				System.out.println("a");
				byte[] rbuf = new byte[BUFFERSIZE];

				DatagramPacket packet = new DatagramPacket(rbuf, BUFFERSIZE);

				
				System.out.println("b");
				socket.receive(packet);
				
				

				Message msg = MessageFactory.processMessage(packet.getData());

				Worker w = new Worker(msg);
				w.start();

			}



		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return;
	}

	public void closeSocket() {
		
		socket.close();	
		
		//Thread.currentThread().interrupt();
	}


	public static Scout getMDBScout() {
		
		
		if (mdbScout == null ) mdbScout = new Scout(Message.MDB_PORT, Message.MDB_ADDRESS);
		
		return mdbScout;
	}


	public static Scout getMDRScout() {
		
		
		if (mdrScout == null || mdrScout.getState().equals(Thread.State.TERMINATED)) mdrScout = new Scout(Message.MDR_PORT, Message.MDR_ADDRESS);
		return mdrScout;
	}	


	public static Scout getMCScout() {
		if (mcScout == null) mcScout = new Scout(Message.MC_PORT, Message.MC_ADDRESS);
		return mcScout;
	}	


	public void attachObserverToPutChunkMsg(Worker w) {
		putChunkList.add(w);
	}


	public void detachObserverFromPutChunkMsg(Worker w) {
		putChunkList.remove(w);
	}

	public void attachObserverToStoredMsg(Worker w) {
		storedList.add(w);
	}


	public void detachObserverFromStoredMsg(Worker w) {
		storedList.remove(w);
	}


	public void attachObserverToGetChunkMsg(Worker w) {
		getChunkList.add(w);
	}


	public void detachObserverFromGetChunkMsg(Worker w) {
		getChunkList.remove(w);
	}

	public void attachObserverToChunkMsg(Worker w) {
		chunkList.add(w);
	}


	public void detachObserverFromChunkMsg(Worker w) {
		chunkList.remove(w);
	}

	public void attachObserverToDeleteMsg(Worker w) {
		deleteList.add(w);
	}


	public void detachObserverFromDeleteMsg(Worker w) {
		deleteList.remove(w);
	}

	public void attachObserverToRemoveMsg(Worker w) {
		removeList.add(w);
	}


	public void detachObserverFromRemoveMsg(Worker w) {
		removeList.remove(w);
	}


	private void notifyPutChunkList(Message msg) {
		
		for (Worker worker : putChunkList) {
			worker.update(msg);
		}

	}

	private void notifyStoredList(Message msg) {
		for (Worker worker : storedList) {
			worker.update(msg);
		}
	}

	private void notifyGetChunkList(Message msg) {
		for (Worker worker : getChunkList) {
			worker.update(msg);
		}
	}
	private void notifyChunkList(Message msg) {
		for (Worker worker : chunkList) {
			worker.update(msg);
		}
	}
	private void notifyDeleteList(Message msg) {
		for (Worker worker : deleteList) {
			worker.update(msg);
		}
	}
	private void notifyRemoveList(Message msg) {
		for (Worker worker : removeList) {
			worker.update(msg);
		}
	}
	
}
