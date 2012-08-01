package be.telenet.config.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FieldList {
	
	HashMap<Field, ArrayList<Field>> map;
	
	public FieldList() {
		map = new HashMap<Field, ArrayList<Field>>();
	}
	
	private ArrayList<Field> getFromMap(Field f) {
		return map.get(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getFieldListNames()
	{
		List<String> listNames = new  ArrayList<String>();
		List listField = (List) Arrays.asList(map.keySet().toArray());
		
		for (Object field : listField)	{
			if (field instanceof Field)	{
				listNames.add(((Field) field).getName());
			}
		}
		return listNames;
	}
	
	public ArrayList<String> getValueListByName(String name)
	{
		ArrayList<String> stringList = new ArrayList<String>();
		ArrayList<Field> fieldList = map.get(new Field(name));
		
		for(Field f : fieldList) {
			stringList.add(f.getName());
		}
		
		return stringList;
	}
	
	/*
	public boolean checkNameExist(String name){
		return map.containsKey(name);
	}*/
	
	public boolean checkValueExist(String name, String value)
	{
		if (map.containsKey(name))
		{
			return map.get(name).contains(value);
		}
		return false;
	}
	
	public void add(String name, ArrayList<Field> values) {
		if (map != null) {
			Field f = new Field(name);
			if (! map.containsKey(f)) {
				map.put(f, values);
			}
			else {
				System.out.println("The key " + name + " already present in FieldList");
			}
		}
	}
	
	public void add(String name, String value, boolean isContained) {
		if (map != null) {
			Field f = new Field(name);
			Field v = new Field(value);
			if (! map.containsKey(f))
			{
				ArrayList<Field> list = new ArrayList<Field>();
				list.add(v);
				map.put(f, list);
			}
			else {
				map.get(f).add(v); 
			}
		}			
	} //end of add
	/*
	public static void main(String [] agrs)
	{
		FieldList fl = new FieldList();
		fl.add("a", "123");
		fl.add("a", "456");
		System.out.println("end");
	}*/
}
