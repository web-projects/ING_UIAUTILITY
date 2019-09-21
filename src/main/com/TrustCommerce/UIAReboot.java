package com.TrustCommerce;

import com.ingenico.api.jpos.IngenicoMSR;

public class UIAReboot {
	
	private static IngenicoMSR msr;
			
	public static void Reboot(String modelName) {
		
		try {
			System.out.println("Attempting to reboot device..."); 
			msr = new IngenicoMSR();
			//System.out.println("open() MSR_{}");
			//msr.open("MSR_" + modelName);
			msr.open(modelName);
			msr.claim(10000);
			// Reboot device
			msr.directIO(106, new int[] { 1 }, "");
			System.out.println("reboot: command issued.");
			System.out.println("");
			System.exit(0);
		} 
		catch (Exception e) {
			System.out.println("IngenicMSR(): " + e.getMessage());
			System.exit(0);
		}
	}
}