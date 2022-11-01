package com.marcelo.tfg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	/**
	 * Convierte el MultipartFile a un File temporal. Devuelve null en caso
	 * contrario.
	 * 
	 * @param fileToConvert
	 * 
	 * @return File
	 */
	public static File convertMultipartFileToTmpFile(MultipartFile fileToConvert) {
		if (fileToConvert == null)
			return null;

		File temp = null;
		try {
			log.info("Creando fichero temporal");
			String nombreCompletoArchivo = fileToConvert.getOriginalFilename();
			String nombre = nombreCompletoArchivo.split("\\.")[0];

			// Parameter 3 - Default tmp directory of the system
			temp = File.createTempFile("kettle_" + nombre, ".ktr", null);

			log.info("Fichero temporal creado con exito, volcando bytes...");

			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(fileToConvert.getBytes());
			fos.close();

			log.info("Conversion terminada, ruta completa del fichero: " + temp.getPath());
		} catch (IOException e) {
			log.error("Error al convertir fichero " + fileToConvert.getName(), e);
		}

		log.info("Tam del fichero: " + temp.length() + " bytes");
		return temp;
	}

	/**
	 * Elimina el fichero pasado como parametro.
	 * 
	 * @param file
	 * 
	 * @return boolean
	 */
	public static boolean deleteFileIfExists(File file) {
		if (file == null)
			return false;

		log.info("Eliminando fichero en la ruta => " + file.getPath());
		return file.delete();
	}

	/**
	 * Elimina los ficheros pasados como parametros.
	 * 
	 * @param files
	 * 
	 * @return boolean
	 */
	public static boolean deleteMultipleFilesIfExists(List<File> files) {
		if (files == null)
			return false;

		int filesDeleted = 0;
		for (File file : files) {
			if (file == null)
				continue;

			log.info("Eliminando fichero en la ruta => " + file.getPath());
			file.delete();
			filesDeleted++;
		}

		return filesDeleted == files.size();
	}

	/**
	 * Modifica la ruta de la transformación por la ruta temporal del sistema
	 * 
	 * @param kettleFile
	 * 
	 * @return File 
	 */
	public static File modifyXmlTransformationPath(File kettleFile) {
		String msgError = "El fichero no se ha modificado";
		log.info("tam file: " + kettleFile.length() + " bytes");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try (InputStream is = new FileInputStream(kettleFile)) {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			NodeList listOfNodeFiles = doc.getElementsByTagName("file");

			for (int i = 0; i < listOfNodeFiles.getLength(); i++) {
				Node nodeFile = listOfNodeFiles.item(i);
				if (nodeFile.getNodeType() == Node.ELEMENT_NODE) {
					NodeList files = nodeFile.getChildNodes();
					for (int j = 0; j < files.getLength(); j++) {
						Node name = files.item(j);
						if (name.getNodeType() == Node.ELEMENT_NODE) {
							if ("name".equalsIgnoreCase(name.getNodeName())) {
								log.info("Ruta encontrada en el ktr: " + name.getTextContent());

								// Cambiamos la ruta del fichero para que apunte
								// a la ruta temporal
								
								// TODO: Revisar para tener en cuenta la extension
								name.setTextContent(kettleFile.getCanonicalPath()); 

								log.info("Ruta nueva del ktr: " + name.getTextContent());
							}
						}
					}
				}
			}

			// write the content into xml file
			DOMSource source = new DOMSource(doc);
			FileWriter writer = new FileWriter(kettleFile);
			StreamResult result = new StreamResult(writer);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);

			return kettleFile;
		} catch (FileNotFoundException e) {
			log.error("Fichero no encontrado. " + msgError, e);
			return kettleFile;
		} catch (IOException e) {
			log.error("Error de InputStream. " + msgError, e);
			return kettleFile;
		} catch (ParserConfigurationException e) {
			log.error("Error al crear DocumentBuilder. " + msgError, e);
			return kettleFile;
		} catch (SAXException e) {
			log.error("Error al crear Document. " + msgError, e);
			return kettleFile;
		} catch (TransformerConfigurationException e) {
			log.error("Error al crear Transformer. " + msgError, e);
			return kettleFile;
		} catch (TransformerException e) {
			log.error("Error al crear el fichero final. " + msgError, e);
			return kettleFile;
		}
	}
}
