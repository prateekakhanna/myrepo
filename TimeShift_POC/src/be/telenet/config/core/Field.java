package be.telenet.config.core;

import java.util.ArrayList;

import be.telenet.config.TimeShiftConstants;
import be.telenet.config.TimeShiftTemplate;
import be.telenet.utils.StringUtils;



public class Field
{	
	private String name;
	private String xPath;
	private ArrayList<String> values;
	private boolean isActive;
	private Integer level;
		
	public Field(String name) {
		this(name, new ArrayList<String>());
	}
	
	public Field(String name, ArrayList<String> values) {
		this(name, name, values);
	}
	
	public Field(String name, String xPath, ArrayList<String> values){
		this.name = name;
		this.values = values;
		this.xPath = xPath;
		this.isActive=true;
		calculateLevel();
	}
	
	public void add(String value) {
		if(values != null) {
			this.values.add(value);
		}
	}

	public int getLevel() {
		return level.intValue();
	}
	
	public String getName() {
		return name;
	}

	public String getXPath() {
		return xPath;
	}
	
	public String setXPath() {
		return xPath;
	}
	
	public ArrayList<String> getValue() {
		return this.values;
	}
	
	private void calculateLevel() {		
		if (this.xPath != null) {
			this.level = new Integer(StringUtils.countOccurenceInString(this.xPath,TimeShiftConstants.XPATH_SEPARATOR));
		}
	}
	
	public boolean equals(Object val){		
		if (val == null) {
			return false;
		}
		else if (val instanceof Field)
		{
			return this.name.equals(((Field)val).getName());
		}
		return false;
	}
	
	public int hashCode(){
		return name.hashCode();
	}
}
