/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.hackathon.webservice;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author predix -
 */
public class Sample implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @return the pointName
	 */
	public String getPointName() {
		return this.pointName;
	}
	/**
	 * @param pointName the pointName to set
	 */
	public void setPointName(String pointName) {
		this.pointName = pointName;
	}
	/**
	 * @return the quality
	 */
	public String getQuality() {
		return this.quality;
	}
	/**
	 * @param quality the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}
	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return this.timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return this.value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		String out = ""; //$NON-NLS-1$
		out+="Point Name: \""+getPointName()+"\"; "; //$NON-NLS-1$ //$NON-NLS-2$
		out+="Value: \""+getValue()+"\"; "; //$NON-NLS-1$ //$NON-NLS-2$
		out+="Quality: \""+getQuality()+"\"; "; //$NON-NLS-1$ //$NON-NLS-2$
		out+="TimeStamp: \""+getTimeStamp()+"\"";  //$NON-NLS-1$//$NON-NLS-2$
				
		return out;
	}
	
	
	private String pointName;
	private String quality;
	private String timeStamp;
	private Object value;
	
}
