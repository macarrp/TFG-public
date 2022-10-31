package com.marcelo.tfg.apruebas;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Prueba {

//	For staff id 1001
//
//	Remove the XML element name.
//	For the XML element role, update the value to "founder".
//	For staff id 1002
//
//	Update the XML attribute to 2222.
//	Add a new XML element salary, contains attribute and value.
//	Add a new XML comment.
//	Rename an XML element, from name to n (remove and add).

	public static void main(String[] args) {
		File ktr = new File("Kettle_transformations/EntradaTxtSalidaExcel.ktr");
		log.info("tam file: " + ktr.length());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try (InputStream is = new FileInputStream(ktr)) {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			NodeList listOfFiles = doc.getElementsByTagName("file");

			for (int i = 0; i < listOfFiles.getLength(); i++) {
				Node file = listOfFiles.item(i);
				if (file.getNodeType() == Node.ELEMENT_NODE) {
					NodeList childNodes = file.getChildNodes();
					for(int j = 0; j < childNodes.getLength(); j++) {
						Node item = childNodes.item(j);
						if (item.getNodeType() == Node.ELEMENT_NODE) {							
							if("name".equalsIgnoreCase(item.getNodeName())) {
								log.info("Ruta encontrada: " + item.getTextContent());

								// Cambiamos la ruta del fichero para que apunte
								// a la ruta temporal								
								String[] rutaCompleta = item.getTextContent().split("\\\\");
								
								String nombreArchivo = rutaCompleta[rutaCompleta.length-1];
								item.setTextContent("C:/Test/" + nombreArchivo);
								
								log.info("Ruta nueva: " + item.getTextContent());
							}
						}
					}
					
				}
			}

			// output to console
			// writeXml(doc, System.out);

			try (FileOutputStream output = new FileOutputStream("C:\\test\\kettle-modified.ktr")) {
				try {
					writeXml(doc, output);
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// write doc to output stream
    private static void writeXml(Document doc,
                                 OutputStream output)
            throws TransformerException, UnsupportedEncodingException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        // The default add many empty new line, not sure why?
        // https://mkyong.com/java/pretty-print-xml-with-java-dom-and-xslt/
        // Transformer transformer = transformerFactory.newTransformer();

        // add a xslt to remove the extra newlines
        Transformer transformer = transformerFactory.newTransformer(
                new StreamSource(new File("src/main/java/apruebas/xslt-format.xslt")));

        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }

}
