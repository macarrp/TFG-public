package com.marcelo.tfg.provider.impl;

import java.io.File;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;
import com.marcelo.tfg.provider.KettleProvider;
import com.marcelo.tfg.utils.FileUtils;
import com.marcelo.tfg.utils.KettleUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KettleProviderImpl implements KettleProvider {

	@Override
	public KettleDto executeKettleTransformation(MultipartFile kettleFile) {
		File temp = FileUtils.convertMultipartFileToTmpFile(kettleFile);
		
		KettleDto ktr = new KettleDto();
		
		if(temp == null) {
			ktr.setErrores(-1);
			ktr.setMensaje("Error al convertir el fichero");
			return ktr;
		}
		
		try {
			
			// WARNING: Tiene que ser false! Si no, el despliegue falla
			if(!KettleEnvironment.isInitialized()) {
				log.info("Inicializando KettleEnvironment");
				KettleEnvironment.init(false);
			}
			log.info("KettleEnvironment inicializado : " + KettleEnvironment.isInitialized());
			
			TransMeta metaData = new TransMeta(temp.getPath());
			Trans trans = new Trans(metaData);
			trans.setLogLevel(LogLevel.BASIC);
			
			String logChannelId = trans.getLogChannelId();
			
			log.info("Ejecutando transformacion...");
			trans.execute(null);
			trans.waitUntilFinished();
			
			ktr.setErrores(trans.getErrors());
			
			log.info("Transformacion ejecutada, recogiendo logs...");
			ktr.setLog(KettleUtils.getKettleLogs(logChannelId));
			
		    if(ktr.getErrores() == 0)
		    	ktr.setMensaje("Transformación realizada con éxito");
		    else
		    	ktr.setMensaje("Han habido errores durante la transformación");
		    
		    KettleEnvironment.shutdown();
		} catch (KettleException e) {
			ktr.setMensaje("Error al ejecutar la transformación de Kettle");
			log.error(ktr.getMensaje(), e);			
		} finally {
			boolean eliminado = FileUtils.deleteFileIfExists(temp);
			log.info("Fichero eliminado: " + eliminado);
		}
		
		return ktr;
	}

	
	@Override
	public KettleDto executeKettleJob(MultipartFile kettleFile) {
		File temp = FileUtils.convertMultipartFileToTmpFile(kettleFile);
		
		KettleDto kjb = new KettleDto();
		
		if(temp == null) {
			kjb.setErrores(-1);
			kjb.setMensaje("Error al convertir el fichero");
			return kjb;
		}
		
//		public static void executeJob(String filename, Map<String,String> parameters, String[] arguments) 
//		        throws KettleXMLException, KettleException{

		try {
			if(!KettleEnvironment.isInitialized())
		        KettleEnvironment.init();

		    JobMeta jobMeta = new JobMeta(temp.getPath(), null);

//		    if(parameters != null){
//		        for(String key : parameters.keySet()){
//		            try {
//		                jobMeta.setParameterValue(key, parameters.get(key));
//		            } catch (UnknownParamException ex) {
//		                log.error("Error asignando parametros del job de kettle", ex);
//		            }
//		        }
//		    }
		    
		    Job job = new Job(null, jobMeta);

//		    if(arguments != null && arguments.length> 0 )
//		        job.setArguments(arguments);

		    String logChannelId = job.getLogChannelId();
		    
		    job.start();
		    job.waitUntilFinished();
			
			kjb.setErrores(job.getErrors());
			
			log.info("Trabajo ejecutada, recogiendo logs...");
			kjb.setLog(KettleUtils.getKettleLogs(logChannelId));
			
		    if(kjb.getErrores() == 0)
		    	kjb.setMensaje("Trabajo realizada con éxito");
		    else
		    	kjb.setMensaje("Han habido errores durante el trabajo");
		    
		    KettleEnvironment.shutdown();
		} catch (KettleException e) {
			kjb.setMensaje("Error al ejecutar el trabajo de Kettle");
			log.error(kjb.getMensaje(), e);			
		} finally {
			boolean eliminado = FileUtils.deleteFileIfExists(temp);
			log.info("Fichero eliminado: " + eliminado);
		}
		
	    return kjb;
	}

}
