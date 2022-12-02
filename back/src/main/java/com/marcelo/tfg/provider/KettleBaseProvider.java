package com.marcelo.tfg.provider;

import java.io.File;
import java.util.List;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;
import com.marcelo.tfg.utils.Constantes;
import com.marcelo.tfg.utils.FileUtilsTFG;
import com.marcelo.tfg.utils.KettleUtils;
import com.marcelo.tfg.utils.enums.LogLevelKettle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KettleBaseProvider {

	/**
	 * Verifica que la conversion se haya realizado correctamente. En caso fallido,
	 * setea el error en el dto
	 * 
	 * @param kettleFile - El fichero Kettle
	 * @param kettleDto - dto
	 * @return Cierto si el fichero se ha convertido correctamente. 
	 * Falso en caso contrario
	 */
	protected boolean checkConversion(File kettleFile, KettleDto kettleDto) {
		if (kettleFile == null) {
			kettleDto.setErrores(1);
			kettleDto.setMensaje("Error al convertir el fichero");
			eliminaFichero(kettleFile);
		}
		return kettleFile != null;
	}
	
	/**
	 * Verifica la extension del archivo. En caso erroneo, setea el error en el dto.
	 * <br>Las extensions admitidas son ktr, kjb y xml
	 * 
	 * @param kettleFile - El fichero Kettle
	 * @param kettleDto - dto
	 * @return Cierto si el fichero tiene la extension apropiada
	 */
	protected boolean checkExtension(MultipartFile kettleFile, KettleDto kettleDto) {
		Boolean isKettleFile = KettleUtils.checkExtensionKettle(kettleFile);
		if (!isKettleFile) {
			kettleDto.setErrores(1);
			kettleDto.setMensaje("El fichero no tiene la extensión apropiada");
		}
		return isKettleFile;
	}
	
	/**
	 * Verifica la extension del archivo. En caso erroneo, setea el error en el dto.
	 * <br>Las extensions admitidas son ktr, kjb y xml
	 * 
	 * @param kettleFile - El fichero Kettle
	 * @param kettleDto - dto
	 * @return Cierto si el fichero tiene la extension apropiada
	 */
	protected boolean checkExtension(File kettleFile, KettleDto kettleDto) {
		Boolean isKettleFile = KettleUtils.checkExtensionKettle(kettleFile);
		if (!isKettleFile) {
			kettleDto.setErrores(1);
			kettleDto.setMensaje("El fichero no tiene la extensión apropiada");
		}
		return isKettleFile;
	}
	
	/**
	 * Añade a fileList los adjuntos pasados como parametro a la ruta temporal del sistema
	 * 
	 * @param fileList - La lista a la que se añaden
	 * @param adjuntos - Los ficheros a convertir
	 */
	protected void convertAndAddToFileList(List<File> fileList, List<MultipartFile> adjuntos) {
		for (MultipartFile adj : adjuntos) {
			fileList.add(FileUtilsTFG.convertMultipartFileToTmpFile(adj));
		}
	}
	
	
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
	
	private void addPlugins() {		
		StepPluginType.getInstance().getPluginFolders().add(new PluginFolder(
				Constantes.PLUGINS_FOLDER, false, true));
	}
	
	private void setLogLevel(LogLevelKettle logLevelKettle, Trans trans) {
		if (logLevelKettle != null) {
			log.info("LogLevel detectado: " + logLevelKettle);
			trans.setLogLevel(logLevelKettle.getLogLevelKettle());
		} else {
			log.info("LogLevel no detectado, asignando a BASIC");
			trans.setLogLevel(LogLevel.BASIC);
		}
	}
	
	private void setKettleErrorMsg(KettleDto kettleDto) {
		if (kettleDto.getErrores() == 0)
			kettleDto.setMensaje("Transformación realizada con éxito");
		else
			kettleDto.setMensaje("Han habido errores durante la transformación");
	}

	
	/**
	 * Ejecuta Kettle
	 * 
	 * @param tempKettle - El fichero Kettle
	 * @param adjuntos - Los adjuntos de la transformacion
	 * @param logLevelKettle - Nivel de logs
	 * @param ktrDto - dto
	 */
	protected void execute(File tempKettle, List<File> adjuntos, LogLevelKettle logLevelKettle, KettleDto ktrDto) {
		String logChannelId = null;
		try {
			KettleInit();

			Trans trans = new Trans(new TransMeta(tempKettle.getPath()));
			logChannelId = trans.getLogChannelId();

			setLogLevel(logLevelKettle, trans);

			executeTrans(trans, logChannelId, ktrDto);

			log.info("Ejecucion finalizada\n");
		} catch (KettleException e) {
			if (logChannelId != null)
				ktrDto.setLog(KettleUtils.getKettleLogs(logChannelId));

			ktrDto.setErrores(ktrDto.getErrores() + 1);
			ktrDto.setMensaje("Error al ejecutar la transformación de Kettle");
			log.error(ktrDto.getMensaje(), e);
		} finally {
			KettleEnvironment.shutdown();
		}
	}
	
	/**
	 * Ejecuta la transformacion de Kettle
	 * 
	 * @param trans
	 * @param logChannelId
	 * @param kettleDto
	 * @throws KettleException
	 */
	private void executeTrans(Trans trans, String logChannelId, KettleDto kettleDto) throws KettleException {
		log.info("Ejecutando transformacion...");
		trans.execute(null); // trans.execute(new String[]{});
		trans.waitUntilFinished();

		log.info("Transformacion ejecutada, recogiendo logs...");
		kettleDto.setLog(KettleUtils.getKettleLogs(logChannelId));
		log.info("Logs recogidos...");
		
		kettleDto.setErrores(trans.getErrors());
		setKettleErrorMsg(kettleDto);
	}

	
	
	/**
	 * Elimina el fichero del sistema
	 * 
	 * @param fich
	 * @return true si el fichero se ha eliminado, false en caso contrario
	 */
	protected boolean eliminaFichero(File fich) {
		boolean eliminado = FileUtilsTFG.deleteFile(fich);
		log.info("Fichero eliminado: " + eliminado);
		return eliminado;
	}

	/**
	 * Elimina los ficheros del sistema
	 * 
	 * @param adjuntos
	 * @return true si todos los ficheros se han eliminado, false en caso contrario
	 */
	protected boolean eliminaAdjuntosKtr(List<File> adjuntos) {
		int adjuntosEliminados = FileUtilsTFG.deleteMultipleFiles(adjuntos);
		int total = adjuntos != null ? adjuntos.size() : 0;
		String result = adjuntosEliminados + " ficheros adjuntos eliminados de " + total;
		log.info(result);
		return adjuntosEliminados == total;
	}
}
