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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Resource bundle class
 */
public final class Messages
{
    private static final String         BUNDLE_NAME     = "sensorwebservice";                 //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages()
    {
        //disallow construction
    }

    /**
     * Get a message string
     * 
     * @param key the key identifying the message
     * @return A string containing the message
     */
    public static String getString(String key)
    {
        try
        {
            return RESOURCE_BUNDLE.getString(key);
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }

    /**
     * Build a formated string from the resource bundle.
     * 
     * @param key the key into the resource bundle that has the formated string.
     * @param args an array of arguments
     * @return the formated string with the arguments in-line.
     */
    public static String getString(String key, Object... args)
    {
        try
        {
            MessageFormat formatter = new MessageFormat(RESOURCE_BUNDLE.getString(key));
            return formatter.format(args);
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }
}
