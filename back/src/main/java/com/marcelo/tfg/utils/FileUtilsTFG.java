package com.marcelo.tfg.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilidades para tratar con ficheros
 * 
 * @author mcarro
 *
 */

@Slf4j
public class FileUtilsTFG {

	/**
	 * Convierte el MultipartFile a un File temporal. Se da la opcion de añadir la extension.
	 * Ejemplo: "ktr"
	 * 
	 * Devuelve null si no se puede hacer la conversion.
	 * 
	 * @param fileToConvert - El fichero a convertir
	 * @param extension - La extension del fichero
	 * 
	 * @return File guardado en la carpeta temporal del sistema
	 */
	public static File convertMultipartFileToTmpFile(MultipartFile fileToConvert, String extension) {
		if (fileToConvert == null)
			return null;

		File temp = null;
		FileOutputStream fos = null;
		try {
			log.info("Creando fichero temporal para " + fileToConvert.getOriginalFilename());
			String nombre = getFileName(fileToConvert);

			// Parameter 3 - Default tmp directory of the system
			temp = File.createTempFile(Constantes.KETTLE_PREFIX + nombre, "." + extension, null);

			fos = new FileOutputStream(temp);
			fos.write(fileToConvert.getBytes());
			
			log.info("Conversion terminada, ruta completa del fichero: " + temp.getPath());
		} catch (IOException e) {
			log.error("Error al convertir fichero " + fileToConvert.getName(), e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error("Error al cerrar la transmision de fos", e);
					e.printStackTrace();
				}
			}
		}

		log.info("Tam del fichero convertido: " + temp.length() + " bytes");
		return temp;
	}

	/**
	 * Elimina el fichero pasado como parametro.
	 * 
	 * @param file - El fichero a eliminar
	 * 
	 * @return true si se ha eliminado el fichero o si no existe. False si no se ha podido
	 * borra todos los fichero
	 */
	public static boolean deleteFile(File file) {
		if (file == null)
			return true;

		log.info("Eliminando fichero en la ruta => " + file.getPath());
		return file.delete();
	}

	/**
	 * Elimina los ficheros pasados como parametros.
	 * 
	 * @param files - Los ficheros a eliminar
	 * 
	 * @return El numero de ficheros eliminados
	 */
	public static int deleteMultipleFiles(List<File> files) {
		if (files == null)
			return 0;

		int filesDeleted = 0;
		for (File file : files) {
			if (file == null)
				continue;

			log.info("Eliminando fichero en la ruta => " + file.getPath());
			file.delete();
			filesDeleted++;
		}

		return filesDeleted;
	}
	
	public static String getFileName(File file) {
		String[] fileSplit = file.getName().split("\\.");
		return fileSplit.length > 0 ? fileSplit[0] : null;
	}
	
	public static String getFileName(MultipartFile file) {
		String[] fileSplit = file.getOriginalFilename().split("\\.");
		return fileSplit.length > 0 ? fileSplit[0] : null;
	}

	public static String getFileExtension(File file) {
		String[] fileSplit = file.getName().split("\\.");
		return fileSplit.length > 1 ? fileSplit[fileSplit.length-1] : null;
	}
	
	public static String getFileExtension(MultipartFile file) {		
		String[] fileSplit = file.getOriginalFilename().split("\\.");
		return fileSplit.length > 1 ? fileSplit[fileSplit.length-1] : null;
	}
}
