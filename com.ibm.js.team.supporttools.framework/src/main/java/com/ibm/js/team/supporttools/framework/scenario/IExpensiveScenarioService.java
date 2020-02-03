/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.scenario;

/**
 * Interface for a basic Expensive Scenario Service that passes the information
 * required between service calls using a string.
 *
 */
public interface IExpensiveScenarioService {

	/**
	 * Start an expensive scenario.
	 * 
	 * @return The request body that is needed in the stop command
	 * 
	 * @throws Exception
	 */
	public String start() throws Exception;

	/**
	 * Stop the expensive scenario.
	 * 
	 * @param startRequestResponse
	 *            the response string from the start command
	 * @throws Exception
	 */
	public void stop(String startRequestResponse) throws Exception;

	/**
	 * @return the scenario name, never null
	 */
	public Object getScenarioName();

}
