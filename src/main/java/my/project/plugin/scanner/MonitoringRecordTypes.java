package my.project.plugin.scanner;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MonitoringRecordTypes {
	Document document;
	
	public String getPropertyName(String traceType, Integer index) {
		NodeList nl = document.getElementsByTagName(traceType);
		NodeList propertyList;
		//only one node -> everything corret; 2 nodes = duplicates; 0 nodes = missing MonitoringrecordType
		try {
		if (nl.getLength() == 1) {
			propertyList = nl.item(0).getChildNodes();	
			return propertyList.item(index).getNodeName();
		}else {
			return "N/A";
		}
		}catch(Exception e) {
			return "N/A";
		}
	}
	
	public void ReadRecordXML(){
		//Read XML file with MonitoringRecordTypes from jqassistant-commandline-neo4jv.../bin
		String path = System.getProperty("user.dir");
		path = path.replace((char) 92, '/');
		File file = new File(path + "/MonitoringRecordTypes.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.parse(file);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}