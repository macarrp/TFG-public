package com.marcelo.tfg;

import org.pentaho.di.core.logging.LogLevel;

public enum LogLevelKettle {
	NOTHING(LogLevel.NOTHING), 
	ERROR(LogLevel.ERROR), 
	MINIMAL(LogLevel.MINIMAL), 
	BASIC(LogLevel.BASIC), 
	DETAILED(LogLevel.DETAILED), 
	DEBUG(LogLevel.DEBUG), 
	ROWLEVEL(LogLevel.ROWLEVEL);
	
	private LogLevel logKettle;
	
	LogLevelKettle(LogLevel log) {
		this.logKettle = log;
	}
	
	public LogLevel getLogLevelKettle() {
		return this.logKettle;
	}
	
	public void setLogLevelKettle(LogLevel log) {
		this.logKettle = log;
	}
}