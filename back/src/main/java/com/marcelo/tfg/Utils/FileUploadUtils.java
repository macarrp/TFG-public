package com.marcelo.tfg.Utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUploadUtils {

	/*
	 * Convierte el MultipartFile pasado como parámetro como File, devuelve null en caso contrario
	 */
	public static File convertMultipartFileToFile(File baseDirectory, MultipartFile fileToConvert) {
		File temp = null;
		try {
			temp = new File(baseDirectory.getCanonicalPath() + File.separator + fileToConvert.getOriginalFilename());
			fileToConvert.transferTo(temp);
		} catch (IOException e) {
			log.error("Error al convertir fichero");
			e.printStackTrace();
		}
		
		return temp;
	}
}
