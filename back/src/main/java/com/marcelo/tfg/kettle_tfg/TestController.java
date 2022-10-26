package es.aragon.espresbk.kettle_tfg;

import java.io.File;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

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

import es.aragon.core.common.beans.MessageResponseDto;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/test")
public class TestController {
	
	private int erroresTransformacion;
	
	@PostMapping("/kettle")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public MessageResponseDto<String> testKettle(@RequestParam MultipartFile file) {
		File temp = FileUploadUtils.convertMultipartFileToTmpFile(file);

		if (temp == null)
			return MessageResponseDto.fail("No se ha encontrado el archivo");

		String logKettle = executeKettleTransformation(temp); // Revisar
		int errores = getErroresTransformacion();
		
		if(temp != null && temp.exists()) {
			log.info("Eliminando archivo temporal => " + temp.getPath());
			temp.delete();
			log.info("Archivo temporal eliminado.");
		}
		
		if(errores != 0)
			return MessageResponseDto.fail(logKettle);
		else
			return MessageResponseDto.success(logKettle);
	}

	private String executeKettleTransformation(File kettleJob)  {
		String logResult = "";
		try {
			log.info("Inicializando KettleEnvironment");

			// WARNING: Tiene que ser false! Si no, el despliegue falla
			KettleEnvironment.init(false);

			log.info("Inicializado KettleEnvironment: " + KettleEnvironment.isInitialized());
			TransMeta metaData = new TransMeta(kettleJob.getPath());
			Trans trans = new Trans(metaData);
			trans.setLogLevel(LogLevel.BASIC);
			
			String logChannelId = trans.getLogChannelId();
			
			trans.execute(null);
			trans.waitUntilFinished();
			
			int errores = trans.getErrors();
			setErroresTransformacion(errores);
			
			log.info("Transformacion ejecutada, recogiendo logs...");
			List<KettleLoggingEvent> kle = KettleLogStore.getLogBufferFromTo(logChannelId, true, 0, KettleLogStore.getLastBufferLineNr());
			logResult = kettleLogToString(kle);
		    
		    log.info("Logs recogidos, limpiando...");
		    KettleLogStore.discardLines(logChannelId, true);
		    
		    log.info("Errores: " + errores); // Si hay errores, devolver fail
		    KettleEnvironment.shutdown();
		    
		    
		} catch (KettleException e) {
			logResult = "Error al ejecutar la transformacion de Kettle";
			log.error(logResult, e);			
		}
		
		return logResult;
	}
	
	private String kettleLogToString(List<KettleLoggingEvent> kle) {
		String logResult = "";
		for(KettleLoggingEvent logEvent : kle) {
			logResult += logEvent.getMessage() + " \n";
		}
		
		return logResult;
	}
	
	private int getErroresTransformacion() {
		return this.erroresTransformacion;
	}
	
	private void setErroresTransformacion(int errores) {
		this.erroresTransformacion = errores;
	}

}
