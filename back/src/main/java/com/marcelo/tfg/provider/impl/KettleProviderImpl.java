package com.marcelo.tfg.provider.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.marcelo.tfg.dto.KettleDto;
import com.marcelo.tfg.provider.KettleProvider;
import com.marcelo.tfg.utils.FileUtilsTFG;
import com.marcelo.tfg.utils.KettleUtils;
import com.marcelo.tfg.utils.enums.LogLevelKettle;

@Component
public class KettleProviderImpl extends KettleBaseProviderImpl implements KettleProvider {

	@Override
	public KettleDto executeTransformation(MultipartFile kettleFile, List<MultipartFile> adjuntosMultipart,
			LogLevelKettle logLevel) {
		KettleDto ktr = new KettleDto();

		if (!checkExtension(kettleFile, ktr))
			return ktr;

		File tempKettleFile = FileUtilsTFG.convertMultipartFileToTmpFile(kettleFile);

		if (!checkConversion(tempKettleFile, ktr))
			return ktr;

		List<File> adjuntosFile = null;

		if (adjuntosMultipart != null && adjuntosMultipart.size() > 0) {
			adjuntosFile = new ArrayList<>();
			convertAndAddToFileList(adjuntosFile, adjuntosMultipart);
		}

		executeKettleFile(tempKettleFile, adjuntosFile, logLevel, ktr);

		return ktr;
	}

	@Override
	public KettleDto executeTransformation(File kettleFile, List<File> adjuntos, LogLevelKettle logLevel) {
		KettleDto ktr = new KettleDto();

		if (!checkExtension(kettleFile, ktr))
			return ktr;

		executeKettleFile(kettleFile, adjuntos, logLevel, ktr);

		return ktr;
	}

	public void executeKettleFile(File kettleFile, List<File> adjuntos, LogLevelKettle logLevel, KettleDto ktr) {
		try {
			kettleFile = KettleUtils.modifyXmlTransformationPath(kettleFile);

			execute(kettleFile, adjuntos, logLevel, ktr);
		} finally {
			eliminaFichero(kettleFile);
			eliminaAdjuntosKtr(adjuntos);
		}
	}

	public Map<String, Integer> getNumberOfInputOutputTypes(MultipartFile kettleFile) {
		File tempKettleFile = FileUtilsTFG.convertMultipartFileToTmpFile(kettleFile);

		Map<String, Integer> str = null;
		try {
			str = KettleUtils.getNumberOfInputOutputTypes(tempKettleFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			eliminaFichero(tempKettleFile);
		}

		return str;
	}

}
