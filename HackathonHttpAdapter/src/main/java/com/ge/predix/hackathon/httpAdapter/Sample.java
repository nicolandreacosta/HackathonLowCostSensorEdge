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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;

import com.ge.dspmicro.machinegateway.types.PDataNode;

/**
 * 
 * @author predix -
 */
public class Sample extends PDataNode implements Serializable {

	 // Add node specific attribute here as needed.
    private String nodeSpecificAttribute;

    //
    
    /**
     * Constructor
     * 
     * @param machineAdapterId a unique id
     * @param name string value
     */
    public Sample(UUID machineAdapterId, String name)
    {
        super(machineAdapterId, name);

        // Do other initialization if needed.
    }

    /**
     * @return the nodeSpecificAttribute
     */
    public String getNodeSpecificAttribute()
    {
        return this.nodeSpecificAttribute;
    }

    /**
     * @param nodeSpecificAttribute the nodeSpecificAttribute to set
     */
    public void setNodeSpecificAttribute(String nodeSpecificAttribute)
    {
        this.nodeSpecificAttribute = nodeSpecificAttribute;
    }

    /**
     * Node address to uniquely identify the node.
     */
    @Override
    public URI getAddress()
    {
        try
        {
            URI address = new URI("sample.subscription.adapter", null, "localhost", -1, "/" + getName(), null, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return address;
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }
	
	
	
	
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
	public Date getTimeStamp() {
		return this.timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
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
	private Date timeStamp;
	private Object value;
	
	
	
	
	
}
