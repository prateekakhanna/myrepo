package be.telenet.display;

import java.util.Calendar;

public class ExecutionDetails {
		
	private String uniqueField;
	private Calendar epcDate;
	private Calendar tsEPCDate;
	private String dateFieldName;
	
	public ExecutionDetails(String uniqueField, Calendar epcDate, Calendar tsEPCDate, String dateFieldName) {		
		this.uniqueField = uniqueField;
		this.epcDate = epcDate;
		this.tsEPCDate = tsEPCDate;
		this.dateFieldName = dateFieldName;
	}

	public String getUniqueField() {
		return uniqueField;
	}

	public Calendar getEpcDate() {
		return epcDate;
	}

	public Calendar getTsEPCDate() {
		return tsEPCDate;
	}

	public String getDateFieldName() {
		return dateFieldName;
	}
	
	
}