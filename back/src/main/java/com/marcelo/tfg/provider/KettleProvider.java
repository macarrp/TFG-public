package com.marcelo.tfg.provider;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;
import com.marcelo.tfg.utils.enums.LogLevelKettle;

/**
 * Proporciona métodos para ejecutar transformaciones con Kettle
 * desde una API REST hasta la ejecucion de una aplicacion Java
 * 
 * @author mcarro
 *
 */

public interface KettleProvider {
	
	/* API REST */
	/**
	 * Ejecuta una transformacion con ficheros adjuntos desde una API REST. Se puede lanzar
	 * sin/con adjuntos. En el caso de que no se deseen adjuntos 
	 * <em>executeTransformation(kettleFile, null, null)</em>
	 * 
	 * @param kettleFile - El fichero a ejecutar
	 * @param adjuntosMultipart - Los ficheros adjuntos (opcional)
	 * @param logLevel - Parametro sobre el nivel de los logs de la transformacion (opcional)
	 * 
	 * @return KettleDto 
	 * @see KettleDto
	 */
	KettleDto executeTransformation(MultipartFile kettleFile, List<MultipartFile> adjuntosMultipart, LogLevelKettle logLevel);

	
	/* Provider */
	/**
	 * Ejecuta una transformacion. Se puede lanzar sin/con adjuntos. 
	 * En el caso de que no se deseen adjuntos 
	 * <em>executeTransformation(kettleFile, null, null)</em>
	 * 
	 * @param kettleFile - El fichero a ejecutar
	 * @param adjuntos - Los ficheros adjuntos (opcional)
	 * @param logLevel - Parametro sobre el nivel de los logs de la transformacion (opcional)
	 * 
	 * @return KettleDto 
	 * @see KettleDto
	 */
	KettleDto executeTransformation(File kettleFile, List<File> adjuntos, LogLevelKettle logLevel);
	
	
	/**
	 * Dado un fichero, recorre los pasos de la transformacion y cuenta el numero de veces que se ejecutan
	 * pasos potenciales
	 * 
	 * @param kettleFile - El fichero a investigar
	 * 
	 * @return Map<String, Integer> - Como clave el nombre de paso y como valor el numero de veces que se emplea
	 */
	Map<String, Integer> getNumberOfInputOutputTypes(MultipartFile kettleFile);
}
