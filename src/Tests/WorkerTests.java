package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.SQLException;

import org.eclipse.swt.internal.cocoa.Protocol;
import org.junit.Test;

import Chunk.RecieveChunk;
import Chunk.SendChunk;
import Files.FileToBackup;
import Files.S_File;
import Main.Database;
import Messages.Message;
import Messages.PutChunkMsg;
import Workers.BackupOrder;
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
		
		
		
		Thread d = new Thread(new Worker(bytes));
		d.start();
		
		d.join();
		l.join();
		
		assertEquals(db2.nrChunksStored(),1);
		
		RecieveChunk c = new RecieveChunk(chunksToSend[0].fileID, chunksToSend[0].nr);

		assertEquals(c.getReplicaCount(),1);
		
		ProtocolTests.changeToDB1();
		
		assertEquals(chunksToSend[0].getReplicaCount(),0);
		
		Thread d2 = new Thread(new Worker(packet.getData()));
		
		d2.start();
		
		d2.join();
		
		assertEquals(chunksToSend[0].getReplicaCount(),1);
		
		
		
		
		
	}
	
	@Test
	public void backupSubprotocol_senderSide_checkWaitTillReplicationMet() throws SQLException, InterruptedException{
		
		
		ProtocolTests.changeToDB1();
		new Database(true);
		
		
		Thread putChunkSendThread = new Thread(new BackupOrder("testFiles/RIGP.pdf", 2, Message.MDB_PORT, Message.MDB_ADDRESS));
		
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
		catch(Exception e){
			
			e.printStackTrace();
		}

		//Thread putChunkReceiveThread = new Thread(new Worker(packet.getData()));
		
		
		
		
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
