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
			boolean isActive, TimeShiftTemplate timeShiftTemplate) {
		this.epcTemplateName = epcTemplateName;
		this.idField = idField;
		this.isActive = isActive;
		this.timeShiftTemplate = timeShiftTemplate;
	}
	
	public ProcessElement()	{		
		
		
		//this("PromotionSpecification", "PromotionSpecificationId", true);
		//this.idField.add("570");
		//this.timeShiftTemplate = createTimeShiftElement2(); 
	}
	/*
	private TimeShiftTemplate createTimeShiftElement2()
	{
		FieldList fl = new FieldList();
		fl.add("ConditionId", "6", true);
		
		TimeShiftTemplate tst = new TimeShiftTemplate("PromotionEligibilityRule", false, fl, 1);
		
		return tst;
	}
	
	private TimeShiftTemplate createTimeShiftElement()
	{		
		FieldList fl = new FieldList();
		fl.add("ApplicableForRatePlan", new ArrayList<Field>());
				
		TimeShiftTemplate tst = new TimeShiftTemplate("BillingRate", true, fl, 1);
		
		return tst;
	}*/
}
