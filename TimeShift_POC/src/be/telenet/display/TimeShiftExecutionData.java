package be.telenet.display;

import java.util.ArrayList;
import java.util.Calendar;

import be.telenet.parse.TimeShiftParser.ApplicationFlowEnum;

public class TimeShiftExecutionData {
	
	private Calendar executionDate;
	private ApplicationFlowEnum flow;
	private String templateName;
	private String elementFieldId;
	private String xPath;
	private ArrayList<ExecutionDetails> listExecutionDetails = new ArrayList<ExecutionDetails>();
	private String displayHeader;
	
	public TimeShiftExecutionData(Calendar executionDate,
			ApplicationFlowEnum flow, String templateName,
			String elementFieldId, String xPath, String displayHeader) {	
		this.executionDate = executionDate;
		this.flow = flow;
		this.templateName = templateName;
		this.elementFieldId = elementFieldId;
		this.xPath = xPath;
		this.displayHeader = displayHeader;
	}
	
	public void addExecutionDetail(String uniqueField, Calendar epcDate,
				Calendar tsEPCDate, String dateFieldName) {
		listExecutionDetails.add(new ExecutionDetails(uniqueField, epcDate, tsEPCDate, dateFieldName));
	}
	
	public void addExectuionDetail(ExecutionDetails execData) {
		listExecutionDetails.add(execData);
	}
	
	public ArrayList<ExecutionDetails> getListExecutionDetails() {
		return this.listExecutionDetails;
	}

	public Calendar getExecutionDate() {
		return executionDate;
	}

	public ApplicationFlowEnum getFlow() {
		return flow;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getElementFieldId() {
		return elementFieldId;
	}

	public String getxPath() {
		return xPath;
	}
	
	public String getDisplayHeader(){
		return this.displayHeader;
	}
}
