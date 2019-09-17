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
	HashMap<String, ComponentStat> fComponents = new HashMap<String, ComponentStat>(2000);
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
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = fComponents.get(key);
			long hDepth = comp.getHierarchyDepth();
			cumulatedHierarchyDepth += hDepth;
			cumulatedFiles += comp.getCumulatedFiles();
			cumulatedFolders += comp.getCumulatedFolders();
			cumulatedFileSize += comp.getCumulatedFileSize();
			cumulatedFolderDepth += comp.getCumulatedFolderDepth();
			cumulatedFileDepth += comp.getCumulatedFileDepth();
			logger.info(comp.toString());
		}
		logger.info("\nSummary:\n\nCross Component Characteristics for connection '{}' across all {} components: \n",
				fConnectionName, noComponents);

		String message = "";
		message += " Files:\t\t " + cumulatedFiles + COLUMN_SEPERATOR;
		message += " File Size(avg):\t " + CalcUtil.divideLong(cumulatedFileSize, cumulatedFiles) + COLUMN_SEPERATOR;
		message += " File Size(sum):\t " + cumulatedFileSize + COLUMN_SEPERATOR;
		message += " File Depth(avg):\t " + CalcUtil.divideFloat(cumulatedFileDepth, cumulatedFiles) + COLUMN_SEPERATOR;
		message += " File Depth(sum):\t " + cumulatedFileDepth + COLUMN_SEPERATOR;
		message += "\n";
		message += " Folders:\t " + cumulatedFolders + COLUMN_SEPERATOR;
		message += " Files/Folder:\t\t " + CalcUtil.divideFloat(cumulatedFiles, cumulatedFolders) + COLUMN_SEPERATOR;
		message += " Folder Depth(avg):\t " + CalcUtil.divideFloat(cumulatedFolderDepth, cumulatedFolders)
				+ COLUMN_SEPERATOR;
		message += " Folder Depth(sum):\t " + cumulatedFolderDepth + COLUMN_SEPERATOR;
		message += " Hierarchical Depth(avg):\t " + CalcUtil.divideFloat(cumulatedHierarchyDepth, keys.size())
				+ COLUMN_SEPERATOR;
		message += " Hierarchical Depth(sum):\t " + cumulatedHierarchyDepth + COLUMN_SEPERATOR;
		message += "\n";
		logger.info(message);

	}

}
