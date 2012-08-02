package be.telenet.display;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeShiftExecutionDetailsWriter {
	
	private static final String SEPARATOR = ",";
	
	public static void writeToTextFile(ArrayList<TimeShiftExecutionData> tsEDList) {
		
		try {
			for (TimeShiftExecutionData tsEd : tsEDList) {
				boolean writeHeader = false;
				File file = new File(tsEd.getTemplateName()+".csv");
				if (! file.exists()) {
					file.createNewFile();
					writeHeader = true;
				}
				
				FileWriter fw = null;
				fw = new FileWriter(file, true);
				
				if(writeHeader) {
					fw.write(tsEd.getDisplayHeader() + "\n");
				}
				
				for(int idx=0; idx<tsEd.getListExecutionDetails().size(); idx++) {
					fw.write(getLineToWrite(tsEd, idx));				
				}
				
				if (fw != null) {
					fw.flush();
					fw.close();
				}
			}
		} catch (IOException e) {
			System.out.println("Error in method writeToTextFile " + e.getMessage());
			e.printStackTrace();
		};
	} // end writeToTxtFile
	
	private static String getLineToWrite(TimeShiftExecutionData tsEd, int seqno) {	
		String line = "";
		
		if (seqno < tsEd.getListExecutionDetails().size()) {
			ExecutionDetails ed = tsEd.getListExecutionDetails().get(seqno);
				
			line = tsEd.getTemplateName() + SEPARATOR;
			line +=	tsEd.getElementFieldId() + SEPARATOR; 
			line +=	tsEd.getxPath() + SEPARATOR;
			line +=	tsEd.getFlow() + SEPARATOR;
			line +=	(tsEd.getExecutionDate() != null ? tsEd.getExecutionDate().getTime().toString() : "") + SEPARATOR;
			if (ed != null) {
				line += ed.getUniqueField() + SEPARATOR;
				line +=	ed.getDateFieldName() + SEPARATOR;
				line +=	getCalenderInStringFormat(ed.getEpcDate()) + SEPARATOR;
				line +=	getCalenderInStringFormat(ed.getTsEPCDate());
			}
			line += "\n";	
		}
		return line;
	}
	
	public static String getCalenderInStringFormat(Calendar calendardate) {
		String strdate = "";

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		if (calendardate != null) {
		strdate = sdf.format(calendardate.getTime());
		}
		
		return strdate;
	}
	
}
