/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.hackathon.webservice;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import aQute.bnd.annotation.component.Component;

@SuppressWarnings("javadoc")
interface IHttpsServer
{
    public static final String PATH = "/testserver"; //$NON-NLS-1$
}

/**
 * 
 */
@Component(provide = HttpServer.class)
@Path(IHttpsServer.PATH)
public class HttpServer
        implements IHttpsServer
{
  
    /**
     * @param json -
     */
    @PUT
    @Path("/addSample")
    @Consumes("application/json")
    public void createSample(final String json){
    	SensorDataDao.addSamples(json);
    }
    
    /**
     * @return -
     */
    @GET
    @Path("/getAllSampleAndCleanAll")
    @Produces("application/json")
    public List<String> getAllSamples()
    {
        return SensorDataDao.getAllSamplesAndCleanAll();
    }
}
