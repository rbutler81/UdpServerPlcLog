package com.cimcorp.plc.logger;

public class ConfigException extends Exception {

	private static final long serialVersionUID = 5427096097823764186L;

	public ConfigException(String message){
		super(message);
	}
	
	public String getMessage() {
		return super.getMessage();
	}

}
