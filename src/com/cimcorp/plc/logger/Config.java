package com.cimcorp.plc.logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Config  {

	private int port;
	private int maxLogSizeBytes;
	private int oldLogsToKeep;
	
	
	public Config(int port, int maxLogSizeBytes, int oldLogsToKeep) throws ConfigException {
		
		try {
			this.setPort(port);
			this.setMaxLogSizeBytes(maxLogSizeBytes);
			this.setOldLogsToKeep(oldLogsToKeep);
		}
		catch (ConfigException e) {
			throw e;
		}
		
	}
	
	public int getPort() {
		return port;
	}

	public Config setPort(int port) throws ConfigException {
		
		if (port > 0 && port <= 65535) {
			this.port = port;
			return this;
		}
		else {
			throw new ConfigException("Port must be between 0 - 65535");
		}
		
	}
	public int getMaxLogSizeBytes() {
		return maxLogSizeBytes;
	}

	public Config setMaxLogSizeBytes(int maxLogSizeBytes) throws ConfigException {
		
		if (maxLogSizeBytes > 0) {
			this.maxLogSizeBytes = maxLogSizeBytes;
			return this;
		}
		else {
			throw new ConfigException("maxLogSizeBytes must be greater than 0");
		}
	}
	public int getOldLogsToKeep() {
		return oldLogsToKeep;
	}

	public Config setOldLogsToKeep(int oldLogsToKeep) throws ConfigException {
		
		if (oldLogsToKeep >= 1) {
			this.oldLogsToKeep = oldLogsToKeep;
			return this;
		}
		else {
			throw new ConfigException("oldLogsToKeep must be greater to, or equal to 1");
		}
	}
	
	static Config fromFile(String str) throws ConfigException{
		
		int port = 0;
		int maxBytes = 0;
		int maxOld = 0;		
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(str));
			String line = br.readLine();
			List<String> p = new ArrayList<>();
			p.add(line);
			
			
		
			while (line != null) {
				line = br.readLine();
				p.add(line);
			}
			
			for (String s : p) {
				
				if (s != null) {
				
					if (s.startsWith("#port=")) {
							port = Integer.parseInt(s.substring(6));
						}
						else if (s.startsWith("#maxLogSizeBytes=")) {
							maxBytes = Integer.parseInt(s.substring(17));
						}
						else if (s.startsWith("#oldLogsToKeep=")) {
							maxOld = Integer.parseInt(s.substring(15));
						}
					
					}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new Config(port, maxBytes, maxOld);
		
	}
	
}
