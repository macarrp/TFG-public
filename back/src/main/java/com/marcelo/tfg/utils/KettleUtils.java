package com.marcelo.tfg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	 * Recoge los logs de Kettle incluyendo un salto de linea y los almacena en una
	 * String
	 * 
	 * @param logChannelId - id del canal de Kettle
	 * @return String - Los logs de la ejecucion
	 */
	public static String getKettleLogs(String logChannelId) {
		List<KettleLoggingEvent> kle = KettleLogStore.getLogBufferFromTo(logChannelId, true, 0,
				KettleLogStore.getLastBufferLineNr());

		String logResult = "";
		for (KettleLoggingEvent logEvent : kle) {
			logResult += logEvent.getMessage() + " \n";
		}
		KettleLogStore.discardLines(logChannelId, true);

		return logResult;
	}

	/**
	 * Verifica que el argumento pasado como parámetro tiene la extensión correcta
	 * 
	 * @param kettleFile - Verifica la extension del fichero
	 * @return true si es ktr, kjb o xml. Falso en caso contrario
	 */
	public static Boolean checkExtensionKettle(File kettleFile) {
		String[] fileParts = kettleFile.getName().split("\\.");
		String extension = fileParts[fileParts.length - 1];
		return Constantes.Extension.KTR.equals(extension) || Constantes.Extension.KJB.equals(extension)
				|| Constantes.Extension.XML.equals(extension);
	}

	/**
	 * Modifica la ruta de los ficheros adjuntos de la transformación por la ruta
	 * temporal del sistema
	 * 
	 * @param kettleFile    - La transformacion ktr
	 * @param llevaAdjuntos - Booleano que controla si la transformacion lleva
	 *                      adjuntos
	 * 
	 * @return File con la ruta interior modificada
	 */
	public static File modifyXmlTransformationPath(File kettleFile) {
		String msgError = "El fichero no se ha modificado \n";

		FileWriter writer = null;

		try (InputStream is = new FileInputStream(kettleFile)) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			NodeList rootChildrenNodes = doc.getChildNodes();

			for (int rootIndex = 0; rootIndex < rootChildrenNodes.getLength(); rootIndex++) {
				Node rootNode = rootChildrenNodes.item(rootIndex);
				if (rootNode.getNodeType() == Node.ELEMENT_NODE) {
					Element rootElement = (Element) rootChildrenNodes.item(rootIndex);

					NodeList fileNodeList = rootElement.getElementsByTagName("file");
					NodeList filenameNodeList = rootElement.getElementsByTagName("filename");

					convertNameTags(fileNodeList);
					convertFilenameTags(filenameNodeList);
				}
			}
			writeDocument(doc, kettleFile, writer);

		} catch (Exception e) {
			log.error("ERROR: " + msgError, e);
		}

		return kettleFile;
	}

	private static void convertNameTags(NodeList fileNodeList) throws IOException {
		int tam = fileNodeList.getLength();
		for (int fileNodeIndex = 0; fileNodeIndex < tam; fileNodeIndex++) {
			Node fileNode = fileNodeList.item(fileNodeIndex);
			if (fileNode != null && fileNode.getNodeType() == Node.ELEMENT_NODE) {
				Element fileElement = (Element) fileNodeList.item(fileNodeIndex);
				NodeList nameNodeList = fileElement.getElementsByTagName("name");

				searchNode("name", nameNodeList);
			}
		}
	}

	private static void convertFilenameTags(NodeList filenameNodeList) throws IOException {
		searchNode("filename", filenameNodeList);
	}

	private static void searchNode(String searchNodeName, NodeList nodeList) throws DOMException, IOException {
		int tam = nodeList.getLength();
		for (int nodeIndex = 0; nodeIndex < tam; nodeIndex++) {
			Node nameNode = nodeList.item(nodeIndex);
			if (nameNode.getNodeType() == Node.ELEMENT_NODE) {
				Element nameElement = (Element) nodeList.item(nodeIndex);
				if (searchNodeName.equalsIgnoreCase(nameElement.getTagName())) {
					tagFound(nameElement);
				}
			}
		}
	}

	private static void tagFound(Element element) throws DOMException, IOException {
		File filepath = new File(element.getTextContent());
		String fileName = FileUtilsTFG.getFileName(filepath);
		String fileExtension = FileUtilsTFG.getFileExtension(filepath); // Si es null, se trata de un fichero de salida

		if ("template".equalsIgnoreCase(fileName)) { // Caso particular
			return;
		}

		log.info("Ruta VIEJA encontrada dentro de la transformacion: " + filepath);

		// Cambiamos la ruta del fichero para que apunte a la ruta temporal
		List<Path> allPossiblePaths = findByFilenameStartsWith(new File(Constantes.TMP_DIR).toPath(),
				Constantes.KETTLE_PREFIX);
		List<File> possiblePaths = getFilePathsByExtension(allPossiblePaths, fileExtension);
		changePathToTempFiles(possiblePaths, element, fileName, fileExtension);

		log.info("Ruta NUEVA dentro del ktr: " + element.getTextContent());
	}

	private static List<Path> findByFilenameStartsWith(Path path, String filename) throws IOException {

		List<Path> result;
		try (Stream<Path> pathStream = Files.find(path, Integer.MAX_VALUE,
				(p, basicFileAttributes) -> p.getFileName().toString().startsWith(filename))) {
			result = pathStream.collect(Collectors.toList());
		}
		return result;
	}

	private static List<File> getFilePathsByExtension(List<Path> paths, String extension) throws IOException {
		List<File> result = new ArrayList<File>();

		for (Path path : paths) {
			File file = path.toFile();
			if (extension == null) {
				result.add(file);
			} else {
				String ext = FileUtilsTFG.getFileExtension(file);
				if (extension.equalsIgnoreCase(ext)) {
					result.add(file);
				}
			}
		}

		return result;
	}

	private static void changePathToTempFiles(List<File> possiblePaths, Element name, String nombreFichDentroNodo,
			String fileExtension) {
		for (File pathKettleFile : possiblePaths) {

			String pathKettleFilename = FileUtilsTFG.getFileName(pathKettleFile);
			String pathFilename = pathKettleFilename.split(Constantes.KETTLE_PREFIX)[1];

			if (fileExtension == null) {
				name.setTextContent(Constantes.TMP_DIR + nombreFichDentroNodo);
				break;
			}

			if (pathFilename.startsWith(nombreFichDentroNodo)) {
				name.setTextContent(Constantes.TMP_DIR + pathKettleFilename + "." + fileExtension);
				break;
			}
		}
	}

	private static void writeDocument(Document doc, File kettleFile, FileWriter writer)
			throws IOException, TransformerException {
		try {
			DOMSource source = new DOMSource(doc);
			writer = new FileWriter(kettleFile);
			StreamResult result = new StreamResult(writer);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					log.error("Error al cerrar la transmision de FileWriter", e);
					e.printStackTrace();
				}
			}
		}
	}
}
