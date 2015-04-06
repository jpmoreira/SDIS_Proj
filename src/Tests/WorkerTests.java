package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.Test;

import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.FileToRestore;
import Files.S_File;
import Main.Database;
import Main.Gui;
import Messages.Message;
import Messages.MessageFactory;
import Messages.PutChunkMsg;
import Messages.StoredMsg;
import Workers.BackupOrder;
import Workers.DeleteOrder;
import Workers.RestoreOrder;
import Workers.Scout;
import Workers.Worker;

public class WorkerTests {

	@Test
	public void backupSubprotocol_recieverSide_successfull() throws Exception {
		
		
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
		ProtocolTests.changeToDB1();
		Database d1 = new Database(true);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",1);
		
		assertEquals(d1.backedFilePaths().length,1);
		
		assertEquals(d1.nrChunksStored(),0);
		
		
		SendChunk[] chunksToSend = b.getChunks();
		
		assertEquals(d1.nrChunksStored(),chunksToSend.length);
		
		PutChunkMsg msg = new PutChunkMsg(chunksToSend[0], "1.0");
		byte[] bytes = msg.toBytes();
		
		
		ProtocolTests.changeToDB2();
		Database db2 = new Database(true);
		
		
		
		MulticastSocket listener = this.initializeMCReadingSocket();
		byte[] rbuf = new byte[Scout.BUFFERSIZE];
		DatagramPacket packet = new DatagramPacket(rbuf, Scout.BUFFERSIZE);
		
		Thread l = new Thread(new Runnable() {
			@Override
			public void run() {				
				try {
					listener.receive(packet);
				} catch (IOException e) {}
			}
		});
		
		l.start();
		
		
		
		
		
		Thread d = new Worker(MessageFactory.processMessage(bytes));
		d.start();
		
		d.join();
		l.join();
		
		assertEquals(db2.nrChunksStored(),1);
		
		RecieveChunk c = new RecieveChunk(chunksToSend[0].fileID, chunksToSend[0].nr);

		Thread ownStoreReciever = new Worker(MessageFactory.processMessage(new StoredMsg("1.0",chunksToSend[0].fileID , "0").toBytes()));
		
		ownStoreReciever.start();
		
		ownStoreReciever.join();
		
		assertEquals(c.getReplicaCount(),1);
		
		ProtocolTests.changeToDB1();
		
		assertEquals(chunksToSend[0].getReplicaCount(),0);
		
		Thread d2 = new Thread(new Worker(MessageFactory.processMessage(packet.getData())));
		
		d2.start();
		
		d2.join();
		
		assertEquals(chunksToSend[0].getReplicaCount(),1);
		
		
		
		
		
	}
	
	@Test
	public void backupSubprotocol_senderSide_checkWaitTillReplicationMet() throws Exception{
		
		
		ProtocolTests.changeToDB1();
		new Database(true);
		
		
		Thread putChunkSendThread = new BackupOrder("testFiles/RIGP.pdf", 2);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf");
		
		MulticastSocket mdb_recieverSocket = initializeMDBReadingSocket();
		byte[] rbuf = new byte[Scout.BUFFERSIZE];
		DatagramPacket packet = new DatagramPacket(rbuf, Scout.BUFFERSIZE);
		
		
		
		Thread mdb_reader = new Thread(new Runnable() {
			
			@Override
			public void run() {	
				try {
					mdb_recieverSocket.receive(packet);
				} catch (IOException e) {}
				
			}
		});
		
		
		mdb_reader.start();
		putChunkSendThread.start();
		
		mdb_reader.join();
		
		Thread.sleep(1000);
		
		
		try{
			
			assertTrue(putChunkSendThread.isAlive());
			
		}
		catch(Exception e){}
		
		
		for (SendChunk s : b.getChunks()) {
			
			Thread w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			
			
		}
		
		Thread.sleep(1000);
		
		try{
			
			assertTrue(putChunkSendThread.isAlive());
			
		}
		catch(Exception e){}
		
		
		
		for (SendChunk s : b.getChunks()) {
			
			Thread w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			
			
		}
		
		Thread.sleep(2000);
		
		try{
			
			assertNotEquals(putChunkSendThread.getState(),Thread.State.TERMINATED);
			
		}
		catch(Exception e){}
		
		
		
		
		
		
		
		
		putChunkSendThread.join();
		
	}
	
	@Test
	public void backupSubprotocol_senderSide_checkOnlyNonFullfiledChunksSent() throws Exception{
		
		
		ProtocolTests.changeToDB1();
		new Database(true);
		
		
		Thread putChunkSendThread = new BackupOrder("testFiles/RIGP.pdf", 2);
		
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf");
		
		
		SendChunk[] chunks = b.getChunks();
		
		MulticastSocket mdb_recieverSocket = initializeMDBReadingSocket();
		byte[] rbuf = new byte[Scout.BUFFERSIZE];
		DatagramPacket packet = new DatagramPacket(rbuf, Scout.BUFFERSIZE);
		
		
		
		Thread mdb_reader = new Thread(new Runnable() {
			
			@Override
			public void run() {	
				try {
					mdb_recieverSocket.receive(packet);
				} catch (IOException e) {}
				
			}
		});
		
		
		
		putChunkSendThread.start();
		
		
		
	
		for(int i = 0 ; i < chunks.length -1; i++){//dont do that for the last one
			
			SendChunk s = chunks[i];
			
			Thread w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			
		}
		
		System.out.println("blasdasdasdasdasdas");
		
		mdb_reader.start();
		mdb_reader.join();
		
		putChunkSendThread.join();
		
		
		ProtocolTests.changeToDB2();
		
		Message msg = MessageFactory.processMessage(packet.getData());
		
		assertTrue(msg instanceof PutChunkMsg);
		
		PutChunkMsg putChunkMsg = (PutChunkMsg) msg;
		
		if(putChunkMsg.chunk == null) System.out.println("NULLL");
		int x = putChunkMsg.chunk.nr;
		int y = chunks[chunks.length-1].nr;
		
		assertEquals(x,y);//assert that the put chunk message that was sent was from the chunk that was missing indeed
		
		//Check replication rates on both sides
		
		ProtocolTests.changeToDB1();
		
		for (SendChunk s : chunks) {
			
			if(putChunkMsg.chunk.nr == s.nr) assertEquals(s.getReplicaCount(),0);
			else assertEquals(s.getReplicaCount(),2);
		}
		

		
	}
	
	//TODO assert that nothing is done if no space available
	@Test
	public void deleteSubprotocol_senderSide() throws Exception{
		
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		S_File.availableSpace = 2560000;
		
		ProtocolTests.changeToDB1();
		Database db_backed = new Database(true);
		
		
		//SIMULATE backup done previously on sender
		FileToBackup b = new FileToBackup("testFiles/RIGP.pdf",1);
		
		SendChunk[] chunks = b.getChunks();
		
		for (SendChunk sendChunk : chunks) {
			
			sendChunk.incrementReplicationCount();
			
			assertEquals(sendChunk.getReplicaCount(),1);
		}
		
		
		assertEquals(db_backed.nrChunksStored(),chunks.length);
		
		ProtocolTests.changeToDB2();
		Database db_backer = new Database(true);
		
		//simulate reception of backup
		for (SendChunk sendChunk : chunks) {
			
			RecieveChunk rc = new RecieveChunk(sendChunk.fileID, sendChunk.nr, sendChunk.getContent(),1);
			assertEquals(rc.getReplicaCount(),1);
		}
		
		assertEquals(db_backer.nrChunksStored(),chunks.length);
			
		
		ProtocolTests.changeToDB1();
		
		Thread deleteWorker = new DeleteOrder("testFiles/RIGP.pdf");
		
		MulticastSocket mcSocket = this.initializeMCReadingSocket();
		byte[] rbuf = new byte[Scout.BUFFERSIZE];
		DatagramPacket packet = new DatagramPacket(rbuf, Scout.BUFFERSIZE);
		
		Thread mcReader = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					mcSocket.receive(packet);
				} catch (IOException e) {}
				
			}
		});
		
		mcReader.start();
		
		
		
		deleteWorker.start();
		
		mcReader.join();
		deleteWorker.join();
		
		assertEquals(db_backed.nrChunksStored(),0);//all have been deleted
		assertEquals(db_backed.backedFilePaths().length,0);
		
		ProtocolTests.changeToDB2();
		assertEquals(db_backer.nrChunksStored(),chunks.length);//all haven't been deleted yet
		
		Thread deleter = new Worker(MessageFactory.processMessage(packet.getData()));
		
		deleter.start();
		
		deleter.join();
		
		
		assertEquals(db_backer.nrChunksStored(),0);//all have been deleted
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
	@Test
	public void restoreSubprotocol_senderSide() throws Exception {
		
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
		ProtocolTests.changeToDB1();
		Database d1 = new Database(true);
		
		FileToBackup b1 = new FileToBackup("testFiles/oneChunkFile",2);
		
		FileToBackup b2 = new FileToBackup("testFiles/RIGP.pdf",1);
		
		SendChunk[] b1_chunks = b1.getChunks();
		SendChunk[] b2_chunks = b2.getChunks();
		
		
		//simulate backup being done
		for (SendChunk c : b1_chunks) {
			
			c.incrementReplicationCount();
			c.incrementReplicationCount();
		}
		
		
		for (SendChunk c : b2_chunks) {
			
			c.incrementReplicationCount();
		}
		
		
		assertEquals(d1.nrChunksStored(),b1_chunks.length+b2_chunks.length);
		
		
		Thread restoreThread_b1 = new RestoreOrder("testFiles/oneChunkFile");
		Thread restoreThread_b2 = new RestoreOrder("testFiles/RIGP.pdf");
		
		
		Scout mdr = Scout.getMDRScout();
		
		mdr.start();
		restoreThread_b1.start();
		restoreThread_b2.start();
		
		Thread.sleep(2000);
		assertNotEquals(mdr.getState(),Thread.State.TERMINATED);
		assertNotEquals(restoreThread_b1.getState(),Thread.State.TERMINATED);
		assertNotEquals(restoreThread_b2.getState(),Thread.State.TERMINATED);
		
		
		new RecieveChunk(b1_chunks[0].fileID, b1_chunks[0].nr, b1_chunks[0].getContent());
		
		restoreThread_b1.join();
		
		assertEquals(restoreThread_b1.getState(),Thread.State.TERMINATED);
		assertNotEquals(restoreThread_b2.getState(),Thread.State.TERMINATED);
		assertNotEquals(mdr.getState(),Thread.State.TERMINATED);
		
		
		for (SendChunk sendChunk : b2_chunks) {
			
			new RecieveChunk(sendChunk.fileID,sendChunk.nr,sendChunk.getContent());
		}
		
		restoreThread_b2.join();
		
		
		assertEquals(restoreThread_b2.getState(),Thread.State.TERMINATED);
		assertEquals(Scout.getMDRScout().getState(),Thread.State.NEW);
		

		
	}
	
	public MulticastSocket initializeMCReadingSocket(){
		
		
		MulticastSocket socket = null;
		try {
			InetAddress address = InetAddress.getByName(Message.MC_ADDRESS);
			socket = new MulticastSocket(Message.MC_PORT);
			socket.joinGroup(address);
		} catch (Exception e) {
			
		}
		
		return socket;

		
	}
	
	public MulticastSocket initializeMDBReadingSocket(){
		
		
		MulticastSocket socket = null;
		try {
			InetAddress address = InetAddress.getByName(Message.MDB_ADDRESS);
			socket = new MulticastSocket(Message.MDB_PORT);
			socket.joinGroup(address);
		} catch (Exception e) {
			
		}
		
		return socket;

		
	}

	
}
