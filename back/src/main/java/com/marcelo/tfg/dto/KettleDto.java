package com.marcelo.tfg.dto;

import lombok.Data;

/**
 * DTO compuesto por un mensaje, 
 * logs de la ejecucion y
 * el numero de errores
 * 
 * @author mcarro
 * 
 */

@Data
public class KettleDto {

	private String mensaje; 
	
	private String log;
	
	private int errores;
}
