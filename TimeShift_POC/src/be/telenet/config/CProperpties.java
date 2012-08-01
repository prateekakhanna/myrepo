/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package be.telenet.config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

/**
 *
 * @author PRATEEKK
 */
public class CProperpties
{
    private  String DBUsername;
    private  String DBPassword;
    private  String DBMachine;
    private  String DBService;
    
    public String strElementaryFilePath =null;
    public String strBillingDiscountInputPath =null;
    public String strBillingDiscountOutputPath = null;
    
    public String getDBUsername() {
		return DBUsername;
	}

	public String getDBPassword() {
		return DBPassword;
	}

	public String getDBMachine() {
		return DBMachine;
	}

	public String getDBService() {
		return DBService;
	}

	public String getStrElementaryFilePath() {
		return strElementaryFilePath;
	}

	public Properties readProperties(String filename) throws IOException, Exception
    {
        Properties prop = new Properties();
        File fProp = new File(filename);

        if (fProp.exists())
        {
            prop.load(new FileInputStream(fProp));

            if (fProp != null)
            {
                SetProperty(prop);
                return prop;
            }
        }

        return null;
    }

    public String getStrBillingDiscountInputPath() {
		return strBillingDiscountInputPath;
	}

	public String getStrBillingDiscountOutputPath() {
		return strBillingDiscountOutputPath;
	}

	public void writeProperties(Vector vec) throws IOException, Exception
    {
        File fwrite = new File("RTS_QA.properties");
        FileOutputStream fout = new FileOutputStream(fwrite);

        for (int i=0; i < vec.size(); i++)
        {
            fout.write(vec.get(i).toString().getBytes());
            fout.write('\n');
        }//end of for loop
        fout.close();
    }

    private void SetProperty(Properties prop)
    {
        DBUsername=prop.getProperty("Username");
        DBPassword=prop.getProperty("Password");
        //DBInstance=prop.getProperty("Instance");
        DBService= "jdbc:oracle:oci:@" + prop.getProperty("Service");
        
        strElementaryFilePath=prop.getProperty("ELEMENTARY_XML");
        strBillingDiscountInputPath=prop.getProperty("BILLING_DISCOUNT_INPUT");
        strBillingDiscountOutputPath = prop.getProperty("BILLING_DISCOUNT_OUTPUT");
    }
	
}
