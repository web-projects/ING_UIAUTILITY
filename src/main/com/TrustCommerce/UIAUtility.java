package com.trustcommerce;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.trustcommerce.ipa.dal.constants.device.TerminalModel;

public class UIAUtility {

	private final static String Version = "1.0.0";
	
	private static Document dom;
	static Hashtable<String, TerminalModel> deviceTable = new Hashtable(); 
	
	public static void main(String args[]) {
		
		// Must have parameters
		if(args.length == 0) {
			System.out.println("main: parameters missing.");
			System.exit(0);
		}
		
		// Redirect Error Output Stream to null
		PrintStream original = new PrintStream(System.err);
		try {
			System.setErr(new PrintStream(new FileOutputStream("NUL:")));
		} 
		catch (FileNotFoundException e) {
			System.out.println("main: OUTPUT stream redirect exception=" + e.getMessage()); 
		}
		
		// Process Commands
		String command = args[0];
		
		if(command.equals("IDENTIFY"))
		{
			UIAIdentify(args);
		}
		else if(command.equals("REBOOT")) 
		{
			UIAReboot(args);
		}
		else if(command.equals("VERSION")) 
		{
			System.out.println("UIAUtility version " + Version);
		}
		else
		{
			System.out.println("main: COMMAND missing."); 
		}
		
		System.exit(0);
    }

	private static void UIAIdentify(String args[])
	{
		// IDENTIFY COMMAND:
		// 	param1 = JPOS.XML
		if(args.length == 2)
		{
			String jPosFilename = args[1];
		    boolean exists = UIACheck.jposExists(jPosFilename, true);
		    
		    if(exists)
		    {
		    	try {
			    	// Get Device Health
			    	UIACheck.UIACheck("", true);
			    	
			    	if(UIACheck.DeviceReported())
			    	{
			    	}
		    	}
		    	catch (Exception e) {
					System.out.println("main: IDENTIFY exception=" + e.getMessage()); 
				}
		    }
		}
		else
		{
			System.out.println("main: REBOOT - invalid number of arguments."); 
		}
	}
	
	private static void UIAReboot(String args[]) 
	{
		// REBOOT COMMAND:
		// 	param1 = JPOS.XML
		// 	param2 = DEVICE NAME
		// 	parma3 = DEVICE PORT
		if(args.length == 4)
		{
			String jPosFilename = args[1];
			String deviceName   = "MSR_" + args[2];
			String devicePort   = args[3];

			deviceTable.put("MSR_iSC250", TerminalModel.iSC250);
			deviceTable.put("MSR_iPP350", TerminalModel.iPP350);
			deviceTable.put("MSR_iSC480", TerminalModel.iSC480);
			deviceTable.put("MSR_iUP250", TerminalModel.iUP250);
			deviceTable.put("MSR_iPP320", TerminalModel.iPP320);
			
		    // Reboot device
		    //System.out.println("\n----------------------------------------------------------------------------------------------------------------");
		    
		    // Check for file
		    boolean exists = UIACheck.jposExists(jPosFilename, true);
		    
		    if(exists)
		    {
		    	try {
			    	// Get Device Health
			    	boolean result = UIACheck.UIACheck("", false);
			    	String portAttached = UIACheck.GetDevicePort();
			    	
			    	if(devicePort.equals(portAttached))
			    	{
				    	if(UIACheck.DeviceReported() && result)
				    	{
				    		String FILEROOT = jPosFilename.substring(0, jPosFilename.lastIndexOf('/')  + 1);
				    		String JPOS_REBOOT = FILEROOT + "JPOSREBOOT.XML"; 
					    	String portName = UIACheck.GetDevicePort();
			    			NodeList jposEntries = ParseXmlFile(jPosFilename);
			    			
			    			if(jposEntries != null) {
			    				//SetPropertyValue(JPOS_REBOOT, jposEntries, deviceName, "portName", portName);
			    				SetPropertyValue(JPOS_REBOOT, jposEntries, deviceName, "portName", devicePort);
			    			}
			
			    			// Set to new XML file
			    			if(!UIACheck.DeviceFound()) {
			    				//UIACheck.UIACheck(JPOS_REBOOT);
			    				if(deviceTable.containsKey(deviceName)) {
				    				TerminalModel tm = deviceTable.get(deviceName);
			    					result = UIACheck.OpenMSR(tm, JPOS_REBOOT, false);
			    				}
			    			}
			    			
					    	//TerminalModel model = UIACheck.GetDeviceModelName();
			    			
			    		    // Reboot Device
			    			if(result) {
			    				//UIAReboot.Reboot(model.name());
			    				UIAReboot.Reboot(deviceName);
			    			}
				    	}
			    	}
			    	else
			    	{
			    		System.out.println("reqTermInfo: device not found at " + devicePort);
			    	}
				} 
		    	catch (Exception e) {
					System.out.println("main: REBOOT exception=" + e.getMessage()); 
				}
		    }
		}
		else
		{
			System.out.println("main: REBOOT - invalid number of arguments."); 
		}
	}
	
	private static boolean DuplicateFile(String src, String dst) throws IOException {
		boolean success = false;
		
		InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(src);
	        os = new FileOutputStream(dst);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	        success = true;
	    } finally {
	        is.close();
	        os.close();
	    }
	    
		return success;
	}
	
	private static NodeList ParseXmlFile(String source) throws Exception {
		
		NodeList jposEntries = null;
		
		try {

			// Remove Standard Library
			Properties systemProperties = System.getProperties();
			systemProperties.remove("javax.xml.parsers.DocumentBuilderFactory");
			System.setProperties(systemProperties);
			
			// set the factory
			//String provider = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
			String provider = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory", provider);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(provider, ClassLoader.getSystemClassLoader());
			
			factory.setValidating(false);

			//Using factory get an instance of document builder
			DocumentBuilder builder = factory.newDocumentBuilder();

			builder.setEntityResolver(new EntityResolver() {

	            public InputSource resolveEntity(String publicId, String systemId)
	                    throws IOException {
	                //System.out.println("Ignoring " + publicId + ", " + systemId);
	                return new InputSource(new StringReader(""));
	            }
	        });			
			
			//parse using builder to get DOM representation of the XML file
			dom = builder.parse(source);
			//System.out.println(dom.getDocumentElement().getTagName());
			Element root = dom.getDocumentElement();
			//System.out.println(root.getNodeName());
			
			jposEntries = root.getElementsByTagName("JposEntry");
		}
		catch(ParserConfigurationException pce) {
			System.out.println("main: ParseXmlFile() ParserConfigurationException=" + pce.getMessage());
		}
		catch(IOException ioe) {
			System.out.println("main: ParseXmlFile() IOException=" + ioe.getMessage());
		} 
		catch (org.xml.sax.SAXException e) {
			System.out.println("main: ParseXmlFile() SAXException=" + e.getMessage());		
	    } 
		
		return jposEntries;
	}
	
	static boolean SetPropertyValue(String fileName, NodeList jposEntries, String logicalName, String name, String value)
	{
		boolean found = false;

		try
		{
		    for(int i = 0; i < jposEntries.getLength() && !found; i++)
		    {
		    	NamedNodeMap nm = jposEntries.item(i).getAttributes();
		    	Node jposEntryAttribute = nm.getNamedItem("logicalName");
		    	//System.out.println(jposEntries.item(i).getNodeName() + "      : " + jposEntryAttribute.getNodeValue());
		    	
		    	if(jposEntryAttribute.getNodeValue().equals(logicalName))
		    	{
			    	NodeList children = jposEntries.item(i).getChildNodes();
			    	for(int j = 0; j < children.getLength(); j++)
			    	{
			    		Node node = children.item(j);
		    			String childNodeName = node.getNodeName();
			    		
			    		if (node.getNodeType() == Node.ELEMENT_NODE && childNodeName.equals("prop")) 
			    		{
			                NamedNodeMap nnm = node.getAttributes();
			                Node propAttribute = nnm.getNamedItem("name");
			                String propAttributeName = propAttribute.getNodeValue(); 
			                //System.out.println(childNodeName + ": " + propAttributeName);
			                if(propAttributeName.equals(name))
			                {
			                	Node portValueAttribute = nnm.getNamedItem("value");
			                	//System.out.println("port old value : " + portValueAttribute.getNodeValue());
			                	found = true;
			                	
			                	// change value
			                	if(!portValueAttribute.getNodeValue().equals(value))
			                	{
			                		portValueAttribute.setNodeValue(value);
			                		
			                		Transformer transformer = TransformerFactory.newInstance().newTransformer();
			                		Source input = new DOMSource(dom);
	
			                		// Direct output to file
			                		Result output = new StreamResult(new FileOutputStream(fileName));
			                        
			                        // File formatting properties
			                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			                        
			                        // Save into file: XERCES causes exception
			                		transformer.transform(input, output);
			                	}
			                	
			                	break;
			                }
			            }
			    	}
		    	}
		    }
		}
		catch(TransformerException tx) {
			System.out.println("main: ParseXmlFile() TransformException=" + tx.getMessage());
		}
		catch(Exception ioe) {
			System.out.println("main: ParseXmlFile() Exception=" + ioe.getMessage());
		}
		
		return found;
	}
}