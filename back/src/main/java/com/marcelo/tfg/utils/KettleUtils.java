package com.marcelo.tfg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase con utilidades de Kettle
 * 
 * @author mcarro
 * 
 */
@Slf4j
public class KettleUtils {
	
	/**
	 * Recoge los logs de Kettle incluyendo un salto de linea y 
	 * los almacena en una String
	 * 
	 * @param logChannelId
	 * @return String
	 */
	public static String getKettleLogs(String logChannelId) {
		List<KettleLoggingEvent> kle = KettleLogStore.
				getLogBufferFromTo(logChannelId, true, 0, KettleLogStore.getLastBufferLineNr());
		
		String logResult = "";
		for(KettleLoggingEvent logEvent : kle) {
			logResult += logEvent.getMessage() + " \n";
		}
	    KettleLogStore.discardLines(logChannelId, true);
		
		return logResult;
	}
	
	/**
	 * Verifica que el argumento pasado como parámetro tiene extensión ktr o kbj
	 * 
	 * @param kettleFile
	 * @return true si es '.ktr' o 'kjb'. Falso en caso contrario
	 */
	public static Boolean checkExtensionKettle(MultipartFile kettleFile) {
		String[] fileParts = kettleFile.getOriginalFilename().split("\\.");
		String extension = fileParts[fileParts.length -1]; 
		return extension.equals("ktr") || extension.equals("kjb") || extension.equals("xml");
	}
	
	/**
	 * Modifica la ruta de los ficheros adjuntos de la transformación por la ruta temporal del sistema
	 * 
	 * @param kettleFile - La transformacion ktr
	 * @param llevaAdjuntos - Booleano que controla si la transformacion lleva adjuntos
	 * 
	 * @return File con la ruta interior modificada
	 */
	public static File modifyXmlTransformationPath(File kettleFile, boolean llevaAdjuntos) {
		String msgError = "El fichero no se ha modificado \n";
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
								log.info("Ruta encontrada DENTRO del ktr: " + name.getTextContent());
								
								String[] nombreNodoSplit = new File(name.getTextContent()).getName().split("\\.");
								String nombreDentroNodo = nombreNodoSplit[0];
								
								// Cambiamos la ruta del fichero para que apunte a la ruta temporal
								if(llevaAdjuntos) {
									List<Path> possiblePaths = findByFilenameStartsWith(new File(Constantes.tmpDir).toPath(), "kettle_");
									for (Path path : possiblePaths) {
										String pathKettleFilename = new File(path.toString()).getName();
										String pathFilename = pathKettleFilename.split("kettle_")[1];
										
										// Si no tiene extension, es un fichero de salida
										// Es decir, el array tiene tam 1
										if(nombreNodoSplit.length <= 1) {
											name.setTextContent(Constantes.tmpDir + nombreNodoSplit[0]);
											continue;
										}
										
										if(pathFilename.startsWith(nombreDentroNodo)) {
											log.info("fichero encontrado, asignando..." + Constantes.tmpDir + pathKettleFilename);
											name.setTextContent(Constantes.tmpDir + pathKettleFilename);
										}
									}
								}
								else {
									File nuevaRuta = new File(Constantes.tmpDir + nombreDentroNodo);
									name.setTextContent(nuevaRuta.getCanonicalPath());
								}
								
								log.info("Ruta nueva DENTRO del ktr: " + name.getTextContent());
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

			
		} catch (FileNotFoundException e) {
			log.error("Fichero no encontrado. " + msgError, e);
		} catch (IOException e) {
			log.error("Error de InputStream. " + msgError, e);
		} catch (ParserConfigurationException e) {
			log.error("Error al crear DocumentBuilder. " + msgError, e);
		} catch (SAXException e) {
			log.error("Error al crear Document. " + msgError, e);
		} catch (TransformerConfigurationException e) {
			log.error("Error al crear Transformer. " + msgError, e);
		} catch (TransformerException e) {
			log.error("Error al crear el fichero final. " + msgError, e);
		}
		
		return kettleFile;
	}
	
	private static List<Path> findByFilenameStartsWith(Path path, String filename)
            throws IOException {

        List<Path> result;
        try (Stream<Path> pathStream = Files.find(path,
                Integer.MAX_VALUE,
                (p, basicFileAttributes) ->
                        p.getFileName().toString().startsWith(filename))
        ) {
            result = pathStream.collect(Collectors.toList());
        }
        return result;

    }
}
