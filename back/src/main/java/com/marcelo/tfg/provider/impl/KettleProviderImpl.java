package com.marcelo.tfg.provider.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.StepPluginType;
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
import com.marcelo.tfg.utils.enums.LogLevelKettle;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KettleProviderImpl implements KettleProvider {
	
	@Override
	public KettleDto executeTransformation(MultipartFile kettleFile, LogLevelKettle logLevelKettle) {
		Boolean isKettleFile = KettleUtils.checkExtensionKettle(kettleFile);

		KettleDto ktr = new KettleDto();
		if (!isKettleFile) {
			ktr.setErrores(1);
			ktr.setMensaje("El fichero no tiene la extensión apropiada");
			return ktr;
		}

		File tempKettle = FileUtils.convertMultipartFileToTmpFile(kettleFile, "ktr");
		if (tempKettle == null) {
			ktr.setErrores(1);
			ktr.setMensaje("Error al convertir el fichero");
			return ktr;
		}
		tempKettle = KettleUtils.modifyXmlTransformationPath(tempKettle, null);

		String logChannelId = null;
		try {
			KettleInit();

			Trans trans = new Trans(new TransMeta(tempKettle.getPath()));
			logChannelId = trans.getLogChannelId();

			setLogLevel(logLevelKettle, trans);

			executeTransformation(trans, logChannelId, ktr);

			eliminaFicheroKtr(tempKettle);
			KettleEnvironment.shutdown();
		} catch (KettleException e) {
			if (logChannelId != null)
				ktr.setLog(KettleUtils.getKettleLogs(logChannelId));

			ktr.setErrores(ktr.getErrores() + 1);
			ktr.setMensaje("Error al ejecutar la transformación de Kettle");
			log.error(ktr.getMensaje(), e);
		} finally {
//			eliminaFicheroKtr(tempKettle);
		}

		return ktr;
	}

	@Override
	public KettleDto executeTransformationWithAttachments(MultipartFile kettleFile, List<MultipartFile> files,
			LogLevelKettle logLevel) {
		Boolean isKettleFile = KettleUtils.checkExtensionKettle(kettleFile);

		KettleDto ktr = new KettleDto();
		if (!isKettleFile) {
			ktr.setErrores(1);
			ktr.setMensaje("El fichero no tiene la extensión apropiada");
			return ktr;
		}

		File tempKettle = FileUtils.convertMultipartFileToTmpFile(kettleFile, "ktr");
		if (tempKettle == null) {
			ktr.setErrores(1);
			ktr.setMensaje("Error al convertir el fichero");
			return ktr;
		}

		List<File> tempKettleFiles = new ArrayList<>();
		for (MultipartFile file : files) {
			String nombreCompletoArchivo = file.getOriginalFilename();
			String extension = nombreCompletoArchivo.split("\\.")[1];
			tempKettleFiles.add(FileUtils.convertMultipartFileToTmpFile(file, extension));
		}

		tempKettle = KettleUtils.modifyXmlTransformationPath(tempKettle, tempKettleFiles);
		log.info("tam " + tempKettle.length() + " bytes");

		String logChannelId = null;
		try {
			KettleInit();

			Trans trans = new Trans(new TransMeta(tempKettle.getPath()));
			logChannelId = trans.getLogChannelId();

			setLogLevel(logLevel, trans);

			executeTransformation(trans, logChannelId, ktr);

			eliminaFicheroKtr(tempKettle);
			eliminaAdjuntosKtr(tempKettleFiles);
			KettleEnvironment.shutdown();
		} catch (KettleException e) {
			if (logChannelId != null)
				ktr.setLog(KettleUtils.getKettleLogs(logChannelId));

			ktr.setErrores(ktr.getErrores() + 1);
			ktr.setMensaje("Error al ejecutar la transformación de Kettle");
			log.error(ktr.getMensaje(), e);
		} finally {
//			eliminaFicheroKtr(tempKettle);
//			eliminaAdjuntosKtr(tempKettleFiles);
		}

		return ktr;
	}

	public KettleDto executeTransformation(File kettleFile, LogLevelKettle logLevelKettle) {
		return null;
	}

	public KettleDto executeKettleTransformationWithAttachments(File kettleFile, List<File> files,
			LogLevelKettle logLevel) {
		return null;

	}

	@Override
	public KettleDto executeJob(MultipartFile kettleFile) {
		File temp = FileUtils.convertMultipartFileToTmpFile(kettleFile, "kjb");

		KettleDto kjb = new KettleDto();

		if (temp == null) {
			kjb.setErrores(-1);
			kjb.setMensaje("Error al convertir el fichero");
			return kjb;
		}

//		public static void executeJob(String filename, Map<String,String> parameters, String[] arguments) 
//		        throws KettleXMLException, KettleException{

		try {
			KettleInit();

			JobMeta jobMeta = new JobMeta(temp.getPath(), null);

//			log.info("Recogiendo parametros del job...");
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

//			log.info("Asignando parametros...");
//		    if(arguments != null && arguments.length> 0 )
//		        job.setArguments(arguments);

			String logChannelId = job.getLogChannelId();

			job.start();
			job.waitUntilFinished();

			kjb.setErrores(job.getErrors());

			log.info("Trabajo ejecutado, recogiendo logs...");
			kjb.setLog(KettleUtils.getKettleLogs(logChannelId));

			if (kjb.getErrores() == 0)
				kjb.setMensaje("Trabajo realizada con éxito");
			else
				kjb.setMensaje("Han habido errores durante el trabajo");

			log.info("Logs recogidos, finalizando procesos...");
			KettleEnvironment.shutdown();
		} catch (KettleException e) {
			kjb.setErrores(kjb.getErrores() + 1);
			kjb.setMensaje("Error al ejecutar el trabajo de Kettle");
			log.error(kjb.getMensaje(), e);
		} finally {
			boolean eliminado = FileUtils.deleteFileIfExists(temp);
			log.info("Fichero eliminado: " + eliminado);
		}

		return kjb;
	}

	/*-------------------------------------------------------------------------*/
	// TODO: KettleBaseProvider¿?

	/**
	 * Wrapper para inicializar el entorno de Kettle
	 * 
	 * @throws KettleException
	 */
	private void KettleInit() throws KettleException {
		addPlugins();
		
		// WARNING: Tiene que ser false! Si no, el despliegue falla
		if (!KettleEnvironment.isInitialized()) {
			log.info("Inicializando KettleEnvironment");
			KettleEnvironment.init(false);
		}
		log.info("KettleEnvironment inicializado : " + KettleEnvironment.isInitialized());
	}

	private void addPlugins() { // TODO: Hacerlo relativo y dentro del modulo
		// JSON input/output
		StepPluginType.getInstance().getPluginFolders().add(new PluginFolder(
				"C:/Users\\mcarro\\Downloads\\TFG_assets\\data-integration\\plugins\\kettle-json-plugin", false, true));

		// Get data from XML
		StepPluginType.getInstance().getPluginFolders().add(new PluginFolder(
				"C:/Users\\mcarro\\Downloads\\TFG_assets\\data-integration\\plugins\\pdi-xml-plugin", false, true));
	}

	/**
	 * Setea el nivel de logs de Kettle
	 * 
	 * @param logLevelKettle
	 * @param trans
	 */
	private void setLogLevel(LogLevelKettle logLevelKettle, Trans trans) {
		if (logLevelKettle != null) {
			log.info("LogLevel detectado: " + logLevelKettle);
			trans.setLogLevel(logLevelKettle.getLogLevelKettle());
		} else {
			log.info("LogLevel no detectado, asignando a BASIC");
			trans.setLogLevel(LogLevel.BASIC);
		}
	}

	/**
	 * Setea el mensaje de Kettle segun el numero de errores
	 * 
	 * @param ktr
	 */
	private void setKettleErrorMsg(KettleDto ktr) {
		if (ktr.getErrores() == 0)
			ktr.setMensaje("Transformación realizada con éxito");
		else
			ktr.setMensaje("Han habido errores durante la transformación");
	}

	/**
	 * Ejecuta la transformacion de Kettle
	 * 
	 * @param trans
	 * @param logChannelId
	 * @param ktr
	 * @throws KettleException
	 */
	private /*TODO: synchronized*/ void executeTransformation(Trans trans, String logChannelId, KettleDto ktr) throws KettleException {
		log.info("Ejecutando transformacion...");
		trans.execute(null); // trans.execute(new String[]{});
		trans.waitUntilFinished();

		log.info("Transformacion ejecutada, recogiendo logs...");
		ktr.setLog(KettleUtils.getKettleLogs(logChannelId));
		ktr.setErrores(trans.getErrors());

		setKettleErrorMsg(ktr);
	}

	/**
	 * Elimina el fichero del sistema
	 * 
	 * @param ktr
	 * @return true si el fichero se ha eliminado, false en caso contrario
	 */
	private boolean eliminaFicheroKtr(File ktr) {
		boolean eliminado = FileUtils.deleteFileIfExists(ktr);
		log.info("Fichero eliminado: " + eliminado);
		return eliminado;
	}

	/**
	 * Elimina los ficheros del sistema
	 * 
	 * @param adjuntos
	 * @return true si todos los ficheros se han eliminado, false en caso contrario
	 */
	private boolean eliminaAdjuntosKtr(List<File> adjuntos) {
		boolean adjuntosEliminados = FileUtils.deleteMultipleFilesIfExists(adjuntos);
		log.info(adjuntos.size() + " ficheros adjuntos eliminados: " + adjuntosEliminados);
		return adjuntosEliminados;
	}
}
