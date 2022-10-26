package com.marcelo.tfg.provider;

import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;

public interface KettleProvider {

	KettleDto executeKettleTransformation(MultipartFile kettleFile);

	KettleDto executeKettleJob(MultipartFile kettleFile);
}
