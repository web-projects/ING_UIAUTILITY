package com.TrustCommerce;

import java.util.Enumeration;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.comm.*;

//The java code for these classes can be found at: .../IPA/IPAApp/4.2.1/jDAL/DeviceManager/src/com/trustcommerce/.../*.java
import com.trustcommerce.ipa.dal.constants.device.TerminalModel;
import com.trustcommerce.ipa.dal.terminal.IngenicoTerminal;
import com.trustcommerce.ipa.dal.model.Terminal;
import com.ingenico.api.jpos.IngenicoMSR;

import jpos.util.JposPropertiesConst;

public class UIACheck {
	
	private static Terminal term;
	private static IngenicoTerminal iTerm;
	private static CommPortIdentifier port;
	private static String serialPort = "";
	private static IngenicoMSR msr;
	private static String JPOSXML = "C:/TrustCommerce/jDAL/jpos/res/jpos.xml";
	private static boolean deviceReported = false;
	private static boolean deviceFound = false;
	private static final String JRE_NAME = "TCIPAjDAL.exe";
			
	public static boolean UIACheck(String fileName, boolean report) {
		
		if (!jposExists(fileName, false)) {
			System.out.println("Unable to find JPOS settings!");
			return false;
		}
		
		if (updaterJreIsRunning()) {
			System.out.println("DAL Running, unable to proceed.");
			return false;
		}
		
		boolean result = false;
		serialPort = "";
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();
		
		while (ports.hasMoreElements()) {
			
			port = (CommPortIdentifier) ports.nextElement();
			String type;
			
			switch (port.getPortType()) {
			
				case CommPortIdentifier.PORT_PARALLEL:
				{
					type = "Parallel";
					break;
				}
				
				case CommPortIdentifier.PORT_SERIAL:
				{
					type = "Serial";
					serialPort = port.getName();
					//System.out.println("available ports: " + serialPort);
					
					try {
						if (serialPort.equals("COM109")) {
							//System.out.println("Checking for UIA on " + serialPort);
							deviceFound = true;
							result = OpenMSR(TerminalModel.iSC250, JPOSXML, report);
						} else if (serialPort.equals("COM110")) {
							//System.out.println("Checking for UIA on " + serialPort);
							deviceFound = true;
							result = OpenMSR(TerminalModel.iPP350, JPOSXML, report);
						} else if (serialPort.equals("COM111")) {
							//System.out.println("Checking for UIA on " + serialPort);
							deviceFound = true;
							result = OpenMSR(TerminalModel.iSC480, JPOSXML, report);
						} else if (serialPort.equals("COM112")) {
							//System.out.println("Checking for UIA on " + serialPort);
							deviceFound = true;
							result = OpenMSR(TerminalModel.iUP250, JPOSXML, report);
						} else if (serialPort.equals("COM113")) {
							//System.out.println("Checking for UIA on " + serialPort);
							deviceFound = true;
							result = OpenMSR(TerminalModel.iPP320, JPOSXML, report);
						} else {
							if(report) {
								//System.out.println("port selected  : " + port.getName());
								System.out.println("Name is " + port.getName());
							}
							deviceFound = false;
							result = true;
						}
					} 
					catch (Exception e) {
						System.out.println("Port identifier exception: " + e.getMessage());
					}
					break;
				}
				
				default: /// Shouldn't happen
				{
					type = "Unknown";
					break;
				}
			}
		}
		
		// Check for serial port
		if(serialPort.length() > 0) {
			deviceReported = true;
		}
		
		if (!deviceReported) {
			System.out.println("No attached devices found.");
		}
		
		return result;
	}
  
	public static boolean OpenMSR(final TerminalModel tm, String fileName, boolean report) {
		
		boolean openSucceed = false;
		boolean infoSucceed = false;
		
		//Set the JPOS library to find our jpos.xml
		System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, fileName);
		
		try {
			iTerm = new IngenicoTerminal(tm);
			openSucceed = true;
		} 
		catch (Exception e) {
			System.out.println("new Term: " + e.getMessage()); 
		}
		
		try {
			if (iTerm != null && openSucceed) {
				iTerm.requestTerminalInformation();
				infoSucceed = true;
			}
		} 
		catch (Exception e) {
			System.out.println("reqTermInfo: " + e.getMessage()); 
		}
		
		try {
			if (iTerm != null && infoSucceed) {
				term = iTerm.getTerminalInfo();
				if(report)
				{
					System.out.println("Firmware Ver   : " + term.getFirmwareVersion()); 
					System.out.println("Model          : " + term.getModelName());
					System.out.println("FullData       : " + term.toString());
				}
				deviceReported = true;
			}
		} 
		catch (Exception e) {
			System.out.println("reqTermInfo: " + e.getMessage()); 
		}
		
		try {
			if (iTerm != null && openSucceed) {
				iTerm.releaseDevice();
			}
		} 
		catch (Exception e) {
			System.out.println("reqTermInfo: " + e.getMessage()); 
		}
		
		return infoSucceed;
	}
	
	public static boolean jposExists(String fileName, boolean display) {
		
		boolean jposFound = false;
		
		if (fileName.length() > 0 && !fileName.equals("reboot")) {
			java.io.File f = new java.io.File(fileName);
			if(f.exists() && !f.isDirectory()) { 
				jposFound = true;
				JPOSXML = fileName;
				if(display) {
					//System.out.println("jpos location  : " + JPOSXML);
				}
			}
		} 
		else {
			java.io.File f = new java.io.File(JPOSXML);
			if(f.exists() && !f.isDirectory()) { 
				jposFound = true;
				if(display) {
					//System.out.println("jpos location  : " + JPOSXML);
				}
			}
		}
		return jposFound;
	}
	
	//The version of this in a delivered DAL(tcIngenico.jar) does not work for us.
    //.../com/trustcommerce/ipa/dal/uploader/utils/UploaderUtils.java 
	//contains ingenicoJreIsRunning() which is the base for this code
	public static boolean updaterJreIsRunning() {
		int jres = 0;
		String line = null;

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				if (line.contains(JRE_NAME)) {
					jres++;
				}
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (jres >= 1) {  //original had > 1, we need >= 1 
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean DeviceReported() {
		return deviceReported;
	}
	
	public static boolean DeviceFound() {
		return deviceFound;
	}
	
	public static TerminalModel GetDeviceModelName() {
		return term.getModelName();
	}
	
	public static String GetDevicePort() {
		return serialPort;
	}
}