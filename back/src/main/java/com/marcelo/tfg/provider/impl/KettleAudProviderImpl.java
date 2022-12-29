package com.marcelo.tfg.provider.impl;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.marcelo.tfg.provider.KettleAudProvider;
import com.marcelo.tfg.repository.KettleAudRepository;
import com.marcelo.tfg.repository.entity.KettleAudEntity;
import com.marcelo.tfg.utils.KettleUtils;

@Component
public class KettleAudProviderImpl implements KettleAudProvider {

	@Autowired
	KettleAudRepository kettleAudRepository;
	
	@Override
	public boolean registrarTransformacion(File ktr, String logsTransformacion) throws Exception {
		KettleAudEntity kettleAudEntity = new KettleAudEntity();
		
		kettleAudEntity.setIdUsuario(1L);
		kettleAudEntity.setFechaHora(new Date());
		
		String operaciones = KettleUtils.getNumberOfInputOutputTypes(ktr).toString();
		kettleAudEntity.setOperacionesRealizadasJson(operaciones);
		
		kettleAudEntity.setFicheroTransformacion(FileUtils.readFileToByteArray(ktr));
		kettleAudEntity.setLogsTransformacion(logsTransformacion);
		
		kettleAudEntity = kettleAudRepository.save(kettleAudEntity);
		
		boolean registrada = kettleAudEntity.getIdAuditoria() != null;
		
		return registrada;
	}

}
