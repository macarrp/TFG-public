package com.marcelo.tfg.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {
	
	/**
	 * Convierte el MultipartFile a un File temporal. 
	 * Devuelve null en caso contrario.
	 * 
	 * @param fileToConvert
	 * 
	 * @return File
	 */
	public static File convertMultipartFileToTmpFile(MultipartFile fileToConvert) {
		if(fileToConvert == null)
			return null;
		
		File temp = null;
		try {
			log.info("Creando fichero temporal");

			// Parameter 3 - Default tmp directory of the system
			temp = File.createTempFile("kettle_", ".ktr", null); 
			
			log.info("Fichero temporal creado con exito, volcando bytes...");

			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(fileToConvert.getBytes());
			fos.close();

			log.info("Conversion terminada, ruta completa del fichero: " + temp.getPath());
		} catch (IOException e) {
			log.error("Error al convertir fichero " + fileToConvert.getName(), e);
		}
		
		log.info("Tam del fichero: " + temp.getTotalSpace() + " bytes");
		return temp;
	}

	/**
	 * Elimina el fichero pasado como parametro.
	 * 
	 * @param file
	 * 
	 * @return boolean
	 */
	public static boolean deleteFileIfExists(File file) {
		if(file == null)
			return false;
		
		log.info("Eliminando fichero en la ruta => " + file.getPath());
		return file.delete();
	}
}



