package es.aragon.espresbk.kettle_tfg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUploadUtils {

	/*
	 * Convierte el MultipartFile pasado como parametro como File, devuelve null en caso contrario
	 */
	public static File convertMultipartFileToTmpFile(MultipartFile fileToConvert) {
		if(fileToConvert == null)
			return null;
		
		File temp = null;
		try {
			log.info("Creando fichero temporal");
			temp = File.createTempFile("kettle_", ".ktr", null); 
			// null 1 - suffix .tmp
			// null 2 - Default tmp directory of the system
			
			log.info("Fichero temporal creado con exito: " + temp.getName() + " \nVolcando bytes...");

			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(fileToConvert.getBytes());
			fos.close();

			log.info("Conversion terminada, ruta completa del fichero: " + temp.getPath());
		} catch (IOException e) {
			log.error("Error al convertir fichero " + fileToConvert.getName(), e);
		}
		
		log.info("Tamaño del fichero: " + temp.getTotalSpace() + " bytes");
		return temp;
	}

}



