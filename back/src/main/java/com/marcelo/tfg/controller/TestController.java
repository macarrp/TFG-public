package com.marcelo.tfg.controller;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

	@Operation(summary = "Test kettle")
	@PostMapping("/kettle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void testKettle(@RequestParam("file") MultipartFile file) {

		File kettleFileDirectory = new File("src/main/kettle-jobs");
		File temp = null;

		log.info(file.getOriginalFilename());

		try {
			temp = new File(kettleFileDirectory.getCanonicalPath() + File.separator + file.getOriginalFilename());

			file.transferTo(temp);
		} catch (IllegalStateException e) {
			log.error("Error sobre el estado del documento", e);
		} catch (IOException e) {
			log.error("Error sobre el estado del documento", e);
		}

		try {
			executeKettleTransformation(temp);
		} catch (KettleException e) {
			log.error("Error ejecutando la transformacion", e);
		}
	}

	private void executeKettleTransformation(File kettleJob) throws KettleException {
		KettleEnvironment.init();
		TransMeta metaData = new TransMeta(kettleJob.getPath());
		Trans trans = new Trans(metaData);
		trans.setLogLevel(LogLevel.BASIC);
		trans.execute(null); // <-- Arguments, variables de la transformacion?
		trans.waitUntilFinished();
		if (trans.getErrors() > 0) {
			log.error("Error ejecutando la transformacion");
		}
	}

}
