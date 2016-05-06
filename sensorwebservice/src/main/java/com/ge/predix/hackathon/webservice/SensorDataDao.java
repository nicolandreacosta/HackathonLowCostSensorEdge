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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author predix -
 */
public class SensorDataDao {
	
    private static Logger _logger = LoggerFactory.getLogger(SensorDataDao.class);

	
	
	/**
	 * @param jsonValue
	 * @return -
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("javadoc")
	public static void addSamples(final String jsonValue)
	{
		String folderName = Messages.getString("SampleDao.repository"); //$NON-NLS-1$
		try{
			File dir = new File(folderName);
	
			if(!dir.exists())
			{
				dir.mkdir();
			}
			SensorData data = fromJsonToSensorData(jsonValue);
			OutputStream os = new FileOutputStream(folderName+"record_"+System.currentTimeMillis()+".txt");  //$NON-NLS-1$//$NON-NLS-2$
			final ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(data);
			oos.close();
		}
		catch(Exception ex)
		{
			_logger.error(ex.getLocalizedMessage());
		}
	}
	
	private static SensorData fromJsonToSensorData(String json)
	{
		SensorData receivedData = new SensorData();
		try
		{
		List<Sample> samples = new ArrayList<Sample>();
		
		JSONObject jsonObject = new JSONObject(json);
		
        String deviceId = jsonObject.getString("DeviceID"); //$NON-NLS-1$
        String authToken = jsonObject.getString("AuthToken"); //$NON-NLS-1$
        JSONArray samplesList = (JSONArray) jsonObject.get("Samples"); //$NON-NLS-1$
        receivedData.setDeviceID(deviceId);
        receivedData.setAuthToken(authToken);
        
        for(int i=0; i<samplesList.length(); i++)
        {
        	JSONObject currentObject = samplesList.getJSONObject(i);
        	Sample s = new Sample();
        	s.setPointName(currentObject.getString("PointName")); //$NON-NLS-1$
        	//s.setTimeStamp(new Date(currentObject.getString("TimeStamp")));
        	s.setTimeStamp(currentObject.getString("TimeStamp"));
        	s.setValue(currentObject.getString("Value")); //$NON-NLS-1$
        	s.setQuality(currentObject.getString("Value")); //$NON-NLS-1$
        	samples.add(s);
        }
        
        receivedData.setSamples(samples);
        
		}
		catch (Exception ex)
		{
			_logger.error(ex.getLocalizedMessage());
		}
        return receivedData;
	}
	
	private static String fromSensorDataToJson(SensorData sensor)
	{
		JSONObject sensorObject = new JSONObject(sensor);
		
		return sensorObject.toString();
		
		
	}
	/**
	 * @return -
	 */
	
	public static List<String> getAllSamplesAndCleanAll()
	{
    	 List<String> currentValues = new ArrayList<String>();
		
		String folderName = Messages.getString("SampleDao.repository"); //$NON-NLS-1$
		File folder = new File(folderName);
		File[] listOfFiles = folder.listFiles();
		SensorData s;
		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	ObjectInputStream ois = null;
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					ois = new ObjectInputStream(fileInputStream);
					s = (SensorData) ois.readObject();
					
					currentValues.add(fromSensorDataToJson(s));
					
					fileInputStream.close();
					file.delete();
				} catch (Exception e) {
					_logger.error(e.getLocalizedMessage());
				}
				finally
				{
					if(ois!= null)
						try {
							ois.close();
						} catch (IOException e) {
							_logger.error(e.getLocalizedMessage());
						}
				}
				
		    }
		}
		return currentValues;
	}

}
