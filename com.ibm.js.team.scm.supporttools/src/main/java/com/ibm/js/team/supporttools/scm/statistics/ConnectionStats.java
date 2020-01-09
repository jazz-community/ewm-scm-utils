/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

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

import com.ibm.js.team.supporttools.scm.utils.CalcUtil;
import com.ibm.js.team.supporttools.scm.utils.POICellHelper;
import com.ibm.team.repository.common.UUID;

public class ConnectionStats {
	public static final String TABBED_COLUMN_SEPERATOR = " \t";
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
	private double avgHierarchyDepth = 0;

	private int activeRow = 0;
	private boolean showExtensions=false;

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

	public double getAvgHierarchyDepth() {
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

	public Workbook updateWorkBook(Workbook workBook) throws IOException {
		resetStats();
		logger.info("\nComponent characteristics for connection '{}' : ", fConnectionName);
		logger.info("Creating sheet...");

		String safeName = WorkbookUtil.createSafeSheetName("0 - Connection");
		Sheet sheet = workBook.createSheet(safeName);

		POICellHelper ch = new POICellHelper(workBook);
		Row groupheader = sheet.createRow(getActiveRow());

		groupheader.createCell(4).setCellValue(ch.boldFace("Component Stats"));
		groupheader.createCell(9).setCellValue(ch.boldFace("Folder Stats"));
		groupheader.createCell(14).setCellValue(ch.boldFace("Folder Depth Limits"));
		groupheader.createCell(17).setCellValue(ch.boldFace("File Stats"));

		Row header1 = sheet.createRow(getNextActiveRow());
		header1.createCell(1).setCellValue(ch.boldFace("Connection"));
		header1.createCell(2).setCellValue(ch.boldFace("Components"));
		header1.createCell(3).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header1.createCell(4).setCellValue(ch.boldFace("Hierarchy Depth (avg)"));
		header1.createCell(5).setCellValue(ch.boldFace("Hierarchy Depth (sum)"));
		header1.createCell(6).setCellValue(ch.boldFace("Folders"));
		header1.createCell(7).setCellValue(ch.boldFace("Files"));
		header1.createCell(8).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header1.createCell(9).setCellValue(ch.boldFace("Files/Folder"));
		header1.createCell(10).setCellValue(ch.boldFace("Folder Depth(avg)"));
		header1.createCell(11).setCellValue(ch.boldFace("Folder Depth(max)"));
		header1.createCell(12).setCellValue(ch.boldFace("Folder Depth(sum)"));
		header1.createCell(13).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header1.createCell(14).setCellValue(ch.boldFace("log(e)"));
		header1.createCell(15).setCellValue(ch.boldFace("Max"));
		header1.createCell(16).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header1.createCell(17).setCellValue(ch.boldFace("File Size(avg) bytes"));
		header1.createCell(18).setCellValue(ch.boldFace("File Size(max)"));
		header1.createCell(19).setCellValue(ch.boldFace("File Size(sum)"));
		header1.createCell(20).setCellValue(ch.boldFace("File Depth(avg)"));
		header1.createCell(21).setCellValue(ch.boldFace("File Depth(max)"));
		header1.createCell(22).setCellValue(ch.boldFace("File Depth(sum)"));
		if(showExtensions){
			header1.createCell(23).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
			header1.createCell(24).setCellValue(ch.boldFace("Extensions"));
			header1.createCell(25).setCellValue(ch.boldFace("Extension Details"));
		}

		// Row for connection data
		Row connectionRow = sheet.createRow(getNextActiveRow());
		connectionRow.createCell(1).setCellValue(getfConnectionName());

		// Distance
		sheet.createRow(getNextActiveRow());
		sheet.createRow(getNextActiveRow());

		Row groupheader2 = sheet.createRow(getNextActiveRow());

		groupheader2.createCell(4).setCellValue(ch.boldFace("Component Stats"));
		groupheader2.createCell(9).setCellValue(ch.boldFace("Folder Stats"));
		groupheader2.createCell(14).setCellValue(ch.boldFace("Folder Depth Limits"));
		groupheader2.createCell(17).setCellValue(ch.boldFace("File Stats"));

		Row header2 = sheet.createRow(getNextActiveRow());
		header2.createCell(1).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(2).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(3).setCellValue(ch.boldFace("Component"));
		header2.createCell(4).setCellValue(ch.boldFace("Hierarchy Depth"));
		header2.createCell(5).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(6).setCellValue(ch.boldFace("Folders"));
		header2.createCell(7).setCellValue(ch.boldFace("Files"));
		header2.createCell(8).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(9).setCellValue(ch.boldFace("Files/Folder"));
		header2.createCell(10).setCellValue(ch.boldFace("Folder Depth(avg)"));
		header2.createCell(11).setCellValue(ch.boldFace("Folder Depth(max)"));
		header2.createCell(12).setCellValue(ch.boldFace("Folder Depth(sum)"));
		header2.createCell(13).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(14).setCellValue(ch.boldFace("log(e)"));
		header2.createCell(15).setCellValue(ch.boldFace("Max"));
		header2.createCell(16).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(17).setCellValue(ch.boldFace("File Size(avg) bytes"));
		header2.createCell(18).setCellValue(ch.boldFace("File Size(max)"));
		header2.createCell(19).setCellValue(ch.boldFace("File Size(sum)"));
		header2.createCell(20).setCellValue(ch.boldFace("File Depth(avg)"));
		header2.createCell(21).setCellValue(ch.boldFace("File Depth(max)"));
		header2.createCell(22).setCellValue(ch.boldFace("File Depth(sum)"));
		if(showExtensions){
			header2.createCell(23).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
			header2.createCell(24).setCellValue(ch.boldFace("Extensions"));
			header2.createCell(25).setCellValue(ch.boldFace("Extension Details"));
		}
		
		Set<String> keys = fComponents.keySet();
		noComponents = keys.size();
		logger.info("Components: {}\n", noComponents);
		connectionRow.createCell(2).setCellValue(new Double(noComponents));
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			ComponentStat comp = fComponents.get(key);
			Row row = sheet.createRow(getNextActiveRow());
			row.createCell(3).setCellValue(comp.getComponentName());
			ch.setNumber(row.createCell(4), comp.getComponentHierarchyDepth());
			ch.setNumber(row.createCell(4), comp.getComponentHierarchyDepth());

			ch.setNumber(row.createCell(6), comp.getNoFolders());
			ch.setNumber(row.createCell(7), comp.getNoFiles());

			ch.setNumberP2(row.createCell(9), CalcUtil.divide(comp.getCumulatedFiles(), comp.getCumulatedFolders()));
			ch.setNumberP2(row.createCell(10),
					CalcUtil.divide(comp.getCumulatedFolderDepth(), comp.getCumulatedFolders()));
			ch.setNumber(row.createCell(11), comp.getMaxFolderDepth());
			ch.setNumber(row.createCell(12), comp.getCumulatedFolderDepth());

			ch.setNumberP2(row.createCell(14), Math.log(comp.getCumulatedFolders()));
			ch.setNumber(row.createCell(15), comp.getCumulatedFolders());

			ch.setNumberP2(row.createCell(17), CalcUtil.divide(comp.getCumulatedFileSize(), comp.getCumulatedFiles()));
			ch.setNumber(row.createCell(18), comp.getMaxFileSize());
			ch.setNumber(row.createCell(19), comp.getCumulatedFileSize());
			ch.setNumberP2(row.createCell(20), CalcUtil.divide(comp.getCumulatedFileDepth(), comp.getCumulatedFiles()));
			ch.setNumber(row.createCell(21), comp.getMaxFileDepth());
			ch.setNumber(row.createCell(22), comp.getCumulatedFileDepth());

			if(showExtensions){
				IExtensions ext = comp.getExtensions();
				ch.setNumber(row.createCell(24), ext.getNoExtensions());
				ch.setText(row.createCell(25), ext.getExtensionsCompressed());
			}
			aggregateComponent(comp);
			logger.info(comp.toString());
		}
		logger.info("\nSummary:\n\nCross Component Characteristics for connection '{}' across all {} components: \n",
				fConnectionName, noComponents);
		Double value = CalcUtil.divide(cumulatedHierarchyDepth, noComponents);
		if (value != null) {
			avgHierarchyDepth = value.doubleValue();
		}
		String message = "";
		message += " Hierarchy Depth(avg):\t " + CalcUtil.roundPrecision2(avgHierarchyDepth).toString() + "";
		message += " Hierarchy Depth(sum):\t " + cumulatedHierarchyDepth + "\n";

		ch.setNumberP2(connectionRow.createCell(4), avgHierarchyDepth);
		ch.setNumber(connectionRow.createCell(5), cumulatedHierarchyDepth);
		ch.setNumber(connectionRow.createCell(6), cumulatedFolders);
		ch.setNumber(connectionRow.createCell(7), cumulatedFiles);

		ch.setNumberP2(connectionRow.createCell(9), CalcUtil.divide(cumulatedFiles, cumulatedFolders));
		ch.setNumberP2(connectionRow.createCell(10), CalcUtil.divide(cumulatedFolderDepth, cumulatedFolders));
		ch.setNumber(connectionRow.createCell(11), maxFolderDepth);
		ch.setNumber(connectionRow.createCell(12), cumulatedFolderDepth);

		ch.setNumberP2(connectionRow.createCell(14), Math.log(cumulatedFolders));
		ch.setNumber(connectionRow.createCell(15), cumulatedFolders);

		ch.setNumberP2(connectionRow.createCell(17), CalcUtil.divide(cumulatedFileSize, cumulatedFiles));
		ch.setNumber(connectionRow.createCell(18), maxFileSize);
		ch.setNumber(connectionRow.createCell(19), cumulatedFileSize);
		ch.setNumberP2(connectionRow.createCell(20), CalcUtil.divide(cumulatedFileDepth, cumulatedFiles));
		ch.setNumber(connectionRow.createCell(21), maxFileDepth);
		ch.setNumber(connectionRow.createCell(22), cumulatedFileDepth);
		if(showExtensions){
			// Not available at this level at the moment
		}
		message += PrintStat.getFileAndFolderStatistics(cumulatedFolders, cumulatedFolderDepth, maxFolderDepth,
				cumulatedFiles, maxFileSize, cumulatedFileSize, maxFileDepth, cumulatedFileDepth);
		message += "\n";
		logger.info(message);
		for (int i = 0; i < 26; i++) {
			sheet.autoSizeColumn(i);
		}
		return workBook;
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

		Double value = CalcUtil.divide(cumulatedHierarchyDepth, keys.size());
		if (value != null) {
			avgHierarchyDepth = value.doubleValue();
		}
		String message = POICellHelper.XLS_COLUMN_SEPARATOR;
		message += " Hierarchy Depth(avg):\t " + CalcUtil.roundPrecision2(avgHierarchyDepth).toString()
				+ TABBED_COLUMN_SEPERATOR;
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
		Double value = CalcUtil.divide(cumulatedHierarchyDepth, keys.size());
		if (value != null) {
			avgHierarchyDepth = value.doubleValue();
		}
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

	private int getActiveRow() {
		return activeRow;
	}

	private int getNextActiveRow() {
		return ++this.activeRow;
	}

}
