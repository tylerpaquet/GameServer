package chatserver;

import game.Game;

import java.io.*;
import java.net.*;

public class GameServer extends Thread{
	
	/* 
	 * pOutBuffer 	0-3 ?
	 * 				4-173 board array
	 * 				174-179 player locations/state
	 */	
	private final int PACKETSIZE = 8;
	private final int OPORT = 3434;
	private final int HBPORT = 13337;
	private InetAddress address1,address2;
	private int inPort1,inPort2;
	private DatagramSocket player1Socket,player2Socket;
	private DatagramPacket p1InPacket,p2InPacket,p1OutPacket,p2OutPacket;
	private byte[] p1InBuf,p2InBuf,p2OutBuf,p1OutBuf;
	private boolean running = true;	
	private String p1Name,p2Name;
	
	private Game game;
	
	public GameServer(String add1,String add2,int port1,int port2,String p1,String p2){
		
		try {
			p1Name = p1;
			p2Name = p2;
			address1 = InetAddress.getByName(add1);
			address2 = InetAddress.getByName(add2);
			inPort1 = port1;
			inPort2 = port2;
			player1Socket = new DatagramSocket( inPort1 );
			player1Socket.setSoTimeout( 10 );
			player2Socket = new DatagramSocket( inPort2 );
			player2Socket.setSoTimeout( 10 );
			p1InBuf = new byte[1];
			p2InBuf = new byte[1];
			p1OutBuf = new byte[PACKETSIZE];
			p2OutBuf = p1OutBuf;
			p1InPacket = new DatagramPacket(p1InBuf,p1InBuf.length);
			p2InPacket = new DatagramPacket(p2InBuf,p2InBuf.length);
			p1OutPacket = new DatagramPacket(p1OutBuf,p1OutBuf.length,address1,OPORT);
			p2OutPacket = new DatagramPacket(p2OutBuf,p2OutBuf.length,address2,OPORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run(){


		try {
			Thread.sleep( 1000 );
			DatagramPacket startPacket1 = new DatagramPacket(p1OutBuf,p1OutBuf.length,address1,HBPORT);
			DatagramPacket startPacket2 = new DatagramPacket(p2OutBuf,p2OutBuf.length,address2,HBPORT);
			p1OutBuf[0] = 1;
			player1Socket.send( startPacket1 );
			p1OutBuf[0] = 2;
			player2Socket.send( startPacket2 );
			System.out.println("Sent Hello PAckets...");
			Thread.sleep( 1000 );
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		}
		
		game = new Game();
		
		while( running ){

			try {
				player1Socket.receive( p1InPacket );
				
				// If recieved something from p1
				game.action(1, p1InBuf[0]);

			} catch (SocketTimeoutException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				player2Socket.receive( p2InPacket );
				
				// If recieved something from p2
				game.action(2, p2InBuf[0]);
			} catch (SocketTimeoutException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}


			try {
				game.update();
				
				game.setBuffer(p1OutBuf);
				
				player1Socket.send( p1OutPacket );
				player2Socket.send( p2OutPacket );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		player1Socket.close();
		player2Socket.close();
	}
}
