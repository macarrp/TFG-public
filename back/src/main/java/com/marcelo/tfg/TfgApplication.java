package com.marcelo.tfg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class TfgApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(TfgApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TfgApplication.class);
    }

	/**
	 * Sets the initial configuration.
	 *
	 * @param startupConfig the startup config
	 *
	 * @return the startup config
	 */
//	@Override
//	public StartupConfig setInitialConfiguration(StartupConfig startupConfig) {
//		startupConfig.setProjectCode(Constants.PROJECT_CODE);
//		startupConfig.setVersionApi(Constants.VERSION_API);
//		startupConfig.setEmbedDeployEnvironment(Constants.EMBEB_DEPLOY_ENVIROMENT);
//		startupConfig.setEnvironmentVariableName(Constants.ENVIROMENT_VARIABLE_NAME);
//		return startupConfig;
//	}
}
