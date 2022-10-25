package com.marcelo.tfg.controller;

import java.io.File;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.Utils.FileUploadUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/test")
public class TestController {

	private File kettleFileDirectory = new File("src/main/kettle-jobs"); // CONST ?

//	@Operation(summary = "Test kettle")
	@PostMapping("/kettle")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public ResponseEntity<String> testKettle(@RequestParam MultipartFile file) {		
		File temp = FileUploadUtils.convertMultipartFileToFile(kettleFileDirectory, file);

		if (temp == null)
			return new ResponseEntity<String>("No se ha encontrado el archivo", HttpStatus.BAD_REQUEST);

		Boolean isFinished = executeKettleTransformation(temp); // Revisar 
		
		return isFinished ? new ResponseEntity<String>("La transformacion se ha ejecutado con exito", HttpStatus.OK)
		: new ResponseEntity<String>("No se ha terminado la transformacion", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private Boolean executeKettleTransformation(File kettleJob)  {
		try {
//			File ruta = new File("src/main/simple-jndi");
			Const.JNDI_DIRECTORY = "src/main/simple-jndi";
			log.info("Inicializando KettleEnvironment");
//			System.getProperty( "KETTLE_JNDI_ROOT" );
//			System.setProperty("KETTLE_JNDI_ROOT", "simple-jndi");
			
			KettleEnvironment.init();
			log.info("jndi dir: " + Const.JNDI_DIRECTORY);
			TransMeta metaData = new TransMeta(kettleJob.getPath());
			Trans trans = new Trans(metaData);
			trans.setLogLevel(LogLevel.BASIC);
			trans.execute(null); // <-- Arguments, variables de la transformacion?
			trans.waitUntilFinished();
			if (trans.getErrors() > 0) {
				log.error("Error ejecutando la transformacion");
			}
			
			return trans.isFinished();
		} catch (KettleException e) {
			log.error("Error al ejecutar la transformacion de Kettle", e);
			return false;
		}
		
	}

}
