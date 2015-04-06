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
import Files.S_File;
import Main.Database;
import Messages.Message;
import Messages.MessageFactory;
import Messages.PutChunkMsg;
import Workers.BackupOrder;
import Workers.DeleteOrder;
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
			
			assertFalse(putChunkSendThread.isAlive());
			
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
		
	
		
		
		
		for (SendChunk s : b.getChunks()) {
			
			Thread w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			w = new Worker(MessageFactory.processMessage(("STORED"  + " " + "1.0" + " " + s.fileID + " " + s.nr + " ").getBytes()));
			w.start();
			w.join();
			
			
		}

		//TODO continue here
		
		putChunkSendThread.join();
		
	}
	
	//TODO backup subproto. check that only sends chunks that are still missing
	//TODO assert that nothing is done if no space available
	@Test
	public void deleteSubprotocol_senderSide() throws Exception{
		
		S_File.cleanFolder(new File("backups/"));
		S_File.cleanFolder(new File("backups_2/"));
		
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
