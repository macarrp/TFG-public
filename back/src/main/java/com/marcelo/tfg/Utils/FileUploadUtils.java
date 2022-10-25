package com.marcelo.tfg.Utils;

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
	public static File convertMultipartFileToFile(File baseDirectory, MultipartFile fileToConvert) {
		File temp = null;
		try {
			temp = new File(baseDirectory + File.separator + fileToConvert.getOriginalFilename());
			temp.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(fileToConvert.getBytes());
			fos.close();
			
			log.info("ruta completa fich: " + temp.getPath());
		} catch (IOException e) {
			log.error("Error al convertir fichero " + fileToConvert.getName(), e);
		}
		log.info("fich " + temp.getTotalSpace() + " bytes");
		return temp;
	}
	
}
