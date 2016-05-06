/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.hackathon.httpAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;




//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.annotation.component.Reference;

import com.ge.dspmicro.httpclient.api.HttpResponseWrapper;
import com.ge.dspmicro.httpclient.api.IHttpClient;
import com.ge.dspmicro.httpclient.api.IHttpClientFactory;
import com.ge.dspmicro.httpclient.api.IPredixCloudHttpClientFactory;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;
import com.ge.dspmicro.machinegateway.types.PQuality;
import com.ge.dspmicro.machinegateway.types.PQuality.QualityEnum;
//import com.mashape.unirest.http.HttpResponse;
//import com.mashape.unirest.http.JsonNode;
//import com.mashape.unirest.http.Unirest;
//import com.mashape.unirest.http.exceptions.UnirestException;


/**
 * 
 * @author Predix Machine Sample
 */
public class SampleDataSubscription
        implements Runnable, IDataSubscription
{
    private UUID                            uuid;
    private String                          name;
    private int                             updateInterval;
    private ISubscriptionMachineAdapter     adapter;
    private Map<UUID, SampleDataNode>       nodes         = new HashMap<UUID, SampleDataNode>();
    private List<IDataSubscriptionListener> listeners     = new ArrayList<IDataSubscriptionListener>();
    private Random                          dataGenerator = new Random();
    private final AtomicBoolean             threadRunning = new AtomicBoolean();

    private static final Logger               _logger             = LoggerFactory
            .getLogger(SampleDataSubscription.class);
    
    /** This injected factory creates private instances of IHttpClient that are not shared. */
    private IHttpClientFactory            httpClientFactory;

    /** A reserved IHttpClient for sending REST calls. */
    private IHttpClient                   httpClient;
    
    /**
     * Constructor
     * 
     * @param adapter machine adapter
     * @param subName Name of this subscription
     * @param updateInterval in milliseconds
     * @param nodes list of nodes for this subscription
     */
    public SampleDataSubscription(ISubscriptionMachineAdapter adapter, String subName, int updateInterval,
            List<SampleDataNode> nodes, IHttpClientFactory clientFactory)
    {
        if ( updateInterval > 0 )
        {
            this.updateInterval = updateInterval;
        }
        else
        {
            throw new IllegalArgumentException("updataInterval must be greater than zero."); //$NON-NLS-1$
        }

        if ( nodes != null && nodes.size() > 0 )
        {
            for (SampleDataNode node : nodes)
            {
                this.nodes.put(node.getNodeId(), node);
            }
        }
        else
        {
            throw new IllegalArgumentException("nodes must have values."); //$NON-NLS-1$
        }

        this.adapter = adapter;

        // Generate unique id.
        this.uuid = UUID.randomUUID();
        this.name = subName;
        
        

        this.httpClientFactory = clientFactory;
        this.httpClient = this.httpClientFactory.createHttpClient();

        /*
         * WARNING: In production, do not use this configuration. ALLOW_ALL_HOSTNAME_VERIFIER is not secure because it skips
         * hostname verification. Instead, use:
         * this.httpClientService.setAllowSSL(IHttpClient.sslType.ALLOW_DEFAULT_CERTS,
         * SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
         */
        this.httpClient.setAllowSSL(IHttpClient.sslType. ALLOW_ALL_HOSTNAME_VERIFIER,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        
        // Initialize random data generator.
        this.dataGenerator.setSeed(Calendar.getInstance().getTimeInMillis());
        this.threadRunning.set(false);
    }

    @Override
    public UUID getId()
    {
        return this.uuid;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int getUpdateInterval()
    {
        return this.updateInterval;
    }

    @Override
    public List<PDataNode> getSubscriptionNodes()
    {
        return new ArrayList<PDataNode>(this.nodes.values());
    }

    /**
     * @param listener callback listener
     */
    @Override
    public synchronized void addDataSubscriptionListener(IDataSubscriptionListener listener)
    {
    	_logger.info("Hack - addDataSubscriptionListener: Called");
        if ( !this.listeners.contains(listener) )
        {
            this.listeners.add(listener);
        }
        
    }

    /**
     * @param listener callback listener
     */
    @Override
    public synchronized void removeDataSubscriptionListener(IDataSubscriptionListener listener)
    {
        if ( !this.listeners.contains(listener) )
        {
            this.listeners.remove(listener);
        }
    }

    /**
     * get all listeners
     * 
     * @return a list of listeners.
     */
    @Override
    public synchronized List<IDataSubscriptionListener> getDataSubscriptionListeners()
    {
    	_logger.info("Hack - getDataSubscriptionListeners: Called");
        return this.listeners;
    }

    /**
     * Thread to generate random data for the nodes in this subscription.
     */
    @Override
    public void run()
    {

    	_logger.info("Hack - run: Called");
        if ( !this.threadRunning.get() && this.nodes.size() > 0 )
        {
            this.threadRunning.set(true);

            while (this.threadRunning.get())
            {
                // Generate random data for each node and push data update.
                List<PDataValue> data = new ArrayList<PDataValue>();

            	_logger.info("Hack - run: SampleDataNode list lenght: " + this.nodes.size());
            	
            	try {
				//	HttpResponse<JsonNode> response = Unirest.get("https://localhost:8443/testserver/getAllSampleAndCleanAll")
				//			  .header("content-type", "application/json").asJson();
				//	_logger.info(response.toString());
            		ArrayList<SensorData> values= GETAll();
            		
            		for (int i=0; i<values.size(); i++ ){
        				
            			SensorData sensorData= values.get(i);
            			_logger.info("adding SensorData: " + sensorData.getDeviceID() );
            			List <Sample> samples= sensorData.getSamples(); 
            			for (int j=0; j<samples.size(); j++) {
            				 // Simulating the data.
            				Sample sample= samples.get(j);
            				_logger.info("adding value: " + " pointName: "+sample.getPointName() +" nodeID: "+ sample.getNodeId() +" Value: "+  sample.getValue() +" Address: "+  sample.getAddress() );
            				PEnvelope envelope = new PEnvelope(sample.getValue());
            				_logger.info(" value type: " + sample.getValue().getClass() );
            				PDataValue value = new PDataValue(sample.getNodeId() , envelope);
                            value.setNodeName(sample.getPointName());
                            value.setAddress(sample.getAddress());
                            value.setQuality(new PQuality(QualityEnum.GOOD));
                          //  envelope.
                            data.add(value);
                            
            				
            				
            			}
            			
            			
            		}
            		
            		for (SampleDataNode node : this.nodes.values())
                    {
                        // Simulating the data.
                        PEnvelope envelope = new PEnvelope(this.dataGenerator.nextFloat());
                        PDataValue value = new PDataValue(node.getNodeId(), envelope);
                        value.setNodeName(node.getName());
                        value.setAddress(node.getAddress());
                      //  envelope.
                        data.add(value);
                    }

                    // Writing the simulated data into cache
                     ((HackathonHttpAdapter) this.adapter).putData(data);

                    for (IDataSubscriptionListener listener : this.listeners)
                    {
                        listener.onDataUpdate(this.adapter, data);
                    }
                    try
                    {
                        // Wait for an updateInterval period before pushing next data update.
                        Thread.sleep(this.updateInterval);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					_logger.info("GET error");
				}
            	
                
            }
        }
    }

    /**
     * Stops generating random data.
     */
    public void stop()
    {
        if ( this.threadRunning.get() )
        {
            this.threadRunning.set(false);

            // Send notification to all listeners.
            for (IDataSubscriptionListener listener : this.listeners)
            {
                listener.onSubscriptionDelete(this.adapter, this.uuid);
            }

            // Do other clean up if needed...
        }
    }
    
    
    /* Private Methods
     * 
     */
    
    private static final String USER_AGENT = "Mozilla/5.0";
    
    private static final String GET_URL = "https://localhost:8443/testserver/getAllSampleAndCleanAll";
 
    private static final String POST_URL = "http://localhost:9090/SpringMVCExample/home";
 
    private ArrayList<SensorData> GETAll() throws IOException {
     //   CloseableHttpClient httpClient = HttpClients.createDefault();
     //   HttpGet httpGet = new HttpGet(GET_URL);
        //httpGet.addHeader("User-Agent", USER_AGENT);
   //     httpGet.addHeader("content-type", "application/json");
   //     CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        
        // Sending a GET request to the cloud.
        URI uri;
        _logger.info("SendGET called.");
        ArrayList<SensorData> result= new ArrayList<SensorData>();
		try {
			   _logger.info("SendGET: inside Try");
			uri = new URI("https://localhost:8443/testserver/getAllSampleAndCleanAll");
			   _logger.info("SendGET: URI" + uri.getHost() + uri.getPort()+ uri.getPath());

	        HttpResponseWrapper httpResponse = getHttpClientService().get(uri);
	        _logger.info("GET Response Status: "
	                + httpResponse.getStatusCode());
	        _logger.info(httpResponse.getContent());
	      
	       result= SensorDataDao.fromJsonToSensorDataList(adapter.getId(), "{\"SampleList\":"+httpResponse.getContent().toString()+"}");
	       _logger.info("GetAll - result: " + result.toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			_logger.info("GET Response Interrupted:" + e.getMessage() + e.toString() );
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			_logger.info("GET Response URI:" + e.getMessage());
			e.printStackTrace();
		}
		
		return result;
 
     /*   String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
 
        // print result
        _logger.info(response.toString());
        httpClient.close();
        */
    }
 
    private  void sendPOST() throws IOException {
 
     /*   CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(POST_URL);
        httpPost.addHeader("User-Agent", USER_AGENT);
 
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("userName", "Pankaj Kumar"));
 
        HttpEntity postParams = new UrlEncodedFormEntity(urlParameters);
        httpPost.setEntity(postParams);
 
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
 
        System.out.println("POST Response Status:: "
                + httpResponse.getStatusLine().getStatusCode());
 
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));
 
        String inputLine;
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
 
        // print result
        System.out.println(response.toString());
        httpClient.close();
 */
    }

    public IHttpClient getHttpClientService()
    {
        return this.httpClient;
    }
    
    
    
}
