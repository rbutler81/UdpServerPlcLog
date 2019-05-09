package com.cimcorp.plc.logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class UDPServer implements Runnable{

	private Config config;
	private ReentrantLock lock;
	private List<String> list;
	
	public UDPServer(Config c, ReentrantLock l, List<String> s) {
		this.config = c;
		this.lock = l;
		this.list = s;
	}
	
	@Override
	public void run() {
		
		DatagramSocket serverSocket = null;
		
		try {
			serverSocket = new DatagramSocket(config.getPort());
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		System.out.println("Listening on port: " + config.getPort());        
        
        while (true) {
        	
        	byte[] receiveData = new byte[1024];
        	
        	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        	try {
				serverSocket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	String r = new String(receivePacket.getData());
        	
        	while (lock.isLocked()) {}
        	lock.lock();
        	list.add(r);
        	lock.unlock();
        	
        	
        }
		
	}
	
	public boolean isLocked() {
		return lock.isLocked();
	}

}
