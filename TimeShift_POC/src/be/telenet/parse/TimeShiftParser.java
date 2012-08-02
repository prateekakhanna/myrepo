package be.telenet.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import weblogic.wtc.jatmi.tsession;

import be.telenet.config.ProcessElement;
import be.telenet.config.TimeShiftConstants;
import be.telenet.display.ExecutionDetails;
import be.telenet.display.TimeShiftExecutionData;
import be.telenet.utils.EPCDate;


public class TimeShiftParser extends DomParser{
	
	public static final String NA = "0";
	public static final String READY = "1";
	public static final String SHIFTED = "2";
	public static final String COMPLETED = "3";
		
	public enum ApplicationFlowEnum {
		DO_TIMESHIFT,
		DO_RECORD_DUPLICATION
	};
	 
	
	private static final String VALIDITY_PERIOD_TEMPLATE_ID = "f54c4363-e87b-4241-8b86-f3db47bfe2f0";
	private static final String TIMESHIFT_FIELDNAME = "TimeShift";
	
	private boolean isSuccessfullyProcess=true;
	private Document doc;
	private ProcessElement pElement;
	private Calendar timeShiftDate;
	private Calendar releaseDate;
	private ApplicationFlowEnum flow;
	private ArrayList<TimeShiftExecutionData> listExecutionData = new ArrayList<TimeShiftExecutionData>();
		
	public ArrayList<TimeShiftExecutionData> getListExecutionData() {
		return listExecutionData;
	}

	public TimeShiftParser(String xmlString, ProcessElement pElement, Calendar tsd, Calendar rd, ApplicationFlowEnum flow) {
		super();
		doc = parse(xmlString);
		this.pElement = pElement; 
		this.timeShiftDate = tsd;
		this.releaseDate = rd;
		this.flow = flow;
	}
	
	public TimeShiftParser(File f, ProcessElement pElement, Calendar tsd, Calendar rd, ApplicationFlowEnum flow) {
		super();
		doc = parse(f);
		this.pElement = pElement; 
		this.timeShiftDate = tsd;
		this.releaseDate = rd;
		this.flow = flow;
	}
	
	public String getStringFromDocument() {
		String xmlString = null;
		if (doc != null) {
			xmlString = serialXMLString(doc);
		}
		return xmlString;
	}
	
	public boolean process()
	{
		
		if (doc == null || pElement == null)
			return false;
		
		Element rootElement = doc.getDocumentElement();
		NodeList nl = rootElement.getElementsByTagName(pElement.getIdFieldName());
		for(int idx=0; idx < nl.getLength(); idx++)
		{
			//Assuming, this ID would be at level 1, so does not run DF execution.
			Element elementId = (Element) nl.item(idx);
			String strElementId = getElementAttribute(elementId, "value");
			TimeShiftExecutionData tsExecData = new TimeShiftExecutionData(Calendar.getInstance(), this.flow, pElement.getEpcTemplateName(), strElementId, pElement.getTimeShiftTemplate().getXPath(), pElement.getDisplayHeader());
			if (isIdInList(strElementId)) {
				Node versionNode = elementId.getParentNode();
				if (versionNode != null && versionNode.getNodeType() == Node.ELEMENT_NODE) 	{
					Element parentElement = (Element) versionNode;
					NodeList timeShiftTemplateNodeList = null;
					
					String xPath = pElement.getTimeShiftTemplate().getXPath();
					int level = pElement.getTimeShiftTemplate().getLevel();
					System.out.println("\nTime Shift called for " + pElement.getEpcTemplateName() + " id: " + strElementId +".");
					if (level == 0) {
						timeShiftTemplateNodeList = parentElement.getElementsByTagName(xPath);
						processTimeShift(timeShiftTemplateNodeList, tsExecData);
					}
					else {
						getNodeListForTagName_Recur(parentElement.getChildNodes(), xPath, level, tsExecData);
					}															
				}
			}//if (isIdInList(elementId))
			// add exec data in the TimeShiftExecutionDetails list.
			listExecutionData.add(tsExecData);
		}// end of for loop.
				
		serialXMLFile("test.xml" , doc);
		return isSuccessfullyProcess;
	}// end of process method
	
	private void processTimeShift(NodeList timeShiftTemplateNodeList, TimeShiftExecutionData tsExecData)	{
		
		if (timeShiftTemplateNodeList == null || timeShiftTemplateNodeList.getLength() == 0) {
			System.out.println("Configuration issue " + pElement.getTimeShiftTemplate().getXPath());
			isSuccessfullyProcess=false;
			return;
		}
		
		HashMap<String, Element> mapAllContainedElements = new HashMap<String, Element>(timeShiftTemplateNodeList.getLength());
			
		Element parentTimeShiftTemplate = (Element) timeShiftTemplateNodeList.item(0).getParentNode();		
		ArrayList<Element> elementTimeShiftList = initForTimeShift(timeShiftTemplateNodeList, mapAllContainedElements);
									
		//Find and Duplicate the Element.
		if (! elementTimeShiftList.isEmpty()) {
			if (flow == ApplicationFlowEnum.DO_TIMESHIFT && !mapAllContainedElements.isEmpty()){
				timeShiftWithoutDuplication(mapAllContainedElements, elementTimeShiftList, tsExecData);
			}
			else if (flow == ApplicationFlowEnum.DO_RECORD_DUPLICATION){	
				parentTimeShiftTemplate = timeShiftUsingDuplication(mapAllContainedElements, parentTimeShiftTemplate, elementTimeShiftList, tsExecData);			
			}
		} else {
			System.out.println("elementTimeShiftList cannot be empty after initialization. Data is not correctly configured in EPC.");
			isSuccessfullyProcess=false;
		}
		
	}
	
	private ArrayList<Element> initForTimeShift(NodeList timeShiftTemplateNodeList, HashMap<String, Element> mapAllRatePlan) {
		ArrayList<Element> elementTimeShiftList = new ArrayList<Element>();
		for (int tsIdx=0; tsIdx < timeShiftTemplateNodeList.getLength(); tsIdx++) {
			Element element = (Element) timeShiftTemplateNodeList.item(tsIdx);
			if (isTimeShift(element)) 	{
				//Assuming only 1 time shift element
				/*
				if (pElement.getTimeShiftTemplate().isDuplicateLogic()) {
					mapTimeShift.put(getTimeShiftElementUniqueString(ratePlan), ratePlan);
				}*/
				elementTimeShiftList.add(element);
			}
			else {
				//Date append to the unique string is required only in case duplicate contained record is required.				
				mapAllRatePlan.put(getUniqueString(element, false, true), element);
			}
		}//end of timeshift for loop.
		return elementTimeShiftList;
	}
	
	private void getNodeListForTagName_Recur(NodeList nodeList, String xpath, int level, TimeShiftExecutionData execData) {		
		
		if (level == 0) {
			if (nodeList != null) {
				processTimeShift(nodeList, execData);
			}
			return;
		}
		
		String xmlTag = xpath.substring(0, xpath.indexOf(TimeShiftConstants.XPATH_SEPARATOR));
		for (int i=0; i<nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(xmlTag)) {											
				Element e = (Element)n;
				xmlTag = xpath.substring(xmlTag.length()+1);
				getNodeListForTagName_Recur(e.getElementsByTagName(xmlTag), xmlTag, --level, execData);
			}
		}
	}
			
	private void timeShiftWithoutDuplication(HashMap<String, Element> map, ArrayList<Element> eList, TimeShiftExecutionData tsExecData)
	{
		if (eList != null) {
			for (Element e : eList) {
			//Element e = eList.get(0);
			
				if (! READY.equals(getElementAttributeByTag(e, TIMESHIFT_FIELDNAME))){
					System.out.println("Method timeShiftWithoutDuplication called for " + getTimeShiftElementUniqueString(e, true) + " where TimeShiftStatus = " + getElementAttributeByTag(e, TIMESHIFT_FIELDNAME));
					isSuccessfullyProcess=false;
					return;
				}						
				
				//Get unique key using release date.
				String tsRecordString = getTimeShiftElementUniqueString(e, true);
				Element oldEle = map.get(tsRecordString);
				
				if (oldEle == null){
					System.out.println("The relevant time shift record not found.");
					return;
				}
													
					//set start date = timeShiftDate.			
					tsExecData.addExectuionDetail(setValidityStartDate(e, timeShiftDate));
					//set end date = timeShiftDate - 1
					tsExecData.addExectuionDetail(setValidityEndDate(oldEle, pElement.getTimeShiftTemplate().isContinousShiftLogic() ? timeShiftDate : getPerviousCalendarDate(timeShiftDate)));
					
					//set timeshift status = shifted.
					setTimeShiftStatus(e, SHIFTED);
					setTimeShiftStatus(oldEle, SHIFTED);
					
					//Adding back to map
					//map.put(tsRecordString, oldEle);
				}//end of for
			} 
	}// end of method
	
	private Element timeShiftUsingDuplication(HashMap<String, Element> map, Element parent, ArrayList<Element> elementTimeShiftList, TimeShiftExecutionData tsExecData)
	{
		//Create temp map
		HashMap<String, Element> tsMap = new HashMap<String, Element>(elementTimeShiftList.size());
		for(Element tempE : elementTimeShiftList) {
			tsMap.put(getUniqueString(tempE, false, true), tempE);
		}
		
		for (Element e : elementTimeShiftList) {
			
			if (SHIFTED.equals(getElementAttributeByTag(e, TIMESHIFT_FIELDNAME))) {
				if (getValidityEndDate(e) == null) {
					//Get unique key using TACT test date.
					String tsRecordString = getTimeShiftElementUniqueString(e, false);
					Element oldEle = tsMap.get(tsRecordString);
							
					if (oldEle == null){
						System.out.println("The relevant time shift record not found.");
						continue;
					}
											
					Element dupNewE = (Element) e.cloneNode(true);
					//set startDate = timeshift time.
					tsExecData.addExectuionDetail(setValidityStartDate(dupNewE, releaseDate));
					setTimeShiftStatus(dupNewE, COMPLETED);
					
					
					Element dupOldEle = (Element) oldEle.cloneNode(true);
					tsExecData.addExectuionDetail(setValidityStartDate(dupOldEle, getValidityStartDate(e)));
					//set end date on time shifted record, based on logic of continous end, start dates.
					tsExecData.addExectuionDetail(setValidityEndDate(dupOldEle, pElement.getTimeShiftTemplate().isContinousShiftLogic() ? releaseDate : getPerviousCalendarDate(releaseDate)));
					setTimeShiftStatus(dupOldEle, COMPLETED);
					
					//update timeshift status in oldEle
					setTimeShiftStatus(oldEle, COMPLETED);
					
					parent.replaceChild(dupOldEle, e);
					parent.appendChild(dupNewE);
				}
			}
			else{
				System.out.println("Method timeShiftUsingDuplication called for " + getTimeShiftElementUniqueString(e, true) + " where TimeShiftStatus = " + getElementAttributeByTag(e, TIMESHIFT_FIELDNAME));
				isSuccessfullyProcess = false;
			}
		}
		
		return parent;
	}
	
	private ExecutionDetails setValidityStartDate(Element e, Calendar date){		
		return setValidityDate(e, date, "StartDate");
	}
	
	private ExecutionDetails setValidityEndDate(Element e, Calendar date){
		return setValidityDate(e, date, "EndDate");
	}
	
	private ExecutionDetails setValidityDate (Element e, Calendar date, String xmlTagName){
		NodeList validityPeriodList = (NodeList) e.getElementsByTagName("ValidityPeriod");
		Calendar previousDate = null;
		for(int i=0; i<validityPeriodList.getLength(); i++)
		{
			Element validityPeriod = (Element) validityPeriodList.item(i);
			if (isValidityPeriod(validityPeriod, e)) {
				
				NodeList nl = validityPeriod.getElementsByTagName(xmlTagName);
		        String str=null;
		        if (nl != null && nl.getLength() > 0)
		        {
		            Element edate = (Element)nl.item(0);				
					if (edate != null) {
						System.out.println("Updated the Validity." + xmlTagName + " from: " + getElementAttribute(edate, "value")
								+ "\tto: " + EPCDate.getCalendarAsEPCDateStr(date));
						previousDate = EPCDate.getEPCDateAsCalendar(getElementAttribute(edate, "value"));
						edate.setAttribute("value", EPCDate.getCalendarAsEPCDateStr(date));
					}
					else {
						System.out.println("End date value not assigned");
					}
		        }
			}
		}
		
		//Prepare display element details.
		String uniqueFieldId = null;
		NodeList displayElementNL = e.getElementsByTagName(pElement.getTimeShiftTemplate().getDisplayField());
		if (displayElementNL != null && displayElementNL.getLength() == 1) {
			Element displayElement = (Element) displayElementNL.item(0);
			uniqueFieldId = displayElement.getAttribute("value");
		}
		
		ExecutionDetails ed = new ExecutionDetails(uniqueFieldId, previousDate, date, xmlTagName);
		return ed;
	}
	
	private Calendar getValidityStartDate(Element e)
	{
		return getValidityDate(e, "StartDate");
	}
	
	private Calendar getValidityEndDate(Element e)
	{
		return getValidityDate(e, "EndDate");
	}
	
	private Calendar getValidityDate(Element e, String xmlTagName) {
		NodeList validityPeriodList = (NodeList) e.getElementsByTagName("ValidityPeriod");
		for(int i=0; i<validityPeriodList.getLength(); i++)
		{
			Element validityPeriod = (Element) validityPeriodList.item(i);
			if (isValidityPeriod(validityPeriod, e)) {				
				NodeList nl = validityPeriod.getElementsByTagName(xmlTagName);
		        String str=null;
		        if (nl != null && nl.getLength() > 0)
		        {
		            Element edate = (Element)nl.item(0);				
					if (edate != null) {						
						return EPCDate.getEPCDateAsCalendar(getElementAttribute(edate, "value"));
					}					
		        }
			}
		}
		return null;
	}
	
	private void setTimeShiftStatus(Element e, String value) {
		if (e != null && value != null) {
			setAttributeValue(e, TIMESHIFT_FIELDNAME, value, true);
		}
	}
	
	private String getTimeShiftElementUniqueString(Element timeShiftElement, boolean useReleaseDateForTimeShiftKey)
	{
		String uniqueString = getUniqueString(timeShiftElement, false, false);
		//Add start date
		uniqueString += useReleaseDateForTimeShiftKey ? pElement.getTimeShiftTemplate().isContinousShiftLogic() ? EPCDate.getCalendarAsEPCDateStr(releaseDate) :  EPCDate.getCalendarAsEPCDateStr(getPerviousCalendarDate(releaseDate))
					: pElement.getTimeShiftTemplate().isContinousShiftLogic() ? EPCDate.getCalendarAsEPCDateStr(timeShiftDate) : EPCDate.getCalendarAsEPCDateStr(getPerviousCalendarDate(timeShiftDate));
		uniqueString += "~";
		//End date would be always null, as we need the active record.
		System.out.println("TimeShift : " + uniqueString);
		return uniqueString;
	}
	
	private String getUniqueString(Element timeShiftElement, boolean withStartDate, boolean withEndDate)
	{
		String uniqueString = "";
		
		for (String name : pElement.getTimeShiftTemplate().getUniqueFields().getFieldListNames())
		{
			if (name != null)
			{
				String tmp = getElementAttributeByTag(timeShiftElement, name);
				uniqueString += (tmp != null ? tmp : "null") + "~";
			}
		}
		
		
		NodeList validityPeriodList = timeShiftElement.getElementsByTagName("ValidityPeriod");
		//System.out.println("ValidityPeriod = " + validityPeriodList.getLength());
		for (int i=0; i<validityPeriodList.getLength(); i++)
		{
			Element validityPeriod = (Element) validityPeriodList.item(i);
		
			if (isValidityPeriod(validityPeriod, timeShiftElement)) {				
				if (withStartDate) {
					String tmpStart = getElementAttributeByTag(timeShiftElement, "StartDate");
					uniqueString += (tmpStart != null && withStartDate ? tmpStart : "null") + "~";
				}
				
				if (withEndDate) {
					String tmpEnd = getElementAttributeByTag(timeShiftElement, "EndDate");
					uniqueString += (tmpEnd != null && withEndDate ? tmpEnd : "null") + "~";
				}
			}
		}
		
		System.out.println(uniqueString);
		return uniqueString;
	}
	
	private boolean isTimeShift(Element e) 	{
		String ts = getElementAttributeByTag(e, "TimeShift");
		if (flow == ApplicationFlowEnum.DO_TIMESHIFT && READY.equals(ts)) {
			return true;
		}
		else if (flow == ApplicationFlowEnum.DO_RECORD_DUPLICATION && SHIFTED.equals(ts)) {
			return true;
		}
		return false;
	}
	
	private boolean isIdInList(String ID){	
		/*if (ID != null && pElement != null && pElement.getIdField().getValue() != null 
				&& pElement.getIdField().getValue().contains(ID))		
			return true;
		else
			return false;*/
		return true;
	}
	private Calendar getPerviousCalendarDate(Calendar c){
		Calendar tmpReleaseDate = (Calendar) c.clone();
		tmpReleaseDate.add(Calendar.DAY_OF_MONTH, -1);
		System.out.println(tmpReleaseDate.getTime());
		return tmpReleaseDate;
	}
	private boolean isValidityPeriod(Element validityPeriod, Element e) {
		return (VALIDITY_PERIOD_TEMPLATE_ID.equals(getElementAttribute(validityPeriod, "template_id"))
		&& e.getNodeName().equals(validityPeriod.getParentNode().getParentNode().getNodeName()));
	}
	
}
