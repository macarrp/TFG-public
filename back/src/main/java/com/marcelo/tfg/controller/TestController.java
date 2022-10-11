package com.marcelo.tfg.controller;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

	@PostMapping("/kettle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void testKettle(@RequestParam("file") MultipartFile file) {

		File kettleFileDirectory = new File("src/main/kettle-jobs");
		
		log.info(file.getName());
		
//		File kettleJob = null;
//		try {
//			kettleJob = new File(kettleFileDirectory.getCanonicalFile() + "/sigpeac_usuarios_normal.ktr");
//			
//		} catch (IOException e) {
//			log.error("Error al recuperar el archivo");
//		}
		
//		return kettleJob != null && kettleJob.exists() ? "Si" : "No";

//		return kettleFileDirectory.getAbsolutePath();

//		try {
//			KettleEnvironment.init();
//            TransMeta metaData = new TransMeta("sigpeac_usuarios_normal.ktr");
//            Trans trans = new Trans(metaData);            
//            trans.setLogLevel(LogLevel.BASIC);
//            trans.execute(null);
//            trans.waitUntilFinished();
//            if (trans.getErrors() > 0) {
//                log.error("Error Ejecutando la transformacion");
//                return "Error Ejecutando la transformacion";
//            }
//            
//            return "Transformacion ejecutada con éxito";
//        } catch (KettleException e) {
//            log.error("Error ejecutando transf", e);
//            return "Error Ejecutando la transformacion";
//        }
	}

}
