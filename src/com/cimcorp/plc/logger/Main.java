package com.cimcorp.plc.logger;

import java.io.File;
import java.io.IOException;


public class Main {
	
	static String ver = "1.0";
    static String path = new File("").getAbsolutePath().concat("\\");
    static String bitmapPath = path + "BMP\\";
	static String logFileName = "PLC.log";
	static String topLine = "Date/Time,Id,Level,Colour,Header,Description";
	static String ETX = ",";
	static String STX = ",";
	static File configFile = new File(path.concat("config.ini"));
	static String bitmapHeader = ",BMP,";
	
	
	public static void main(String[] args) {

		Config config = null;
		BitmapHandler bh = new BitmapHandler(bitmapPath);
		
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

		Message msg = new Message();

		Thread udp = new Thread(new UDPServer(config, msg), "UDP Server");
		udp.start();


        System.out.println ("PLC Logger v" + ver);
		
		while (true) {
			
			synchronized (msg) {
				try {
					msg.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			while (!msg.isEmpty()) {

				String t = msg.getNextMsg();
	
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

				if (t.contains(bitmapHeader)) {

					bh.parseLine(t);
				}

				System.out.println(t);
				
			}
					

		}
		
		
		
	}

}
