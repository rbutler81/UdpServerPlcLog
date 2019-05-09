package com.cimcorp.plc.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Log {

	private Config config;
	private String path;
	private String fileName;
	private String firstLine;
	private File logFile;
	private File archivePath;
	private String filePrefix;
	private String fileExtension;
	
	public Log(Config config, String path, String fileName, String firstLine) throws IOException {
		
		this.config = config;
		this.path = path;
		this.fileName = fileName;
		this.firstLine = firstLine;
		
		logFile = new File(path.concat(fileName));
		archivePath = new File(path.concat("archive\\"));
		
		filePrefix = fileName.substring(0, fileName.indexOf("."));
		fileExtension = fileName.substring(fileName.indexOf("."));
		
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
				appendLine(firstLine);
			}catch (IOException e) {
				throw e;
			}
		}
		
	}
	
	public void appendLine(String s) throws IOException {
		
		if (logFile.length() < config.getMaxLogSizeBytes()) {	
			
			try {
				writeToLog(s);
			}catch (IOException e) {
				throw e;
			}
			
		}
		
		else {
			
			if (!archivePath.exists() && config.getOldLogsToKeep() > 0) {
				
				archivePath.mkdir();
				File old = new File(archivePath.toString() + "\\" + filePrefix + "_0" + fileExtension);
				logFile.renameTo(old);
				logFile.createNewFile();
				
				try {
					writeToLog(firstLine);
					writeToLog(s);
				}catch (IOException e) {
					throw e;
				}
			}
			
			else if (archivePath.exists() && config.getOldLogsToKeep() > 0) {
				
				List<File> fl = new ArrayList<File>();
				
				for (int i = 0; i < config.getOldLogsToKeep(); i++) {
					fl.add(new File(archivePath.toString() + "\\" + filePrefix + "_" + Integer.toString(i) + fileExtension));
				}
				
				if (fl.get(config.getOldLogsToKeep() - 1).exists()) {
					fl.get(config.getOldLogsToKeep() - 1).delete();
				}
				
				for (int i = config.getOldLogsToKeep() - 1; i >= 0; i--) {
					
					if (fl.get(i).exists()) {
						fl.get(i).renameTo(new File(archivePath.toString() + "\\" + filePrefix + "_" + Integer.toString(i + 1) + fileExtension));
					}
				}
				
				logFile.renameTo(new File(archivePath.toString() + "\\" + filePrefix + "_0" + fileExtension));
				logFile.createNewFile();
				
				try {
					writeToLog(firstLine);
					writeToLog(s);
				}catch (IOException e) {
					throw e;
				}
				
			}
			
		}
		
	}
	
	private void writeToLog(String s) throws IOException {
		
		BufferedWriter br = null;
		
		try {
		    FileWriter fstream = new FileWriter(logFile.toString(), true);
		    br = new BufferedWriter(fstream);
		    br.write(s + "\n");
		}

		catch (IOException e) {
		    throw e;
		}

		finally {
		    if(br != null) {
		        br.close();
		    }
		}
		
	}
	
	
	
}
