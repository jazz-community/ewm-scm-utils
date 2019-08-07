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
		fConnectionName=connectionName;
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
		Set<String> keys = fComponents.keySet();
		logger.info("Components: {}", keys.size());
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
			cumulatedFiles+=comp.getCumulatedFiles();
			cumulatedFolders+=comp.getCumulatedFolders();			
			cumulatedFileSize+=comp.getCumulatedFileSize();			
			cumulatedFolderDepth+=comp.getCumulatedFolderDepth();
			cumulatedFileDepth+=comp.getCumulatedFileDepth();
			logger.info(comp.toString());
			
		}

		logger.info("Workspace {}", fConnectionName);
//		logger.info("Average Hierarchical Depth {}", CalcUtil.divideFloat(cumulatedHierarchyDepth, keys.size()));
		// Average files/component
		// average folders/component
		// Average Filesize/component
		// Average Depth
		// # files
		// # folders
		// av file depth
		// Av folder depth
		String message = "";
		message += " CumulatedFiles:\t " + cumulatedFiles + COLUMN_SEPERATOR;
		message += " CumulatedFileSize:\t " + cumulatedFileSize + COLUMN_SEPERATOR;
		message += " averageFileSize:\t " + CalcUtil.divideLong(cumulatedFileSize,cumulatedFiles)+ COLUMN_SEPERATOR;
		message += " cumulatedFileDepth:\t " + cumulatedFileDepth+ COLUMN_SEPERATOR;
		message += " averageFileDepth:\t " + CalcUtil.divideFloat(cumulatedFileDepth,cumulatedFiles)+ " \n";		
		message += " CumulatedFolders:\t " + cumulatedFolders + COLUMN_SEPERATOR;
		message += " cumulatedFolderDepth:\t " + cumulatedFolderDepth + COLUMN_SEPERATOR;
		message += " averageFolderDepth:\t " + CalcUtil.divideFloat(cumulatedFolderDepth,cumulatedFolders)+ COLUMN_SEPERATOR;		
		message += " averageFilesPerFolder:\t " + CalcUtil.divideFloat(cumulatedFiles,cumulatedFolders)+ COLUMN_SEPERATOR;		
		message += " cumulatedHierarchicalDepth:\t " + cumulatedHierarchyDepth + COLUMN_SEPERATOR;
		message += " averageHierarchicalDepth:\t " + CalcUtil.divideFloat(cumulatedHierarchyDepth, keys.size())+ " \n";
		logger.info(message);
		
	}
	


}
