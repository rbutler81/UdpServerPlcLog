package com.cimcorp.plc.logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
	
	static String path = new File("").getAbsolutePath().concat("\\");
	static String logFileName = "PLC.log";
	static String topLine = "Date/Time,Id,Level,Colour,Header,Description";
	static String ETX = ",";
	static String STX = ",";
	static File configFile = new File(path.concat("config.ini"));
	
	
	public static void main(String[] args) {
		
		Config config = null;
		
		if (configFile.exists()) {
			
			try {
				config = Config.fromFile(configFile.getAbsolutePath());
			} catch (ConfigException e) {
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		else {
			System.out.println ("Error: Config File not Found");
			System.exit(0);
		}
		
		Log log = null;
		
		try {
			log = new Log(config, path, logFileName, topLine);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ReentrantLock lock = new ReentrantLock();
		List<String> list = new ArrayList<String>();
		
		Thread udp = new Thread(new UDPServer(config, lock, list), "UDP Server");
		udp.start();
		
		while (true) {
			
			String t = "";
			
			if (!lock.isLocked()) {
				
				if (!list.isEmpty()) {
					
					while (lock.isLocked()) {}
					lock.lock();
					t = list.get(0);
					list.remove(0);
					lock.unlock();
					
					int s = t.indexOf(STX);
					int x = t.indexOf(ETX);
					
					if ((s == 0) && (x > 10)) {
						t = t.substring(2,x);
					}
					else {
						t = t.concat(" RX_ERROR: MAL-FORMED");
					}
					
					try {
						log.appendLine(t);
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println(t);
					
				}
				
			}
			
		}
		
		
		
	}

}
