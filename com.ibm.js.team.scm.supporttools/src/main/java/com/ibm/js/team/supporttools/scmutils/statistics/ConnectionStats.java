/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.statistics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scmutils.utils.CalcUtil;
import com.ibm.js.team.supporttools.scmutils.utils.POICellHelper;
import com.ibm.team.repository.common.UUID;

/**
 * Used to collect and calculate the statistics for a SCM Connection.
 *
 */
public class ConnectionStats {
	public static final String TABBED_COLUMN_SEPERATOR = "\t";
	public static final Logger logger = LoggerFactory.getLogger(ConnectionStats.class);
	HashMap<String, ComponentStat> fComponents = new HashMap<String, ComponentStat>(2500);
	private String fConnectionName;

	// Hierarchy
	private long cumulatedHierarchyDepth = 0;
	private long maxHierarchyDepth = 0;
	// Files
	private long cumulatedFiles = 0;
	private long cumulatedFileSize = 0;
	private long maxFileSize = 0;
	private long cumulatedFileDepth = 0;
	private long maxFileDepth = 0;
	// Folders
	private long cumulatedFolders = 0;
	private long cumulatedFolderDepth = 0;
	private long maxFolderDepth = 0;

	private int activeRow = -1;
	private boolean showExtensions = false;

	public void resetStats() {
		// Hierarchy
		this.cumulatedHierarchyDepth = 0;
		this.maxHierarchyDepth = 0;
		// Files
		this.cumulatedFiles = 0;
		this.cumulatedFileSize = 0;
		this.maxFileSize = 0;
		this.cumulatedFileDepth = 0;
		this.maxFileDepth = 0;
		// Folders
		this.cumulatedFolders = 0;
		this.cumulatedFolderDepth = 0;
		this.maxFolderDepth = 0;
	}

	/**
	 * @param connectionName
	 */
	public ConnectionStats(String connectionName) {
		fConnectionName = connectionName;
	}

	/**
	 * Resets and calculates the connection statistics without printing the
	 * data. Use the available getters and setters to get the data.
	 */
	public void calculateConnectionStats() {
		logger.info("Analyze connection '{}'" + fConnectionName);
		resetStats();
		Set<String> keys = getComponentStatisticsMap().keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = getComponentStatisticsMap().get(key);
			aggregateComponent(comp);
			logger.info(comp.toString());
		}
	}

	/**
	 * Aggregates the data for one component to the Connection statistics
	 * 
	 * @param comp
	 */
	private void aggregateComponent(ComponentStat comp) {

		// Hierarchy
		this.cumulatedHierarchyDepth += comp.getHierarchyDepth();
		// this.avgHierarchyDepth = 0; // calculated
		this.maxHierarchyDepth = CalcUtil.calcMax(this.maxHierarchyDepth, comp.getHierarchyDepth());

		// Files
		this.cumulatedFiles += comp.getCumulatedFiles();
		this.cumulatedFileSize += comp.getCumulatedFileSize();
		this.maxFileSize = CalcUtil.calcMax(this.maxFileSize, comp.getMaxFileSize());
		// this.averageFileSize = 0;
		this.cumulatedFileDepth += comp.getCumulatedFileDepth();
		this.maxFileDepth = CalcUtil.calcMax(this.maxFileDepth, comp.getMaxFileDepth());

		// Folders
		this.cumulatedFolders += comp.getCumulatedFolders();
		this.cumulatedFolderDepth += comp.getCumulatedFolderDepth();
		// this.averageFolderDepth = 0; // Calculated
		this.maxFolderDepth = CalcUtil.calcMax(this.maxFolderDepth, comp.getMaxFolderDepth());
	}

	public Workbook updateWorkBook(Workbook workBook) throws IOException {
		resetStats();
		logger.info("\nComponent characteristics for connection '{}' : ", fConnectionName);
		logger.info("Creating sheets...");

		String connectionSheetSafeName = WorkbookUtil.createSafeSheetName("0 - Connection");
		Sheet connectionSheet = workBook.createSheet(connectionSheetSafeName);

		String componentSheetSafeName = WorkbookUtil.createSafeSheetName("1 - Components");
		Sheet componentSheet = workBook.createSheet(componentSheetSafeName);

		POICellHelper ch = new POICellHelper(workBook);

		Row componentHeader = componentSheet.createRow(getNextActiveRow());
		
		componentHeader.createCell(1).setCellValue(ch.boldFace("Component"));
		componentHeader.createCell(2).setCellValue(ch.boldFace("Hierarchy Depth"));
		componentHeader.createCell(3).setCellValue(ch.boldFace("Folders (sum)"));
		componentHeader.createCell(4).setCellValue(ch.boldFace("Files (sum)"));
		componentHeader.createCell(5).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		componentHeader.createCell(6).setCellValue(ch.boldFace("Files/Folder"));
		componentHeader.createCell(7).setCellValue(ch.boldFace("Folder Depth(avg)"));
		componentHeader.createCell(8).setCellValue(ch.boldFace("Folder Depth(max)"));
		componentHeader.createCell(9).setCellValue(ch.boldFace("Folder Depth(sum)"));
		componentHeader.createCell(10).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		componentHeader.createCell(11).setCellValue(ch.boldFace("log(e)"));
		componentHeader.createCell(12).setCellValue(ch.boldFace("Max"));
		componentHeader.createCell(13).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		componentHeader.createCell(14).setCellValue(ch.boldFace("File Size(avg)"));
		componentHeader.createCell(15).setCellValue(ch.boldFace("File Size(max)"));
		componentHeader.createCell(16).setCellValue(ch.boldFace("File Size(sum)"));
		componentHeader.createCell(17).setCellValue(ch.boldFace("File Depth(avg)"));
		componentHeader.createCell(18).setCellValue(ch.boldFace("File Depth(max)"));
		componentHeader.createCell(19).setCellValue(ch.boldFace("File Depth(sum)"));
		if (showExtensions) {
			componentHeader.createCell(20).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
			componentHeader.createCell(21).setCellValue(ch.boldFace("Extensions"));
			componentHeader.createCell(22).setCellValue(ch.boldFace("Extension Details"));
		}
		
		// Group header
		componentHeader.createCell(0).setCellValue(ch.boldFace("Component Stats"));
		componentHeader.createCell(5).setCellValue(ch.boldFace("Folder Stats"));
		componentHeader.createCell(10).setCellValue(ch.boldFace("Folder Depth Limits"));
		componentHeader.createCell(13).setCellValue(ch.boldFace("File Stats"));

		Set<String> keys = getComponentStatisticsMap().keySet();
		logger.info("Components: {}\n", getNoComponents());

		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = getComponentStatisticsMap().get(key);
			Row row = componentSheet.createRow(getNextActiveRow());
			row.createCell(1).setCellValue(comp.getComponentName());
			ch.setNumber(row.createCell(2), comp.getComponentHierarchyDepth());

			ch.setNumber(row.createCell(3), comp.getNoFolders());
			ch.setNumber(row.createCell(4), comp.getNoFiles());

			ch.setNumberP2(row.createCell(6), CalcUtil.divide(comp.getCumulatedFiles(), comp.getCumulatedFolders()));
			ch.setNumberP2(row.createCell(7),
					CalcUtil.divide(comp.getCumulatedFolderDepth(), comp.getCumulatedFolders()));
			ch.setNumber(row.createCell(8), comp.getMaxFolderDepth());
			ch.setNumber(row.createCell(9), comp.getCumulatedFolderDepth());

			ch.setNumberP2(row.createCell(11), Math.log(comp.getCumulatedFolders()));
			ch.setNumber(row.createCell(12), comp.getCumulatedFolders());

			ch.setNumberP2(row.createCell(14), CalcUtil.divide(comp.getCumulatedFileSize(), comp.getCumulatedFiles()));
			ch.setNumber(row.createCell(15), comp.getMaxFileSize());
			ch.setNumber(row.createCell(16), comp.getCumulatedFileSize());
			ch.setNumberP2(row.createCell(17), CalcUtil.divide(comp.getCumulatedFileDepth(), comp.getCumulatedFiles()));
			ch.setNumber(row.createCell(18), comp.getMaxFileDepth());
			ch.setNumber(row.createCell(19), comp.getCumulatedFileDepth());

			if (showExtensions) {
				IExtensions ext = comp.getExtensions();
				ch.setNumber(row.createCell(21), ext.getNoExtensions());
				ch.setText(row.createCell(22), ext.getExtensionsCompressed());
			}
			aggregateComponent(comp);
			logger.info(comp.toString());
		}
		logger.info("\nSummary:\n\nCross Component Characteristics for connection '{}' across all {} components: \n",
				getConnectionName(), getNoComponents());
		String message = "";
		message += " Hierarchy Depth(avg):\t " + CalcUtil.roundPrecision2(getAverageHierarchyDepth()).toString();
		message += " Hierarchy Depth(sum):\t " + getCumulatedHierarchyDepth();
		message += " Hierarchy Depth(max):\t " + getMaxHierarchyDepth() + "\n";
		
		logger.info("\nComponent characteristics for connection '{}' : ", fConnectionName);
		logger.info("Output cumulative data on sheet '{}'sheet...", connectionSheetSafeName);
		
		Row connectionHeader = connectionSheet.createRow(0);

		connectionHeader.createCell(1).setCellValue(ch.boldFace("Connection"));
		connectionHeader.createCell(2).setCellValue(ch.boldFace("Components"));
		connectionHeader.createCell(3).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		connectionHeader.createCell(4).setCellValue(ch.boldFace("Hierarchy Depth (avg)"));
		connectionHeader.createCell(5).setCellValue(ch.boldFace("Hierarchy Depth (sum)"));
		connectionHeader.createCell(6).setCellValue(ch.boldFace("Hierarchy Depth (max)"));
		connectionHeader.createCell(7).setCellValue(ch.boldFace("Folders (sum)"));
		connectionHeader.createCell(8).setCellValue(ch.boldFace("Files (sum)"));
		connectionHeader.createCell(9).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		connectionHeader.createCell(10).setCellValue(ch.boldFace("Files/Folder"));
		connectionHeader.createCell(11).setCellValue(ch.boldFace("Folder Depth(avg)"));
		connectionHeader.createCell(12).setCellValue(ch.boldFace("Folder Depth(max)"));
		connectionHeader.createCell(13).setCellValue(ch.boldFace("Folder Depth(sum)"));
		connectionHeader.createCell(14).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		connectionHeader.createCell(15).setCellValue(ch.boldFace("log(e)"));
		connectionHeader.createCell(16).setCellValue(ch.boldFace("Max"));
		connectionHeader.createCell(17).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		connectionHeader.createCell(18).setCellValue(ch.boldFace("File Size(avg)"));
		connectionHeader.createCell(19).setCellValue(ch.boldFace("File Size(max)"));
		connectionHeader.createCell(20).setCellValue(ch.boldFace("File Size(sum)"));
		connectionHeader.createCell(21).setCellValue(ch.boldFace("File Depth(avg)"));
		connectionHeader.createCell(22).setCellValue(ch.boldFace("File Depth(max)"));
		connectionHeader.createCell(23).setCellValue(ch.boldFace("File Depth(sum)"));
		if (showExtensions) {
			connectionHeader.createCell(24).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
			connectionHeader.createCell(25).setCellValue(ch.boldFace("Extensions"));
			connectionHeader.createCell(26).setCellValue(ch.boldFace("Extension Details"));
		}

		// Group header
		connectionHeader.createCell(0).setCellValue(ch.boldFace("Connection Stats"));
		connectionHeader.createCell(3).setCellValue(ch.boldFace("Component Stats"));
		connectionHeader.createCell(9).setCellValue(ch.boldFace("Folder Stats"));
		connectionHeader.createCell(14).setCellValue(ch.boldFace("Folder Depth Limits"));
		connectionHeader.createCell(17).setCellValue(ch.boldFace("File Stats"));

		
		// Row for connection data
		Row connectionRow = connectionSheet.createRow(1);
		connectionRow.createCell(1).setCellValue(getConnectionName());
		connectionRow.createCell(2).setCellValue(new Double(getNoComponents()));

		ch.setNumberP2(connectionRow.createCell(4), getAverageHierarchyDepth());
		ch.setNumber(connectionRow.createCell(5), getCumulatedHierarchyDepth());
		ch.setNumber(connectionRow.createCell(6), getMaxHierarchyDepth());
		ch.setNumber(connectionRow.createCell(7), getCumulatedFolders());
		ch.setNumber(connectionRow.createCell(8), getCumulatedFiles());

		ch.setNumberP2(connectionRow.createCell(10), getAverageFilesPerFolder());
		ch.setNumberP2(connectionRow.createCell(11), getAverageFolderDepth());
		ch.setNumber(connectionRow.createCell(12), getMaxFolderDepth());
		ch.setNumber(connectionRow.createCell(13), getCumulatedFolderDepth());

		ch.setNumberP2(connectionRow.createCell(15), Math.log(getCumulatedFolders()));
		ch.setNumber(connectionRow.createCell(16), getCumulatedFolders());

		ch.setNumberP2(connectionRow.createCell(18), getAverageFileSize());
		ch.setNumber(connectionRow.createCell(19), getMaxFileSize());
		ch.setNumber(connectionRow.createCell(20), getCumulatedFileSize());
		ch.setNumberP2(connectionRow.createCell(21), getAverageFileDepth());
		ch.setNumber(connectionRow.createCell(22), getMaxFileDepth());
		ch.setNumber(connectionRow.createCell(23), getCumulatedFileDepth());

		if (showExtensions) {
			// Not available at this level at the moment
		}
		message += PrintStat.getFileAndFolderStatistics(getCumulatedFolders(), getCumulatedFolderDepth(),
				getMaxFolderDepth(), getCumulatedFiles(), getMaxFileSize(), getCumulatedFileSize(), getMaxFileDepth(),
				getCumulatedFileDepth());
		message += "\n";
		logger.info(message);
		for (int i = 0; i < 27; i++) {
			connectionSheet.autoSizeColumn(i);
		}
		for (int i = 0; i < 27; i++) {
			componentSheet.autoSizeColumn(i);
		}
		return workBook;
	}
	
	public HashMap<String, ComponentStat> getComponentStatisticsMap() {
		return fComponents;
	}

	public String getConnectionName() {
		return fConnectionName;
	}

	public int getNoComponents() {
		return getComponentStatisticsMap().keySet().size();
	}

	public long getCumulatedHierarchyDepth() {
		return cumulatedHierarchyDepth;
	}

	public double getAverageHierarchyDepth() {
		Double value = CalcUtil.divide(getCumulatedHierarchyDepth(), getNoComponents());
		if (value != null) {
			return value.doubleValue();
		}
		// TODO
		return 0;
	}

	public long getMaxHierarchyDepth() {
		return maxHierarchyDepth;
	}

	public long getCumulatedFiles() {
		return cumulatedFiles;
	}

	public long getCumulatedFileSize() {
		return cumulatedFileSize;
	}

	public double getAverageFileSize() {
		Double value = CalcUtil.divide(getCumulatedFileSize(), getCumulatedFiles());
		if (value != null) {
			return value.doubleValue();
		}
		// TODO
		return 0;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public long getCumulatedFileDepth() {
		return cumulatedFileDepth;
	}

	public double getAverageFileDepth() {
		Double value = CalcUtil.divide(getCumulatedFileDepth(), getCumulatedFiles());
		if (null != value) {
			return value.doubleValue();
		}
		// TODO
		return 0;
	}

	public long getMaxFileDepth() {
		return maxFileDepth;
	}

	public long getCumulatedFolders() {
		return cumulatedFolders;
	}

	public double getAverageFilesPerFolder() {
		Double value = CalcUtil.divide(getCumulatedFiles(), getCumulatedFolders());
		if (null != value) {
			return value.doubleValue();
		}
		// TODO
		return 0;
	}

	public long getCumulatedFolderDepth() {
		return cumulatedFolderDepth;
	}

	public double getAverageFolderDepth() {
		Double value = CalcUtil.divide(getCumulatedFolderDepth(), getCumulatedFolders());
		if (null != value) {
			return value.doubleValue();
		}
		// TODO
		return 0;
	}

	public long getMaxFolderDepth() {
		return maxFolderDepth;
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
		getComponentStatisticsMap().put(uuid.toString(), component);
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
		return getComponentStatisticsMap().get(itemId);
	}

	@SuppressWarnings("unused")
	private int getActiveRow() {
		return activeRow;
	}

	private int getNextActiveRow() {
		return ++this.activeRow;
	}

}
