package com.marcelo.tfg.provider;

import java.util.List;

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

	/**
	 * Ejecutar transformacion desde una API REST
	 * 
	 * @param kettleFile el fichero ktr a ejecutar
	 * @param logLevel parametro opcional sobre el nivel de los logs de la transformacion
	 * 
	 * @return KettleDto 
	 * @see KettleDto
	 */
	KettleDto executeTransformation(MultipartFile kettleFile, LogLevelKettle logLevel);
	
	/**
	 * Ejecutar transformacion con adjuntos desde una API REST
	 * 
	 * @param kettleFile el fichero ktr a ejecutar
	 * @param logLevel parametro opcional sobre el nivel de los logs de la transformacion
	 * 
	 * @return KettleDto 
	 * @see KettleDto
	 */
	KettleDto executeTransformationWithAttachments(MultipartFile kettleFile, List<MultipartFile> files, LogLevelKettle logLevel);

	
	
	KettleDto executeJob(MultipartFile kettleFile);
}
