package be.telenet.epc;

import com.amdocs.pc.domain.external.EPCElement;
import com.amdocs.pc.exceptions.EPCExternalServiceException;

public class EPCElements {
	
	public static boolean  updateElement(String xml, EPCClient client, String templatename) {
		
		if (xml != null && client != null && client.getElementServices() != null
				&& client.getContext() != null) {
			
			xml = updateXMLString(xml, templatename);
			
			EPCElement element = new EPCElement();
			element.setDocumentAsString(xml);
			
			try {
				client.getElementServices().updateElement(client.getContext(), element);
			} catch (EPCExternalServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
		} else 	{
			System.out.println("EPCElements client initialization not correctly done.");
			return false;
		}		
		return true;
	}
	
	private static String updateXMLString(String xml, String templateName) {
		xml = xml.replace("</"+ templateName +"List>", "");
		xml = xml.substring(xml.indexOf("</Id>") + 5);
		
		return xml;
	}

}
