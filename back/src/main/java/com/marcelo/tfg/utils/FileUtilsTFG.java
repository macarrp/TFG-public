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
			log.info("Creando fichero temporal...");
			String nombreCompletoArchivo = fileToConvert.getOriginalFilename();
			String nombre = nombreCompletoArchivo.split("\\.")[0];

			// Parameter 3 - Default tmp directory of the system
			temp = File.createTempFile("kettle_" + nombre, "." + extension, null);

			log.info("Fichero temporal creado con exito, volcando bytes...");

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

		log.info("Tam del fichero: " + temp.length() + " bytes");
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
	 * @return true si todos los ficheros se han eliminado o si la lista esta vacia. False si no se han podido
	 * borrar todos los ficheros
	 */
	public static boolean deleteMultipleFiles(List<File> files) {
		if (files == null)
			return true;

		int filesDeleted = 0;
		for (File file : files) {
			if (file == null)
				continue;

			log.info("Eliminando fichero en la ruta => " + file.getPath());
			file.delete();
			filesDeleted++;
		}

		return filesDeleted == files.size();
	}
}
