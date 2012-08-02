package be.telenet.config;

import java.util.ArrayList;

import be.telenet.config.core.Field;
import be.telenet.config.core.FieldList;


public class ProcessElement 
{
	private String epcTemplateName;
	private String epcQueryName;
	private Field idField;
	private boolean isActive;
	private TimeShiftTemplate timeShiftTemplate;
	private String displayHeader;
	
	public String getDisplayHeader() {
		return displayHeader;
	}
	public void setDisplayHeader(String displayHeader) {
		this.displayHeader = displayHeader;
	}
	public String getEpcTemplateName() {
		return epcTemplateName;
	}
	public void setEpcTemplateName(String epcTemplateName) {
		this.epcTemplateName = epcTemplateName;
	}
	public String getIdFieldName() {
		return this.idField.getName();
	}
	public Field getIdField() {
		return this.idField;
	}
	public void setIdFieldName(String name) {
		this.idField = new Field(name);
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public TimeShiftTemplate getTimeShiftTemplate() {
		return timeShiftTemplate;
	}
	public void setTimeShiftTemplate(TimeShiftTemplate timeShiftTemplate) {
		this.timeShiftTemplate = timeShiftTemplate;
	}
	public void setEPCQueryName(String queryName) {
		this.epcQueryName = queryName;
	}
	public String getEPCQueryName() {
		return this.epcQueryName;
	}
	
	public ProcessElement(String epcTemplateName, String idFieldName,
			boolean isActive, TimeShiftTemplate timeShiftTemplate) {
		this.epcTemplateName = epcTemplateName;
		this.idField = new Field(idFieldName);
		this.isActive = isActive;
		this.timeShiftTemplate = timeShiftTemplate;
	}
	
	public ProcessElement(String epcTemplateName, Field idField,
			boolean isActive, String displayHeader, TimeShiftTemplate timeShiftTemplate) {
		this.epcTemplateName = epcTemplateName;
		this.idField = idField;
		this.isActive = isActive;
		this.timeShiftTemplate = timeShiftTemplate;
		this.displayHeader = displayHeader;
	}
	
	public ProcessElement()	{		
		
		
		//this("PromotionSpecification", "PromotionSpecificationId", true);
		//this.idField.add("570");
		//this.timeShiftTemplate = createTimeShiftElement2(); 
	}
}
