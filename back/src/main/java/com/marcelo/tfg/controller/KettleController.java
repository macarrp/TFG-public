package com.marcelo.tfg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;
import com.marcelo.tfg.provider.KettleProvider;
import com.marcelo.tfg.utils.dto.MessageResponseDto;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/test")
public class KettleController {

	@Autowired
	KettleProvider kettleProvider;

	@PostMapping(value = "/kettle-transformation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MessageResponseDto<KettleDto> testKettleTransformation(@RequestParam MultipartFile file) {
		KettleDto ktr = null;
		try {
			ktr = kettleProvider.executeKettleTransformation(file);
			return MessageResponseDto.success(ktr);
		} catch (Exception e) {
			log.error("Error al ejecutar la transformacion de Kettle", e);
			return MessageResponseDto.fail("Error al ejecutar la transformacion de Kettle");
		}
	}
	
	@PostMapping(value = "/kettle-job", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MessageResponseDto<KettleDto> testKettleJob(@RequestParam MultipartFile file) {
		KettleDto ktr = null;
		try {
			ktr = kettleProvider.executeKettleJob(file);
			return MessageResponseDto.success(ktr);
		} catch (Exception e) {
			log.error("Error al ejecutar el trabajo de Kettle", e);
			return MessageResponseDto.fail("Error al ejecutar el trabajo de Kettle");
		}
	}

}
