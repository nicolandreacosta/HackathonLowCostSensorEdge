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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.service.component.ComponentContext;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.ConfigurationPolicy;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Modified;
//import aQute.bnd.annotation.metatype.Configurable;
//import aQute.bnd.annotation.metatype.Meta;

import aQute.bnd.annotation.component.Reference;

import com.ge.dspmicro.httpclient.api.IHttpClient;
import com.ge.dspmicro.httpclient.api.IHttpClientFactory;
import com.ge.dspmicro.machinegateway.api.IMachineGateway;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.IMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterException;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterInfo;
import com.ge.dspmicro.machinegateway.api.adapter.MachineAdapterState;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A random machine adapter that
 */
@Component(name = HackathonHttpAdapter.SERVICE_PID)
public class HackathonHttpAdapter
        implements ISubscriptionMachineAdapter, IMachineAdapter
{
    /**
     * Service Persistence ID
     */
    public static final String  SERVICE_PID         = "com.ge.predix.solsvc.MachineTraining2.randomadapter"; //$NON-NLS-1$

    private static final String ADAPTER_NAME        = "HackathonAdapter";                                       //$NON-NLS-1$
    private static final String ADAPTER_NODE        = "HackathonNode"; //$NON-NLS-1$
    private static final String ADAPTER_DESCRIPTION = "An adapter for Hackathon";              //$NON-NLS-1$

    private UUID                uuid                = UUID.randomUUID();
    private MachineAdapterInfo  info;
    private Random              random;
    private PDataNode           node;
    private UUID                nodeUUID            = UUID.randomUUID();
    private IMachineGateway               gateway;
    private Map<UUID, SampleDataSubscription> dataSubscriptions   = new HashMap<UUID, SampleDataSubscription>();
    private int                               updateInterval;
    protected Map<UUID, PDataValue>           dataValueCache      = new ConcurrentHashMap<UUID, PDataValue>();
    
    private static Logger   _logger     = LoggerFactory.getLogger(HackathonHttpAdapter.class);
    
    
    /** This injected factory creates private instances of IHttpClient that are not shared. */
    private IHttpClientFactory            httpClientFactory;

    /** A reserved IHttpClient for sending REST calls. */
    private IHttpClient                   httpClient;
    
    /**
     * Activator
     * 
     * @param ctx context
     */
    @Activate
    public void activate(ComponentContext ctx) 
    {
    	this.gateway.getMachineAdapters();
    	for (IMachineAdapter adapter: this.gateway.getMachineAdapters()){
    		if (adapter.getInfo() != null ) {
    			_logger.info("hack: " + adapter.getInfo().getName()); //$NON-NLS-1$
    		}
    		else _logger.info("hack: adapter list empty"); //$NON-NLS-1$
    	}
    	
    	_logger.info("HACKH - hackathonAdapter Started, found adapters: "+ this.gateway.getMachineAdapters().size()); //$NON-NLS-1$
        this.info = new MachineAdapterInfo(ADAPTER_NAME, SERVICE_PID, ADAPTER_DESCRIPTION, ctx.getBundleContext()
                .getBundle().getVersion().toString());
        this.random = new Random();
        this.node = new PDataNode(getId(), ADAPTER_NODE, this.nodeUUID);
        _logger.info("HACKH - Node Created: adapterID: " + getId() + ", NodeName: " + ADAPTER_NODE +", NodeID: "+ this.nodeUUID ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        this.updateInterval=20000;
		String sub="HackathonSubscription";
		ArrayList<SampleDataNode> dataNodes= new ArrayList<SampleDataNode>();
		dataNodes.add(new SampleDataNode(this.uuid, "HackathonNode"));
		SampleDataSubscription dataSubscription = new SampleDataSubscription(this, sub, this.updateInterval,
                dataNodes, 
                this.httpClientFactory);
        this.dataSubscriptions.put(dataSubscription.getId(), dataSubscription);
        
        new Thread(dataSubscription).start();
    }
    
    @Reference
    public void setGateway(IMachineGateway gateway)
    {
        this.gateway = gateway;
    }

    /**
     * Deactivate
     * 
     * @param ctx context
     */
    @Deactivate
    public void deactivate(ComponentContext ctx)
    {
        //do nothing
    }

    @Override
    public UUID getId()
    {
        return this.uuid;
    }

    @Override
    public MachineAdapterInfo getInfo()
    {
        return this.info;
    }

    @Override
    public List<PDataNode> getNodes()
    {
        return Collections.singletonList(this.node);
    }

    @Override
    public MachineAdapterState getState()
    {
        return MachineAdapterState.Started;
    }

/*    @Override
    public PDataValue readData(UUID nodeId)
            throws MachineAdapterException
    {
        if ( !nodeId.equals(this.nodeUUID) )
        {
        	
            throw new MachineAdapterException("Node not found"); //$NON-NLS-1$
        }
        int numberValue = this.random.nextInt();
        PDataValue dataValue = new PDataValue(this.nodeUUID, new PEnvelope(numberValue));
        _logger.info("HACKH - Node value returned:  " + numberValue ); //$NON-NLS-1$
        
        return dataValue;
    }

    @Override
    public void writeData(UUID nodeId, PDataValue value)
            throws MachineAdapterException
    {
        if ( !nodeId.equals(this.nodeUUID) )
        {
            throw new MachineAdapterException("Node not found"); //$NON-NLS-1$
        }
    }
*/
   
    

    /*
     * Reads data from data cache. Data cache always contains latest values.
     */
    @Override
    public PDataValue readData(UUID nodeId)
            throws MachineAdapterException
    {
		_logger.info("Hack - readData: Called");
        if ( this.dataValueCache.containsKey(nodeId) )
        {
            return this.dataValueCache.get(nodeId);
        }

        // Do not return null.
        return new PDataValue(nodeId);
    }

    /*
     * Writes data value into data cache.
     */
    @Override
    public void writeData(UUID nodeId, PDataValue value)
            throws MachineAdapterException
    {
		_logger.info("Hack - writeData: Called");
        if ( this.dataValueCache.containsKey(nodeId) )
        {
            // Put data into cache. The value typically should be written to a device node.
            this.dataValueCache.put(nodeId, value);
        }
    }
    
	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter#addDataSubscription(com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription)
	 */
	@Override
	public UUID addDataSubscription(IDataSubscription arg0)
			throws MachineAdapterException {
		_logger.info("Hack - addDataSubscription: Called");
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter#addDataSubscriptionListener(java.util.UUID, com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener)
	 */
	@Override
	public void addDataSubscriptionListener(UUID arg0,
			IDataSubscriptionListener arg1) throws MachineAdapterException {
		_logger.info("Hack - addDataSubscriptionlistener: Called");
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter#getDataSubscription(java.util.UUID)
	 */
	@Override
	public IDataSubscription getDataSubscription(UUID arg0) {
		// TODO Auto-generated method stub
		_logger.info("Hack - getDataSubscription: Called");
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter#getSubscriptions()
	 */
	@Override
	public List<IDataSubscription> getSubscriptions() {
		// TODO Auto-generated method stub
		_logger.info("Hack - getSubscriptions: Called");
		
		return new ArrayList<IDataSubscription>(this.dataSubscriptions.values());
	}

	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter#removeDataSubscription(java.util.UUID)
	 */
	@Override
	public void removeDataSubscription(UUID arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter#removeDataSubscriptionListener(java.util.UUID, com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener)
	 */
	@Override
	public void removeDataSubscriptionListener(UUID arg0,
			IDataSubscriptionListener arg1) {
		// TODO Auto-generated method stub
		
	}
	
    protected void putData(List<PDataValue> values)
    {
        for (PDataValue value : values)
        {
            this.dataValueCache.put(value.getNodeId(), value);
        }
    }
    @SuppressWarnings("deprecation")
    @Reference
    public void setHttpClientService(IHttpClientFactory clientFactory)
    {
        /*
         * When modifying the IHttpClient, it will affect all other instances since the client uses a shared thread pool
         * Therefore when modifying (configuring HTTPS as an example), the best practice is to use the factory
         * and create your own private instance of IHttpClient instead of injecting the standard IHttpClient .
         */
    	_logger.info("setHttpClientService Called:");
        this.httpClientFactory = clientFactory;
        
    }
}
