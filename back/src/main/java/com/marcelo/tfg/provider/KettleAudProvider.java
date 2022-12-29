package com.marcelo.tfg.provider;

import java.io.File;

public interface KettleAudProvider {

	boolean registrarTransformacion(File ktr, String logsTransformacion) throws Exception;
}
