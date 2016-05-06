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
import java.util.UUID;

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
    /* NC Temporary commented
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
	*/
	public static ArrayList<SensorData> fromJsonToSensorDataList(UUID machineAdapterID, String json){
		ArrayList<SensorData> sensorDataList = new ArrayList<SensorData>();
		try {
			_logger.info("SensorDataDao -  fromJsontoData");
			JSONObject jsonObject = new JSONObject(json);
			JSONArray acquisitionArray = (JSONArray) jsonObject.get("SampleList");
			
			for ( int i=0;  i<acquisitionArray.length(); i++){
				_logger.info("fromJson2Data json object: " + acquisitionArray.getString(i) );
				sensorDataList.add(fromJsonToSensorData(machineAdapterID, acquisitionArray.getString(i)));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_logger.error(e.getLocalizedMessage());
		}
		
		
		return sensorDataList;
	}
	
	private static SensorData fromJsonToSensorData(UUID machineAdapterID, String json)
	{
		SensorData receivedData = new SensorData();
		try
		{
		List<Sample> samples = new ArrayList<Sample>();
		
		JSONObject jsonObject = new JSONObject(json);
		
        String deviceId = jsonObject.getString("deviceID"); //$NON-NLS-1$
        String authToken = jsonObject.getString("authToken"); //$NON-NLS-1$
        JSONArray samplesList = (JSONArray) jsonObject.get("samples"); //$NON-NLS-1$
        receivedData.setDeviceID(deviceId);
        receivedData.setAuthToken(authToken);
        
        for(int i=0; i<samplesList.length(); i++)
        {
        	JSONObject currentObject = samplesList.getJSONObject(i);
        	Sample s = new Sample(machineAdapterID,currentObject.getString("pointName") );
        	s.setPointName(currentObject.getString("pointName")); //$NON-NLS-1$
        	s.setTimeStamp(new Date( Long.parseLong(currentObject.getString("timeStamp")))); //$NON-NLS-1$
        	//create the Value of correct Type.
        	s.setValue(Double.parseDouble(currentObject.getString("value"))); //$NON-NLS-1$
        	_logger.info("Double Casted: " + currentObject.getString("value"));
        	s.setQuality(currentObject.getString("value")); //$NON-NLS-1$
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
	/* NC commented
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
*/
}
