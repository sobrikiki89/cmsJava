package app.core.service;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import app.core.service.impl.XMLDocumentServiceImpl.XMLErrorHandler;

public interface XMLDocumentService {
	public Document createXML();

	public Node createField(Document xml, String name, String value);

	public String toString(Document xml);

	public XMLErrorHandler validateXMLSchema(String xmlString, String schemaClassPath);

	public Document toXML(String xmlString);

	public String getField(Document xml, String fieldName);
	
	public List<String[]> getListField(Document xml, String parentFieldName, String[] fieldNameArray);
}
