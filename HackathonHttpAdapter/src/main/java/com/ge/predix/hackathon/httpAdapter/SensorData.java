/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.hackathon.httpAdapter;

import java.io.Serializable;
import java.util.List;


/**
 * 
 * @author predix -
 */
public class SensorData  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String deviceID;
	private String authToken;
	private List<Sample> samples;
	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return this.deviceID;
	}
	/**
	 * @param deviceID the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return this.authToken;
	}
	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
	/**
	 * @return the samples
	 */
	public List<Sample> getSamples() {
		return this.samples;
	}
	/**
	 * @param samples the samples to set
	 */
	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}
	
	@Override
	public String toString()
	{
		String out = ""; //$NON-NLS-1$
		out += "DeviceID: \""+getDeviceID()+"\"; "+ //$NON-NLS-1$ //$NON-NLS-2$
		"AuthToken: \""+getAuthToken()+"\"; ";  //$NON-NLS-1$//$NON-NLS-2$
		out += "Sample: ["; //$NON-NLS-1$
		for (Sample s : this.samples) {
			out+=s.toString();
		}
		out += "]";	 //$NON-NLS-1$
		
		return out;
	}
}
