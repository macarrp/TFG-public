package com.marcelo.tfg.provider.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;
import com.marcelo.tfg.provider.KettleBaseProvider;
import com.marcelo.tfg.provider.KettleProvider;
import com.marcelo.tfg.utils.Constantes;
import com.marcelo.tfg.utils.FileUtilsTFG;
import com.marcelo.tfg.utils.KettleUtils;
import com.marcelo.tfg.utils.enums.LogLevelKettle;

@Component
public class KettleProviderImpl extends KettleBaseProvider implements KettleProvider {

	@Override
	public KettleDto executeTransformation(MultipartFile kettleFile, List<MultipartFile> adjuntosMultipart,
			LogLevelKettle logLevel) {
		KettleDto ktr = new KettleDto();

		File tempKettleFile = FileUtilsTFG.convertMultipartFileToTmpFile(kettleFile, Constantes.Extension.KTR);

		if (!checkConversion(tempKettleFile, ktr))
			return ktr;

		if (!checkExtension(tempKettleFile, ktr))
			return ktr;

		List<File> adjuntosFile = null;
		boolean tieneAdjuntos = false;
		
		if (adjuntosMultipart != null && adjuntosMultipart.size() > 0) {
			adjuntosFile = new ArrayList<>();
			convertAndAddToFileList(adjuntosFile, adjuntosMultipart);
			tieneAdjuntos = true;
		}

		executeKettleFile(tempKettleFile, adjuntosFile, tieneAdjuntos, logLevel, ktr);

		return ktr;
	}

	@Override
	public KettleDto executeTransformation(File kettleFile, List<File> adjuntos, LogLevelKettle logLevel) {
		KettleDto ktr = new KettleDto();

		if (!checkExtension(kettleFile, ktr))
			return ktr;

		boolean tieneAdjuntos = false;

		if (adjuntos != null && adjuntos.size() > 0)
			tieneAdjuntos = true;

		executeKettleFile(kettleFile, adjuntos, tieneAdjuntos, logLevel, ktr);
		
		return ktr;
	}
	
	public void executeKettleFile(File kettleFile, List<File> adjuntos, boolean tieneAdjuntos, LogLevelKettle logLevel, KettleDto ktr) {
		try {
			kettleFile = KettleUtils.modifyXmlTransformationPath(kettleFile, tieneAdjuntos);

			execute(kettleFile, adjuntos, logLevel, ktr);
		} finally {
			eliminaFichero(kettleFile);
			eliminaAdjuntosKtr(adjuntos);
		}
	}

//	@Override
//	public KettleDto executeJob(MultipartFile kettleFile) {
//		KettleDto kjb = new KettleDto();

//		File tempKettleFile = FileUtilsTFG.convertMultipartFileToTmpFile(kettleFile, Constantes.Extension.KJB);
//
//		if(!checkConversion(tempKettleFile, kjb))
//			return kjb;
//
////		public static void executeJob(String filename, Map<String,String> parameters, String[] arguments) 
////		        throws KettleXMLException, KettleException{
//		String logChannelId = null;
//		try {
//			KettleInit();
//
//			JobMeta jobMeta = new JobMeta(tempKettleFile.getPath(), null);
//
////			log.info("Recogiendo parametros del job...");
////		    if(parameters != null){
////		        for(String key : parameters.keySet()){
////		            try {
////		                jobMeta.setParameterValue(key, parameters.get(key));
////		            } catch (UnknownParamException ex) {
////		                log.error("Error asignando parametros del job de kettle", ex);
////		            }
////		        }
////		    }
//
//			Job job = new Job(null, jobMeta);
//
////			log.info("Asignando parametros...");
////		    if(arguments != null && arguments.length> 0 )
////		        job.setArguments(arguments);
//
//			logChannelId = job.getLogChannelId();
//
//			job.start();
//			job.waitUntilFinished();
//
//			kjb.setErrores(job.getErrors());
//
//			log.info("Trabajo ejecutado, recogiendo logs...");
//			kjb.setLog(KettleUtils.getKettleLogs(logChannelId));
//
//			if (kjb.getErrores() == 0)
//				kjb.setMensaje("Trabajo realizada con éxito");
//			else
//				kjb.setMensaje("Han habido errores durante el trabajo");
//
//			log.info("Logs recogidos, finalizando procesos...");
//			KettleEnvironment.shutdown();
//		} catch (KettleException e) {
////			if (logChannelId != null)
////				kjb.setLog(KettleUtils.getKettleLogs(logChannelId));
//			
//			kjb.setErrores(kjb.getErrores() + 1);
//			kjb.setMensaje("Error al ejecutar el trabajo de Kettle");
//			log.error(kjb.getMensaje(), e);
//		} finally {
//			eliminaFichero(tempKettleFile);
//		}

//		return kjb;
//	}

}
