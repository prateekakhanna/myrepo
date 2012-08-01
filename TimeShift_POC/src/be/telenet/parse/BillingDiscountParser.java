package be.telenet.parse;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import be.telenet.config.ProcessElement;
import be.telenet.config.TimeShiftTemplate;
import be.telenet.config.core.Field;
import be.telenet.config.core.FieldList;
import be.telenet.utils.EPCDate;



public class BillingDiscountParser extends TimeShiftParser {
	
	public static final String EPCTEMPLATENAME="BillingDiscount";
		
	public BillingDiscountParser(String filename, ProcessElement p, Calendar tsDate, Calendar releaseDate, ApplicationFlowEnum flow) {
		super(filename, p, tsDate, releaseDate, flow);				
	}
	
	public BillingDiscountParser(File f, ProcessElement p, Calendar tsDate, Calendar releaseDate) {
		super(f, p, tsDate, releaseDate, ApplicationFlowEnum.DO_RECORD_DUPLICATION);				
	}
		
	public static void main(String[] args)
	{
		//String n = "PS/Elements/PromotionSpecification/PromotionSpecification.xml";
		String n = "BD/Elements/BillingDiscount/BillingDiscount.xml";
			
		TimeShiftTemplate tst = createTimeShiftElement();
		
		ArrayList<String> BDid = new ArrayList<String>();
		BDid.add("31612");		
		Field fieldBDid = new Field("DiscountId", BDid);
		ProcessElement pElement = new ProcessElement("BillingDiscount", fieldBDid, true, tst);
		
		Calendar timeShiftDate = EPCDate.getEPCDateAsCalendar("2011-08-01 00:00:00");
		Calendar releaseDate = EPCDate.getEPCDateAsCalendar("2011-10-01 00:00:00");
		
		BillingDiscountParser bdp = new BillingDiscountParser(new File(n), pElement, timeShiftDate, releaseDate);
		bdp.process();
	}
	
	private static TimeShiftTemplate createTimeShiftElement()
	{		
		FieldList fl = new FieldList();
		fl.add("ApplicableForRatePlan", null);
		fl.add("BillingFrequency", null);
				
		//TimeShiftTemplate tst = new TimeShiftTemplate("BillingRate", true, fl);
		TimeShiftTemplate tst = new TimeShiftTemplate("BillingDiscountRateList.BillingRate", fl, true);
		
		return tst;
	}
	
}
