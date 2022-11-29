package com.marcelo.integramodulo.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marcelo.tfg.provider.KettleProvider;

@RestController
public class HelloController {

	@Autowired
	KettleProvider kettleProvider;
	
	@GetMapping("/hello")
	public String index() {
		return "Servidor inicializado";
	}
}
