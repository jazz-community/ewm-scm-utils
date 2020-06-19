/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.statistics.sizerange.RangeStats;
import com.ibm.js.team.supporttools.scm.utils.CalcUtil;
import com.ibm.js.team.supporttools.scm.utils.POICellHelper;

/**
 * Analyzes file and folder structure for one sandbox folder
 *
 */
public class SandboxAnalyzer {
	public static final Logger logger = LoggerFactory.getLogger(SandboxAnalyzer.class);
	private int fProgress = 0;
	private RangeStats rangeStats = new RangeStats();
	private HashSet<String> ignoreFolderSet = new HashSet<String>(20);
	private HashSet<String> ignoreFileSet = new HashSet<String>(20);
	private ComponentStat comp = null;

	/**
	 * Constructor 
	 * 
	 * @param sandboxFolderPath
	 */
	public SandboxAnalyzer(String sandboxFolderPath) {
		comp = new ComponentStat(sandboxFolderPath);
	}

	/**
	 * Main analyzer class
	 * 
	 * @param sandboxFolder
	 */
	public void analyze(File sandboxFolder) {
		analyzeFolder(sandboxFolder, "", comp, 0);
	}

	/**
	 * Analyze the content of a folder
	 * 
	 * @param sandboxFolder
	 * @param path
	 * @param compStat
	 * @param depth
	 */
	private void analyzeFolder(File sandboxFolder, String path, ComponentStat compStat, int depth) {
		File[] contents = sandboxFolder.listFiles();
		long folders = 0;
		long files = 0;
		for (File file : contents) {
			if (file.isDirectory()) {
				if (!isIgnoredDirectory(file)) {
					folders++;
					compStat.addFolderStat(file, depth);
					analyzeFolder(file, file.getAbsolutePath(), compStat, depth + 1);
				} else {
					logger.info("\nIgnoring folder '{}'", file.getAbsolutePath());
				}
			} else {
				if (!isIgnoredFile(file)) {
					files++;
					FileInfo fInfo = FileInfo.getFileInfo(file);
					compStat.addFileStat(fInfo, depth);
					rangeStats.analyze(fInfo);
				} else {
					logger.info("\nIgnoring file '{}'", file.getAbsolutePath());
				}
			}
		}
		compStat.addFolderStats(folders, files, depth);
		showProgress();
	}

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	private void showProgress() {
		fProgress++;
		if (fProgress % 10 == 9) {
			System.out.print(".");
		}
	}

	/**
	 * Directories to be added to the ignore list
	 * 
	 * @param name
	 */
	public void addIgnoreDirectory(String name) {
		ignoreFolderSet.add(name);
	}

	/**
	 * Files to be ignored
	 * 
	 * @param name
	 */
	public void addIgnoreFile(String name) {
		ignoreFileSet.add(name);
	}

	private boolean isIgnoredDirectory(File file) {
		if (file == null) {
			return false;
		}
		return ignoreFolderSet.contains(file.getName());
	}

	private boolean isIgnoredFile(File file) {
		if (file == null) {
			return false;
		}
		return ignoreFolderSet.contains(file.getName());
	}

	public Workbook updateWorkBook(Workbook workBook) throws IOException {
		logger.info("Creating sheets...");

		String sandboxSheetSafeName = WorkbookUtil.createSafeSheetName("1 - Sandbox");
		Sheet sandboxSheet = workBook.createSheet(sandboxSheetSafeName);

		POICellHelper ch = new POICellHelper(workBook);
		Row groupHeader = sandboxSheet.createRow(0);
		Row sandboxHeader = sandboxSheet.createRow(1);

		sandboxHeader.createCell(1).setCellValue(ch.boldFace("Sandbox Folder"));
		sandboxHeader.createCell(2).setCellValue(ch.boldFace("Hierarchy Depth"));
		sandboxHeader.createCell(3).setCellValue(ch.boldFace("Folders (sum)"));
		sandboxHeader.createCell(4).setCellValue(ch.boldFace("Files (sum)"));
		sandboxHeader.createCell(5).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		sandboxHeader.createCell(6).setCellValue(ch.boldFace("Files/Folder"));
		sandboxHeader.createCell(7).setCellValue(ch.boldFace("Folder Depth(avg)"));
		sandboxHeader.createCell(8).setCellValue(ch.boldFace("Folder Depth(max)"));
		sandboxHeader.createCell(9).setCellValue(ch.boldFace("Folder Depth(sum)"));
		sandboxHeader.createCell(10).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		sandboxHeader.createCell(11).setCellValue(ch.boldFace("log(e)"));
		sandboxHeader.createCell(12).setCellValue(ch.boldFace("Max"));
		sandboxHeader.createCell(13).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		sandboxHeader.createCell(14).setCellValue(ch.boldFace("File Size(avg)"));
		sandboxHeader.createCell(15).setCellValue(ch.boldFace("File Size(max)"));
		sandboxHeader.createCell(16).setCellValue(ch.boldFace("File Size(sum)"));
		sandboxHeader.createCell(17).setCellValue(ch.boldFace("File Depth(avg)"));
		sandboxHeader.createCell(18).setCellValue(ch.boldFace("File Depth(max)"));
		sandboxHeader.createCell(19).setCellValue(ch.boldFace("File Depth(sum)"));
		sandboxHeader.createCell(20).setCellValue(ch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		sandboxHeader.createCell(21).setCellValue(ch.boldFace("Extensions"));
		sandboxHeader.createCell(22).setCellValue(ch.boldFace("Extension Details"));

		// Group header
		groupHeader.createCell(0).setCellValue(ch.boldFace("Sandbox Stats"));
		groupHeader.createCell(5).setCellValue(ch.boldFace("Folder Stats"));
		groupHeader.createCell(10).setCellValue(ch.boldFace("Folder Depth Limits"));
		groupHeader.createCell(13).setCellValue(ch.boldFace("File Stats"));

		Row row = sandboxSheet.createRow(2);
		row.createCell(1).setCellValue(comp.getComponentName());
		ch.setNumber(row.createCell(2), comp.getComponentHierarchyDepth());

		ch.setNumber(row.createCell(3), comp.getNoFolders());
		ch.setNumber(row.createCell(4), comp.getNoFiles());

		ch.setNumberP2(row.createCell(6), CalcUtil.divide(comp.getCumulatedFiles(), comp.getCumulatedFolders()));
		ch.setNumberP2(row.createCell(7), CalcUtil.divide(comp.getCumulatedFolderDepth(), comp.getCumulatedFolders()));
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

		IExtensions ext = comp.getExtensions();
		ch.setNumber(row.createCell(21), ext.getNoExtensions());
		ch.setText(row.createCell(22), ext.getExtensionsCompressed());
		logger.info("Autosize...");
		for (int i = 0; i < 27; i++) {
			sandboxSheet.autoSizeColumn(i);
		}
		rangeStats.updateWorkBook(workBook);
		return workBook;
	}

	/**
	 * Get the data to print it to the console.
	 * 
	 * @return
	 */
	public String getResultAsString() {
		return comp.toString();
	}

}
