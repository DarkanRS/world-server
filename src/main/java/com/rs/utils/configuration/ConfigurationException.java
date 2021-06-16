package com.rs.utils.configuration;

/**
 * An exception thrown if the value of the configuration is not the appropriate
 * type
 * 
 * @author Nikki
 * 
 */
@SuppressWarnings("serial")
public class ConfigurationException extends IllegalArgumentException {

	public ConfigurationException(String string) {
		super(string);
	}
}