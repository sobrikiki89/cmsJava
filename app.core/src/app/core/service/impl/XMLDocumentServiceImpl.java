package app.core.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import app.core.service.XMLDocumentService;

@Service
public class XMLDocumentServiceImpl implements XMLDocumentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLDocumentServiceImpl.class);

	@Override
	public Document createXML() {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			return docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			LOGGER.error("Unable to create XML Document", e);
		}
		return null;
	}

	@Override
	public Node createField(Document xml, String name, String value) {
		Element node = xml.createElement(name);
		node.appendChild(xml.createTextNode(value));
		return node;
	}

	@Override
	public String toString(Document xml) {
		try {
			DOMSource domSource = new DOMSource(xml);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();

			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException e) {
			LOGGER.error("Unable to parse XML Document to String", e);
			return null;
		}
	}

	@Override
	public XMLErrorHandler validateXMLSchema(String xmlString, String schemaClassPath) {
		XMLErrorHandler handler = new XMLErrorHandler();
		try {

			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			ClassPathResource classPathResouce = new ClassPathResource(schemaClassPath);
			InputStream is = classPathResouce.getInputStream();
			if (is != null) {
				// Prepare schema
				Schema schema = factory.newSchema(new StreamSource(is));
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				docFactory.setSchema(schema);

				// Prepare xml
				DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();
				String xml = new String(xmlString.getBytes(Charset.forName("UTF-8")));
				xml = xml.trim().replaceFirst("^([\\W]+)<", "<");
				xml = xml.replaceAll("&(?!amp;)", "&amp;");
				InputSource input = new InputSource();
				input.setCharacterStream(new StringReader(xml));
				documentBuilder.setErrorHandler(handler);
				documentBuilder.parse(input);
			} else {
				LOGGER.warn("Given XML schema is not exists [" + schemaClassPath + "]");
				handler.getXmlErrorList()
						.add(new SAXException("Given XML schema is not exists [" + schemaClassPath + "]"));
			}
		} catch (SAXException e) {
			LOGGER.error("Unable to validate XML Document", e);
			handler.getXmlErrorList().add(e);
		} catch (IOException | ParserConfigurationException e) {
			LOGGER.error("Unable to parse before validate XML Document", e);
			handler.getXmlErrorList().add(new SAXException(
					schemaClassPath + "Unable to parse before validate XML Document " + e.getMessage()));
		}
		return handler;
	}

	@Override
	public Document toXML(String xmlString) {
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			String xml = new String(xmlString.getBytes(Charset.forName("UTF-8")));
			xml = xml.trim().replaceFirst("^([\\W]+)<", "<");
			xml = xml.replaceAll("&(?!amp;)", "&amp;");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("######## XML received start ##########");
				LOGGER.debug(xml);
				LOGGER.debug("######## XML received end ##########");
			}
			InputSource input = new InputSource();
			input.setCharacterStream(new StringReader(xml));

			return documentBuilder.parse(input);
		} catch (Exception e) {
			LOGGER.error("Unable to parse String to XML Document", e);
			return null;
		}
	}

	@Override
	public String getField(Document xml, String fieldName) {
		NodeList nodeList = xml.getElementsByTagName(fieldName);
		if (nodeList == null)
			return null;
		// only get the first appear element
		Node node = nodeList.item(0);
		if(node == null) {
			return null;
		}
		return node.getTextContent();
	}

	@Override
	public List<String[]> getListField(Document xml, String parentFieldName, String[] fieldNameArray) {		
		List<String[]> lst = new ArrayList<String[]>();
		NodeList nodeList = xml.getElementsByTagName(parentFieldName);
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element) node;
					String[] resultArray = new String[fieldNameArray.length];
					for(int j = 0; j < fieldNameArray.length; j++){
						resultArray[j] = eElement.getElementsByTagName(fieldNameArray[j]).item(0).getTextContent();
					}					
					lst.add(resultArray);
				}				
			}
		}
		return lst;
	}

	public class XMLErrorHandler implements ErrorHandler {

		private List<SAXException> xmlErrorList;
		private List<SAXException> xmlWarnList;

		public XMLErrorHandler() {
			xmlErrorList = new ArrayList<>();
			xmlWarnList = new ArrayList<>();
		}

		public boolean isAllPass() {
			return xmlErrorList.isEmpty();
		}

		public boolean isAllPassWithoutWarn() {
			return xmlErrorList.isEmpty() && xmlWarnList.isEmpty();
		}

		public List<SAXException> getXmlErrorList() {
			return xmlErrorList;
		}

		public List<SAXException> getXmlWarnList() {
			return xmlWarnList;
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			xmlErrorList.add(e);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			xmlErrorList.add(e);
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			xmlWarnList.add(e);
		}
	}
}