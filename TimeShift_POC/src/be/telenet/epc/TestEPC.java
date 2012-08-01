package be.telenet.epc;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import be.telenet.config.ElementConfigReader;
import be.telenet.config.ProcessElement;
import be.telenet.config.TimeShiftTemplate;
import be.telenet.config.core.Field;
import be.telenet.config.core.FieldList;
import be.telenet.parse.BillingDiscountParser;
import be.telenet.parse.TimeShiftParser;
import be.telenet.parse.TimeShiftParser.ApplicationFlowEnum;
import be.telenet.utils.EPCDate;

import com.amdocs.pc.domain.external.EPCElement;



public class TestEPC {

	private static String SERVER_NAME;
    private static String SERVER_PORT;
    private static String EPC_PROJECT_NAME;
    private static String EPC_USERNAME;
    private static String EPC_PASSWORD;
    
    static 
    {

		// telenet env1		
		SERVER_NAME = "wlsepcmngdcluster.edp.corp.telenet.be";
		SERVER_PORT = "9450";
		EPC_PROJECT_NAME = "TimeShift_Prepartion2";
		EPC_USERNAME = "epcoperator";	
		EPC_PASSWORD = "epc+oper";
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ArrayList<ProcessElement> listProcessElement = ElementConfigReader.readElementConfiguration(new File("config.xml"));

		EPCClient client = new EPCClient(SERVER_NAME, SERVER_PORT, EPC_USERNAME, EPC_PASSWORD, EPC_PROJECT_NAME);
		if (client.login())
		{
			for (ProcessElement pe : listProcessElement) {				
			
				Calendar timeShiftDate = EPCDate.getEPCDateAsCalendar("2011-08-01 00:00:00");
				Calendar releaseDate = EPCDate.getEPCDateAsCalendar("2011-10-01 00:00:00");
				
				if (pe.getEPCQueryName() == null) {
					System.out.println("Invalid EPC Query : " + pe.getEPCQueryName());
					continue;
				}
							
				EPCQueries query = new EPCQueries();			
				query.executeQuery(pe.getEPCQueryName(), client);
				
				if(query.getElements() != null)
				{
					for(EPCElement element : query.getElements()) {
						String elementString = null;
						TimeShiftParser tsp = new TimeShiftParser(element.getDocumentAsString(), pe, timeShiftDate, releaseDate, ApplicationFlowEnum.DO_RECORD_DUPLICATION);
						if (tsp.process()) {
							elementString = tsp.getStringFromDocument();
							//System.out.println(elementString);
							
							EPCElements.updateElement(elementString, client, pe.getEpcTemplateName());
						}
						else {System.out.println("XML process failed");}
					}
				}
				
			}
		}// end of if client check
		System.out.println("End");
		
		if (client != null) {
			client.logout();
		}
	}

}
