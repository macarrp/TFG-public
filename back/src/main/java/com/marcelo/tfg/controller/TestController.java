package com.marcelo.tfg.controller;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.Utils.FileUploadUtils;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

	private File kettleFileDirectory = new File("src/main/kettle-jobs");

	@Operation(summary = "Test kettle")
	@PostMapping("/kettle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String testKettle(@RequestParam("file") MultipartFile file) {

		File temp = FileUploadUtils.convertMultipartFileToFile(kettleFileDirectory, file);

		if (temp == null)
			return "Error";

		Boolean isFinished = executeKettleTransformation(temp);
		
		return isFinished ? "Transformación ejecutada con éxito" : "No pudo completarse la transformación";
	}

	private Boolean executeKettleTransformation(File kettleJob)  {
		try {
			KettleEnvironment.init();
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
			log.error("Error al ejecutar la transformación de Kettle");
			return false;
		}
		
	}

}
