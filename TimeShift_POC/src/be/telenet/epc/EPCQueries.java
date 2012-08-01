package be.telenet.epc;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.amdocs.pc.domain.external.EPCElement;
import com.amdocs.pc.domain.external.EPCFieldData;
import com.amdocs.pc.domain.external.QueryInputParameter;
import com.amdocs.pc.domain.external.QueryPaginationInfo;
import com.amdocs.pc.exceptions.EPCExternalServiceException;


public class EPCQueries 
{
	private static final Logger log = Logger.getLogger(EPCQueries.class); 
	private ArrayList<EPCElement> m_elements = null;
	
	private static final int pageStart = 1;
	private static final int pageSize = 270;
	
	public static final int QUERYFIELD_INTEGER = 0;
	public static final int QUERYFIELD_STRING = 1;
	
	
	public EPCQueries(){
		m_elements = new ArrayList<EPCElement>();
	}
	
	public ArrayList<EPCElement> getElements() {
		return m_elements;
	}
	
	
	public QueryInputParameter setQueryParameter(String fieldname, String fieldvalue)
	{
		QueryInputParameter param = new QueryInputParameter();
		param.setParameterName(fieldname);
		
		EPCFieldData fieldData = new EPCFieldData();
		fieldData.setFieldValue(fieldvalue);
		fieldData.setFieldExtraData(null);
		
		return param;
	}
	
	public QueryInputParameter setQueryParameter(String fieldname, String fieldvalue, int fieldType)
	{
		QueryInputParameter param = new QueryInputParameter();
		param.setParameterName(fieldname);
		
		EPCFieldData fieldData = new EPCFieldData();
		fieldData.setFieldValue(fieldvalue);
		fieldData.setFieldExtraData(null);
		fieldData.setFieldType(fieldType);
		
		return param;
	}
	
	public QueryInputParameter setQueryParameter(String fieldname, String fieldvalue, String fieldExtraData)
	{
		QueryInputParameter param = new QueryInputParameter();
		param.setParameterName(fieldname);
		
		EPCFieldData fieldData = new EPCFieldData();
		fieldData.setFieldValue(fieldvalue);
		fieldData.setFieldExtraData(fieldExtraData);		
		
		return param;
	}
	
	public boolean executeQuery (String queryName, EPCClient client) {
		QueryPaginationInfo paginationInfo=null;
		if (client != null && client.getQueryServices() != null
				&& client.getContext() != null && client.getElementServices() != null){
			try {
				//System.out.println(client.getContext().getTicket());
					paginationInfo = client.getQueryServices().executeQuery(client.getContext(), queryName, null);

			} catch (EPCExternalServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			
			if (paginationInfo != null) {
				int iStart = 0;
				long ltotal = paginationInfo.getTotalRowsCount();
				while(iStart < ltotal)
				{
					paginationInfo.setStart(iStart == 0 ? 1 : iStart);
					paginationInfo.setPaginationPageSize( (int) ((long) (ltotal - iStart)<pageSize ? ltotal - iStart : pageSize));
					try {
						//System.out.println(client.getContext().getTicket());
						EPCElement [] elements = client.getElementServices().getElementsAsXmlsForPagination(client.getContext(), paginationInfo);
						
						if (elements != null && elements.length > 0) {							
							m_elements.addAll(Arrays.asList(elements));
						}
						iStart +=pageSize; 
					} catch (EPCExternalServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
			}
		}
		
		return false;
	}
}
