/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.repository.common.UUID;

public class ConnectionStats {
	public static final String COLUMN_SEPERATOR = " \t";
	public static final Logger logger = LoggerFactory.getLogger(ConnectionStats.class);
	HashMap<String, ComponentStat> fComponents = new HashMap<String, ComponentStat>(2500);
	private String fConnectionName;
	private int noComponents = 0;
	private long cumulatedHierarchyDepth = 0;
	private long cumulatedFiles = 0;
	private long cumulatedFolders = 0;
	private long cumulatedFileSize = 0;
	private long cumulatedFolderDepth = 0;
	private long cumulatedFileDepth = 0;
	private long maxFolderDepth = 0;
	private long maxFileDepth = 0;
	private long maxFileSize = 0;
	private float avgHierarchyDepth = 0;

	public HashMap<String, ComponentStat> getComponents() {
		return fComponents;
	}

	public String getfConnectionName() {
		return fConnectionName;
	}

	public int getNoComponents() {
		return noComponents;
	}

	public long getCumulatedHierarchyDepth() {
		return cumulatedHierarchyDepth;
	}

	public long getCumulatedFiles() {
		return cumulatedFiles;
	}

	public long getCumulatedFolders() {
		return cumulatedFolders;
	}

	public long getCumulatedFileSize() {
		return cumulatedFileSize;
	}

	public long getCumulatedFolderDepth() {
		return cumulatedFolderDepth;
	}

	public long getCumulatedFileDepth() {
		return cumulatedFileDepth;
	}

	public long getMaxFolderDepth() {
		return maxFolderDepth;
	}

	public long getMaxFileDepth() {
		return maxFileDepth;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public float getAvgHierarchyDepth() {
		return avgHierarchyDepth;
	}

	public void resetStats() {
		noComponents = 0;
		avgHierarchyDepth = 0;
		cumulatedHierarchyDepth = 0;
		cumulatedFiles = 0;
		cumulatedFolders = 0;
		cumulatedFileSize = 0;
		cumulatedFolderDepth = 0;
		cumulatedFileDepth = 0;
		maxFolderDepth = 0;
		maxFileDepth = 0;
		maxFileSize = 0;
	}

	/**
	 * @param connectionName
	 */
	public ConnectionStats(String connectionName) {
		fConnectionName = connectionName;
	}

	/**
	 * @param uuid
	 * @return
	 */
	public ComponentStat getNewComponent(UUID uuid) {
		return getNewComponent(uuid.toString());
	}

	/**
	 * @param uuid
	 * @return
	 */
	public ComponentStat getNewComponent(String uuid) {
		ComponentStat component = new ComponentStat(uuid);
		fComponents.put(uuid.toString(), component);
		return component;
	}

	/**
	 * @param itemId
	 * @return
	 */
	public ComponentStat getComponentStat(UUID itemId) {
		return getComponentStat(itemId.toString());
	}

	/**
	 * @param itemId
	 * @return
	 */
	public ComponentStat getComponentStat(String itemId) {
		return fComponents.get(itemId);
	}

	/**
	 * Calculates the connection statistics with printing the data.
	 */
	public void printConnectionStatistics() {
		resetStats();
		logger.info("\nComponent characteristics for connection '{}' : ", fConnectionName);
		Set<String> keys = fComponents.keySet();
		noComponents = keys.size();
		logger.info("Components: {}\n", noComponents);
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = fComponents.get(key);
			aggregateComponent(comp);
			logger.info(comp.toString());
		}
		logger.info("\nSummary:\n\nCross Component Characteristics for connection '{}' across all {} components: \n",
				fConnectionName, noComponents);

		avgHierarchyDepth = CalcUtil.divideFloat(cumulatedHierarchyDepth, keys.size());
		String message = "";
		message += " Hierarchy Depth(avg):\t " + CalcUtil.formatPrecision2(avgHierarchyDepth) + COLUMN_SEPERATOR;
		message += " Hierarchy Depth(sum):\t " + cumulatedHierarchyDepth + "\n";
		message += PrintStat.getFileAndFolderStatistics(cumulatedFolders, cumulatedFolderDepth, maxFolderDepth,
				cumulatedFiles, maxFileSize, cumulatedFileSize, maxFileDepth, cumulatedFileDepth);
		message += "\n";
		logger.info(message);
	}

	/**
	 * Resets and calculates the connection statistics without printing the
	 * data. Use the available getters and setters to get the data.
	 */
	public void calculateConnectionStats() {
		logger.info("Analyze connection '{}'" + fConnectionName);
		resetStats();
		Set<String> keys = fComponents.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = fComponents.get(key);
			aggregateComponent(comp);
			logger.info(comp.toString());
		}
		avgHierarchyDepth = CalcUtil.divideFloat(cumulatedHierarchyDepth, keys.size());
	}

	/**
	 * Aggregates the data for one component to the Connection statistics
	 * 
	 * @param comp
	 */
	private void aggregateComponent(ComponentStat comp) {
		cumulatedHierarchyDepth += comp.getHierarchyDepth();
		cumulatedFiles += comp.getCumulatedFiles();
		cumulatedFolders += comp.getCumulatedFolders();
		cumulatedFileSize += comp.getCumulatedFileSize();
		cumulatedFolderDepth += comp.getCumulatedFolderDepth();
		if (maxFolderDepth < comp.getMaxFolderDepth()) {
			maxFolderDepth = comp.getMaxFolderDepth();
		}
		cumulatedFileDepth += comp.getCumulatedFileDepth();
		if (maxFileDepth < comp.getMaxFileDepth()) {
			maxFileDepth = comp.getMaxFileDepth();
		}
		if (maxFileSize < comp.getMaxFileSize()) {
			maxFileSize = comp.getMaxFileSize();
		}
	}

}
