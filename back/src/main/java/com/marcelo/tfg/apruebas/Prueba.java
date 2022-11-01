package com.marcelo.tfg.apruebas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Prueba {

	public static void main(String[] args) throws IOException {
		modifyXmlTransformationPath(new File("Kettle_transformations/EntradaTxtSalidaExcel.ktr"));
	}
	
	public static File modifyXmlTransformationPath(File kettleFile) {
		String msgError = "El fichero no se ha modificado";
		log.info("tam file: " + kettleFile.length());

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
								
								name.setTextContent(kettleFile.getCanonicalPath()); 

								log.info("Ruta nueva del ktr: " + name.getTextContent());
							}
						}
					}
				}
			}

			// write the content into xml file
			DOMSource source = new DOMSource(doc);
			FileWriter writer = new FileWriter(new File("output.ktr"));
			StreamResult result = new StreamResult(writer);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);

			return new File("output.ktr");
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
