package com.marcelo.tfg.utils;

public class Constantes {

	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	
	public static final String KETTLE_PREFIX = "kettle_";
	
	public static final class Extension {
		private Extension() {
			
		}
		
		public static final String KTR = "ktr";
		public static final String KJB = "kjb";
		public static final String XML = "xml";
	}
	
//	public static final String PLUGINS_FOLDER = "src/main/resources/kettle_plugins";
	public static final String PLUGINS_FOLDER = "kettle_plugins";
}
