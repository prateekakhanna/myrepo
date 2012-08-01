package be.telenet.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DomParser {
		
	public String getElementText(Element el, String strXMLTag)
    {
        NodeList nl = el.getElementsByTagName(strXMLTag);
        String str=null;
        if (nl != null && nl.getLength() > 0)
        {
            Element n = (Element)nl.item(0);
            NodeList nlValue = n.getElementsByTagName("Value");
            if(nlValue != null && nlValue.getLength()>0)
                str=nlValue.item(0).getFirstChild().getNodeValue();
        }
        return str;
    }    
	
	private Element convertTextToElement(String strXMLTag)
    {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Element rootNode=null;
        try {
          DocumentBuilder db = dbf.newDocumentBuilder();
          Document doc = db.parse(strXMLTag);
          
          doc.getDocumentElement().normalize();
          
          rootNode = doc.getDocumentElement(); 
        }
        catch (Exception ex)
        {
        	System.out.println(ex.getMessage());
        }        
        
        return rootNode;
    }

	public String getElementAttributeByTag(Element el, String strTagName, String strAttribute)
    {
		NodeList nl = el.getElementsByTagName(strTagName);
        String str=null;
        if (nl != null && nl.getLength() > 0)
        {
            Element n = (Element)nl.item(0);		
            return ((n != null && ! n.getAttribute(strAttribute).isEmpty()) ? n.getAttribute(strAttribute) : null);
        }
        return null;
    }
	
	public String getElementAttributeByTag(Element el, String strTagName) {
		return getElementAttributeByTag(el, strTagName, "value");
	}
	
    public String getElementAttribute(Element el, String strAttribute)
    {
        return el != null ? el.getAttribute(strAttribute) : null;
    }
    
    public void addElementAttribute(Document doc, Element el, String attributeName, String attributeValue)
    {
    	if (doc != null)
    	{
    		if(el.getAttribute(attributeName) == null) 	{
    			Attr attribute = doc.createAttribute(attributeName);
    			attribute.setValue(attributeValue);
    			el.setAttributeNode(attribute);
    		}
    		else{
    			
    		}
    	}
    }
    
    public Document parse(String xmlString){
    	Document doc = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	try {
	    	DocumentBuilder db = dbf.newDocumentBuilder();
	    	InputSource inStream = new InputSource();
	    	inStream.setCharacterStream(new StringReader(xmlString));
	    	doc = db.parse(inStream);
    	}
    	catch (Exception e){
        	e.printStackTrace();
        }
    	return doc;
    }
    
    
    public Document parse(File f)
	{		
		Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder db = dbf.newDocumentBuilder();
          doc = db.parse(f);
          doc.getDocumentElement().normalize();
          //print the "type" attribute
          System.out.println("Root element " + doc.getDocumentElement().getNodeName());
          Element rootNode = doc.getDocumentElement();
        }
        catch (Exception e)
        {
        	//TODO
        }
        return doc;
	}
    
    protected String serialXMLString(Document doc) {
    	if (doc == null)
    		return null;
    	
    	OutputFormat xmlOutputFormat = new OutputFormat(doc); 
    	xmlOutputFormat.setIndenting(true);
    	    	
        StringWriter stringOut = new StringWriter();    
        XMLSerializer serial   = new XMLSerializer (stringOut, xmlOutputFormat);
        try {
			serial.serialize(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return stringOut.toString();
    }
    
    protected void serialXMLFile(String filename, Document doc)
    {
    	if (doc == null || filename == null)
    		return;
    		
    	// Creating XML output format
    	OutputFormat xmlOutputFormat = new OutputFormat(doc);
    	xmlOutputFormat.setIndenting(true);

		// Creating XML output format for xml file
		XMLSerializer fileSerializer=null;
		try {
			fileSerializer = new XMLSerializer(
					new FileOutputStream(new File(filename)),
					xmlOutputFormat);
			
			// Serializing the format
			fileSerializer.serialize(doc);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean setAttributeValue(Element e, String tagName, String attributeValue, boolean clearExistingAttributes) {
    	boolean isSuccess = false;
    	
    	if (e != null) {
			NodeList nl = e.getElementsByTagName(tagName);
			if (nl != null && nl.getLength() == 1) {
				Element ts = (Element) nl.item(0);
				if (clearExistingAttributes) {
					clearAllAttributes(ts);
				}
				ts.setAttribute("value", attributeValue);
				isSuccess = true;
			}
			else {
				System.out.println("clearAndSetAttributeValue not able to find the tag: " + tagName + " in element: " + e.getNodeName() +" Please check the data.");
			}
		}
    	
    	return isSuccess;
    }
    
    private void clearAllAttributes(Element e){    	
		if (e != null) {
			// Remove all the attributes of an element
			NamedNodeMap attrs = e.getAttributes();
			String[] names = new String[attrs.getLength()];
			for (int i=0; i<names.length; i++) {
			    names[i] = attrs.item(i).getNodeName();
			}
			for (int i=0; i<names.length; i++) {
			    attrs.removeNamedItem(names[i]);
			}
		}    	
    }
}
