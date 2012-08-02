package be.telenet.config;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import be.telenet.config.core.Field;
import be.telenet.config.core.FieldList;
import be.telenet.parse.DomParser;



public class ElementConfigReader {
			
	public static ArrayList<ProcessElement> readElementConfiguration(File f) {
		DomParser dom = new DomParser();
		Document doc = dom.parse(f);
		Element root = doc.getDocumentElement();
		ArrayList<ProcessElement> listProcessElements = new ArrayList<ProcessElement>();
		
		if (root.getNodeName().equals("TemplateList")) {
			NodeList templateList = root.getElementsByTagName("Template");
			for (int iTL=0; iTL <  templateList.getLength(); iTL++) {						
				Element template = (Element) templateList.item(iTL);
				String templateName = template.getAttribute("name");
				boolean templateActive = template.getAttribute("active").equals("true") ? true : false;
				
				if (! templateActive) {
					System.out.println("Template " + templateName + " is inactive in the configuration file.");
					continue;
				}
				
				String epcQueryName = template.getAttribute("epcqueryname");
				String displayHeader = template.getAttribute("displayheader");
				
				//getIdField
				Field fieldId = new Field(dom.getElementAttributeByTag(template, "IdField", "name"));
				
				//Get TimeShift Template Details.
				Element timeShift = (Element) template.getElementsByTagName("TimeShiftTemplate").item(0);
				String tsName = timeShift.getAttribute("name");
				String tsXPath = timeShift.getAttribute("xpath");
				String tsDisplayField = timeShift.getAttribute("displayfield");
				boolean isContinous = timeShift.getAttribute("continousLogic").equals("true") ? true : false;
				Field tsField = new Field(tsName, tsXPath, null);
				
				FieldList fl = new FieldList();
				NodeList uniqueFieldList = timeShift.getElementsByTagName("Field");
				for(int iUFL = 0; iUFL < uniqueFieldList.getLength(); iUFL++) {
					Element field = (Element) uniqueFieldList.item(iUFL);
					fl.add(field.getAttribute("name"), null);
				}
				
				ProcessElement pe = new ProcessElement(templateName, fieldId, templateActive, displayHeader,
						new TimeShiftTemplate(tsField, fl, isContinous, tsDisplayField));
				pe.setEPCQueryName(epcQueryName);
				listProcessElements.add(pe);
			}// end of TemplateList for loop
		}//end of TemplateList check if condition
		
		return listProcessElements;
	}//end of method readElementConfiguration
}
