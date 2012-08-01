package be.telenet.config;

import be.telenet.config.core.Field;
import be.telenet.config.core.FieldList;

public class TimeShiftTemplate
{
	private Field targetField;
	private FieldList uniqueFields;
	private boolean continousShiftLogic;
	
	public TimeShiftTemplate(Field field, FieldList uniqueFields, boolean continousShiftLogic) {
		this.targetField = field;
		this.uniqueFields = uniqueFields;
		this.continousShiftLogic=continousShiftLogic;
	}
	
	public TimeShiftTemplate(String name, FieldList uniqueFields, boolean continousShiftLogic) {
		this(new Field(name), uniqueFields, continousShiftLogic);
	}
	/*
	public TimeShiftTemplate(String name) {
		this(name, new FieldList(), false);
	}*/
	
	public int getLevel() {
		return this.targetField.getLevel();
	}
	public String getName() {
		return this.targetField.getName();
	}
	public String getXPath() {
		return this.targetField.getXPath();
	}
	
	public void setField(String name, String xString) {
		this.targetField = new Field(name);
	}
	public FieldList getUniqueFields() {
		return this.uniqueFields;
	}
	public void setUniqueFields(FieldList uniqueFields) {
		this.uniqueFields = uniqueFields;
	}
	public boolean isContinousShiftLogic(){
		return continousShiftLogic;
	}
}
