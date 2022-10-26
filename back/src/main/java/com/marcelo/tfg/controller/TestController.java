package com.marcelo.tfg.controller;

import java.io.File;
import java.util.List;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
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
			return new ResponseEntity<>("No se ha encontrado el archivo", HttpStatus.BAD_REQUEST);

		String logKettle = executeKettleTransformation(temp); // Revisar

		return new ResponseEntity<>(logKettle, HttpStatus.OK);
	}

	private String executeKettleTransformation(File kettleJob)  {
		try {
			log.info("Inicializando KettleEnvironment");

			// INFO: Tiene que ser false! Si no, el despliegue falla
			KettleEnvironment.init(false);

			log.info("Inicializado KettleEnvironment: " + KettleEnvironment.isInitialized());
			TransMeta metaData = new TransMeta(kettleJob.getPath());
			Trans trans = new Trans(metaData);
			trans.setLogLevel(LogLevel.BASIC);
			
			String logChannelId = trans.getLogChannelId();
			
			trans.execute(null);
			trans.waitUntilFinished();
			int errores = trans.getErrors();
			
			log.info("Transformacion ejecutada, recogiendo logs...");
			List<KettleLoggingEvent> kle = KettleLogStore.getLogBufferFromTo(logChannelId, true, 0, KettleLogStore.getLastBufferLineNr());
		    String logKettle = kettleLogToString(kle);
		    
		    log.info("Logs recogidos, limpiando...");
		    KettleLogStore.discardLines(logChannelId, true);
		    
		    log.info("PDIExecutor : fine job, errores: ",errores);
		    KettleEnvironment.shutdown();

			return logKettle;
		} catch (KettleException e) {
			log.error("Error al ejecutar la transformacion de Kettle", e);
			return "Error al ejecutar la transformacion de Kettle";
		}
	}
	
	private String kettleLogToString(List<KettleLoggingEvent> kle) {
		String logResult = "";
		for(KettleLoggingEvent logEvent : kle) {
			logResult += logEvent.getMessage() + " \n";
		}
		
		return logResult;
	}

}
