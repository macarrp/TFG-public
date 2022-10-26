package com.marcelo.tfg.utils;

import java.util.List;

import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KettleUtils {

	/**
	 * Recoge los logs de Kettle incluyendo un salto de linea y 
	 * los almacena en una String
	 * 
	 * @param logChannelId
	 * @return String
	 */
	public static String getKettleLogs(String logChannelId) {
		List<KettleLoggingEvent> kle = KettleLogStore.
				getLogBufferFromTo(logChannelId, true, 0, KettleLogStore.getLastBufferLineNr());
		
		String logResult = "";
		for(KettleLoggingEvent logEvent : kle) {
			logResult += logEvent.getMessage() + " \n";
		}
		
		log.info("Limpiando buffer...");
	    KettleLogStore.discardLines(logChannelId, true);
		
		return logResult;
	}
}
