package com.marcelo.tfg.provider;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;

public interface KettleProvider {

	KettleDto executeKettleTransformation(MultipartFile kettleFile);
	
	KettleDto executeKettleTransformationWithAttachments(MultipartFile kettleFile, List<MultipartFile> files);

	KettleDto executeKettleJob(MultipartFile kettleFile);
}
