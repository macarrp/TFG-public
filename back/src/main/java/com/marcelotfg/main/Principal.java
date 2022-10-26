package com.marcelotfg.main;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Principal {

	public static void main(String[] args) {
		try {
			String result = execute("jobSencilloUsrPwd_1.ktr");
//			log.info("Resultado transf\n" + result);
//			executeJob2("jobSencilloUsrPwd_1.ktr", null, null);
		} catch (KettleXMLException e) {
			log.error("Error", e);
		} catch (KettleException e) {
			log.error("Error", e);
		}
	}
	
	public static String execute(String kettleJob) throws KettleXMLException, KettleException {
		if(!KettleEnvironment.isInitialized())
	        KettleEnvironment.init();
		
		TransMeta metaData = new TransMeta(kettleJob);
		Trans trans = new Trans(metaData); 
		String logChannelId = trans.getLogChannelId();
		trans.setLogLevel(LogLevel.BASIC);
		trans.execute(null); //
		trans.waitUntilFinished();
		
		int erroresJob = trans.getErrors();
		
		List<KettleLoggingEvent> lkl = KettleLogStore.getLogBufferFromTo(logChannelId, true, 0, KettleLogStore.getLastBufferLineNr());

		String templog = kettleLogToString(lkl);
	    KettleLogStore.discardLines(logChannelId, true);
	    
	    log.info("PDIExecutor : fine job, errores: ", erroresJob);
	    KettleEnvironment.shutdown();
	    
	    return templog;
	}
	
	public static void executeJob2(String filename, Map<String,String> parameters, String[] arguments) 
	        throws KettleXMLException, KettleException{

	    if(!KettleEnvironment.isInitialized())
	        KettleEnvironment.init();

	    JobMeta jobMeta = new JobMeta(filename, null);

	    if(parameters != null){
	        for(String key : parameters.keySet()){
	            try {
	                jobMeta.setParameterValue(key, parameters.get(key));
	            } catch (UnknownParamException ex) {
	                log.error("Error asignando parametros del job de kettle", ex);
	            }
	        }
	    }
	    Job job = new Job(null, jobMeta);

	    if(arguments != null && arguments.length> 0 )
	        job.setArguments(arguments);

	    String logChannelId = job.getLogChannelId();
	    job.start();
	    job.waitUntilFinished();
	    int errori = job.getErrors();
	    List<KettleLoggingEvent> lkl = KettleLogStore.getLogBufferFromTo(logChannelId, true, 0, KettleLogStore.getLastBufferLineNr());
//	    String templog = kettleLogToString(lkl);
	    KettleLogStore.discardLines(logChannelId, true);

	    log.info("PDIExecutor : fine job, {0} errori",errori);
	    KettleEnvironment.shutdown();

	}
	
	public static String kettleLogToString(List<KettleLoggingEvent> lkl) {
		String log = "";
		for(KettleLoggingEvent k : lkl) {
			log += k.getMessage() + " \n";
		}
		
		return log;
	}

}

