package be.telenet.epc;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amdocs.pc.domain.external.EPCServicesSessionContext;
import com.amdocs.pc.exceptions.EPCExternalServiceException;
import com.amdocs.pc.services.EPCElementServices;
import com.amdocs.pc.services.EPCQueryServices;
import com.amdocs.pc.services.EPCRuleServices;
import com.amdocs.pc.services.EPCSecurityServices;
import com.amdocs.pc.services.EPCTemplateServices;

public class EPCClient {
	
	private static final Logger log = Logger.getLogger(EPCClient.class); 
	
	private static final String WLS_PROTOCOL = "t3";
	private static final String WLS_INITIAL_CONTEXT = "weblogic.jndi.WLInitialContextFactory";
	private static final String RULE_SERVICES_JNDI = "epc#com.amdocs.pc.services.EPCRuleServices";
	private static final String SECURITY_SERVICES_JNDI = "epc#com.amdocs.pc.services.EPCSecurityServices";
	private static final String QUERY_SERVICES_JNDI = "epc#com.amdocs.pc.services.EPCQueryServices";
	private static final String ELEMENT_SERVICES_JNDI = "epc#com.amdocs.pc.services.EPCElementServices";
	private static final String TEMPLATE_SERVICES_JNDI = "epc#com.amdocs.pc.services.EPCTemplateServices";
	
	public static final String PARENT_RULE_PATH = "parentRule.BusinessRule_Rule";
	
	private EPCRuleServices ruleServices;
	private EPCSecurityServices securityServices;
	private EPCQueryServices queryServices;
	private EPCElementServices elementServices;
	private EPCTemplateServices templateServices;
	
	private String serverName;
	private String serverPort;
	private String epcUsername;
	private String epcPassword;
	private String epcProject;	
	
	private EPCServicesSessionContext context;	
	
	public EPCClient() {

	}	
	public EPCClient(String serverName, String serverPort, String epcUsername,
			String epcPassword, String epcProject) {
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.epcUsername = epcUsername;
		this.epcPassword = epcPassword;
		this.epcProject = epcProject;
	}
	private void afterPropertiesSet() throws Exception {
		Properties ruleTestClientProps = new java.util.Properties();
		ruleTestClientProps.put("EPC.Server.Name", serverName);
		ruleTestClientProps.put("EPC.Server.Port", serverPort);
		
		Properties props = new java.util.Properties();
		props.put(Context.PROVIDER_URL, WLS_PROTOCOL + "://" + serverName + ":" + serverPort);
		props.put(Context.INITIAL_CONTEXT_FACTORY, WLS_INITIAL_CONTEXT);
		Context initialContext = new InitialContext(props);
		securityServices = (EPCSecurityServices) initialContext.lookup(SECURITY_SERVICES_JNDI);
		ruleServices = (EPCRuleServices) initialContext.lookup(RULE_SERVICES_JNDI);
		queryServices = (EPCQueryServices) initialContext.lookup(QUERY_SERVICES_JNDI);
		elementServices = (EPCElementServices) initialContext.lookup(ELEMENT_SERVICES_JNDI);
		templateServices = (EPCTemplateServices) initialContext.lookup(TEMPLATE_SERVICES_JNDI);
	}

	public boolean login() {
		boolean status = false;
		log.debug("first calling afterPropertiesSet");
		
		try {
			afterPropertiesSet();
		} catch(Exception e) {
			throw new RuntimeException("while setting properties: " + e.getMessage(), e);
		}
		
		String project = checkProject();
		log.debug("login with [" + epcUsername + "], serverName [" + serverName + ":" + serverPort + ", project [" + project + "]");
		try {
			String ticket = securityServices.login(epcUsername, epcPassword, true);
			log.debug("ticket: " + ticket);
			//System.out.println(ticket);
			context = new EPCServicesSessionContext(epcUsername, ticket, "en", project);
			//System.out.println(context.getTicket());
			status = true;
		} catch(EPCExternalServiceException e) {
			throw new RuntimeException("while login in: " + e.getMessage(), e);
		}
		
		return status;
	}
	
	protected String checkProject() {
		if (epcProject == null || epcProject.isEmpty()) {
			return null;
		}
		return epcProject;
	}
	
	public void logout() {
		log.debug("logout");
		try {
			securityServices.logout(context);
		} catch(EPCExternalServiceException e) {
			throw new RuntimeException("while login out: " + e.getMessage(), e);
		}
	}
	
	public EPCServicesSessionContext getContext() {
		return context;
	}
	
	/**
	 * Evaluate given rule.If not logged in, login() is called first.
	 * 
	 * @param parentRuleId
	 * @param map
	 * @return
	 * TODO move this to service!!!
	 */
	public Serializable evaluateRule(String parentRuleId, Map<String, Serializable> map) {
		log.debug("evaluateRule");
		if (context == null) {
			log.debug("context is null -> login required");
			login(); 
		}
		try {
			return ruleServices.evaluateRule(
					context, parentRuleId, Calendar.getInstance(), PARENT_RULE_PATH, map);
		} catch(EPCExternalServiceException e) {
			//log.error("EpcClient exception while evaluateRule: " + e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public void setEpcUsername(String epcUsername) {
		this.epcUsername = epcUsername;
	}

	public void setEpcPassword(String epcPassword) {
		this.epcPassword = epcPassword;
	}

	public void setEpcProject(String epcProject) {
		this.epcProject = epcProject;
	}
	
	public EPCQueryServices getQueryServices() {
		return queryServices;
	}
	
	public EPCElementServices getElementServices() {
		return elementServices;
	}
	
	public EPCTemplateServices getTemplateService() {
		return templateServices;
	}
	
	public EPCRuleServices getRuleServices() {
		return ruleServices;
	}

}
