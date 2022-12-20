package com.marcelo.tfg.controller;

import java.util.List;
import java.util.Map;

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
import com.marcelo.tfg.utils.enums.LogLevelKettle;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/kettle")
public class KettleController {

	@Autowired
	KettleProvider kettleProvider;
	
	@PostMapping(value = "/transformation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public MessageResponseDto<KettleDto> runKettleTransformation(@RequestParam MultipartFile kettle, 
			@RequestParam(required = false) List<MultipartFile> files,
			@RequestParam(required = false) LogLevelKettle logLevelKettle) {
		try {
			KettleDto ktr = kettleProvider.executeTransformation(kettle, files, logLevelKettle);
			return MessageResponseDto.success(ktr);
		} catch (Exception e) {
			String errorMsg = "Error al ejecutar la transformacion de Kettle";
			log.error(errorMsg, e);
			return MessageResponseDto.fail(errorMsg);
		}
	}
	
	@PostMapping(value = "/origin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Integer> getOriginKtr(@RequestParam MultipartFile kettle) {
		return kettleProvider.getNumberOfInputOutputTypes(kettle);
	}

}
