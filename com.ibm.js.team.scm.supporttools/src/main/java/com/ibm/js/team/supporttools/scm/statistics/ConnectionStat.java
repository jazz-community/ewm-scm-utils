package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.repository.common.UUID;

public class ConnectionStat {
	public static final String COLUMN_SEPERATOR = " \t";
	public static final Logger logger = LoggerFactory.getLogger(ConnectionStat.class);
	HashMap<String, ComponentStat> fComponents = new HashMap<String, ComponentStat>(2500);
	private String fConnectionName;

	public ConnectionStat(String connectionName) {
		fConnectionName = connectionName;
	}

	public ComponentStat getNewComponent(UUID uuid) {
		return getNewComponent(uuid.toString());
	}

	public ComponentStat getNewComponent(String uuid) {
		ComponentStat component = new ComponentStat(uuid);
		fComponents.put(uuid.toString(), component);
		return component;
	}

	public ComponentStat getComponentStat(UUID itemId) {
		return getComponentStat(itemId.toString());
	}

	public ComponentStat getComponentStat(String itemId) {
		return fComponents.get(itemId);
	}

	public void log() {
		logger.info("\nComponent characteristics for connection '{}' : ", fConnectionName);
		Set<String> keys = fComponents.keySet();
		int noComponents = keys.size();
		logger.info("Components: {}\n", noComponents);
		long cumulatedHierarchyDepth = 0;
		long cumulatedFiles = 0;
		long cumulatedFolders = 0;
		long cumulatedFileSize = 0;
		long cumulatedFolderDepth = 0;
		long cumulatedFileDepth = 0;
		long maxFolderDepth = 0;
		long maxFileDepth = 0;
		long maxFileSize = 0;
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = fComponents.get(key);
			long hDepth = comp.getHierarchyDepth();
			cumulatedHierarchyDepth += hDepth;
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
			logger.info(comp.toString());
		}
		logger.info("\nSummary:\n\nCross Component Characteristics for connection '{}' across all {} components: \n",
				fConnectionName, noComponents);

		String message = "";
		message += " Hierarchy Depth(avg):\t "
				+ CalcUtil.divideFloatWithPrecision2AsString(cumulatedHierarchyDepth, keys.size()) + COLUMN_SEPERATOR;
		message += " Hierarchy Depth(sum):\t " + cumulatedHierarchyDepth + "\n";
		message += PrintStat.getFileAndFolderStatistics(cumulatedFolders, cumulatedFolderDepth, maxFolderDepth,
				cumulatedFiles, maxFileSize, cumulatedFileSize, maxFileDepth, cumulatedFileDepth);
		message += "\n";
		logger.info(message);

	}

}
