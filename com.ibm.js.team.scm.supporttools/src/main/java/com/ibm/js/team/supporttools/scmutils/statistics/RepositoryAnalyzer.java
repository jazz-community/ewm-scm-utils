/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.statistics;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scmutils.statistics.sizerange.RangeStats;
import com.ibm.js.team.supporttools.scmutils.utils.ComponentUtil;
import com.ibm.js.team.supporttools.scmutils.utils.POICellHelper;
import com.ibm.js.team.supporttools.scmutils.utils.SheetUtils;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;

/**
 * Analyzes a list of workspace connections and generates statistics in an excel file.
 *
 */
public class RepositoryAnalyzer {

	private static final int COL_CONNS = 1;
	private static final int COL_CONN = 2;
	private static final int COL_COMP_STATS = 5;
	private static final int COL_FOLDERS = 9;
	private static final int COL_FOLDER_STATS = 12;
	private static final int COL_FOLDER_DEPTH_STATS = 16;
	private static final int COL_FILE_STATS = 19;

	public static final Logger logger = LoggerFactory.getLogger(RepositoryAnalyzer.class);
	private ITeamRepository teamRepository = null;
	private IProgressMonitor monitor = null;
	private RepositoryStat repoStat = new RepositoryStat();
	private int activeRow = 0;
	private String fOutputFolder;
	private RangeStats fCrossWorkspaceRangeStatistics = new RangeStats();

	public RepositoryAnalyzer(ITeamRepository teamRepository, String outputFolder, IProgressMonitor monitor) {
		this.teamRepository = teamRepository;
		this.fOutputFolder = outputFolder;
		this.monitor = monitor;
	}

	public boolean analyze(List<? extends IWorkspaceConnection> connections) throws TeamRepositoryException {
		boolean result = false;
		String repositoryWorkBookName = "_repository.xls";
		Workbook repositoryWorkBook = SheetUtils.createWorkBook();
		String sheetName = WorkbookUtil.createSafeSheetName("Repository Stats");
		Sheet sheet = repositoryWorkBook.createSheet(sheetName);

		POICellHelper rch = new POICellHelper(repositoryWorkBook);
		Row groupheader = sheet.createRow(getActiveRow());
		// Top grouping
		groupheader.createCell(COL_COMP_STATS).setCellValue(rch.boldFace("Component Stats"));
		//groupheader.createCell(COL_FOLDERS).setCellValue(rch.boldFace("Folders"));
		groupheader.createCell(COL_FOLDER_STATS).setCellValue(rch.boldFace("Folder Stats"));
		groupheader.createCell(COL_FOLDER_DEPTH_STATS).setCellValue(rch.boldFace("Folder Depth Limits"));
		groupheader.createCell(COL_FILE_STATS).setCellValue(rch.boldFace("File Stats"));

		// 2nd grouping
		Row header = sheet.createRow(getNextActiveRow());
		header.createCell(COL_CONNS).setCellValue(rch.boldFace("Connections"));
		//header.createCell(COL_CONN).setCellValue(rch.boldFace("Connection"));
		header.createCell(COL_CONN+1).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(COL_CONN+2).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(COL_COMP_STATS).setCellValue(rch.boldFace("Components"));
		header.createCell(COL_COMP_STATS+1).setCellValue(rch.boldFace("Hierarchy Depth (avg)"));
		header.createCell(COL_COMP_STATS+2).setCellValue(rch.boldFace("Hierarchy Depth (max)"));
		header.createCell(COL_COMP_STATS+3).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(COL_FOLDERS).setCellValue(rch.boldFace("Folders (sum)"));
		header.createCell(COL_FOLDERS+1).setCellValue(rch.boldFace("Files (sum)"));
		header.createCell(COL_FOLDERS+2).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(COL_FOLDER_STATS).setCellValue(rch.boldFace("Files/Folder"));
		header.createCell(COL_FOLDER_STATS+1).setCellValue(rch.boldFace("Folder Depth(avg)"));
		header.createCell(COL_FOLDER_STATS+2).setCellValue(rch.boldFace("Folder Depth(sum)"));
		header.createCell(COL_FOLDER_STATS+3).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(COL_FOLDER_DEPTH_STATS).setCellValue(rch.boldFace("log(e)"));
		header.createCell(COL_FOLDER_DEPTH_STATS+1).setCellValue(rch.boldFace("Folder Depth(max)"));
		header.createCell(COL_FOLDER_DEPTH_STATS+2).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(COL_FILE_STATS).setCellValue(rch.boldFace("File Size (avg)"));
		header.createCell(COL_FILE_STATS+1).setCellValue(rch.boldFace("File Size (max)"));
		header.createCell(COL_FILE_STATS+2).setCellValue(rch.boldFace("File Size (sum)"));
		header.createCell(COL_FILE_STATS+3).setCellValue(rch.boldFace("File depth (avg)"));
		header.createCell(COL_FILE_STATS+4).setCellValue(rch.boldFace("File depth (max)"));

		Row summaryRow = sheet.createRow(getNextActiveRow());
		summaryRow.createCell(COL_CONNS).setCellValue(new Double(connections.size()));
		sheet.createRow(getNextActiveRow());
		sheet.createRow(getNextActiveRow());
		Row groupheader2 = sheet.createRow(getNextActiveRow());

		groupheader2.createCell(COL_COMP_STATS).setCellValue(rch.boldFace("Component Stats"));
		groupheader2.createCell(COL_FOLDER_STATS).setCellValue(rch.boldFace("Folder Stats"));
		groupheader2.createCell(COL_FOLDER_DEPTH_STATS).setCellValue(rch.boldFace("Folder Depth Limits"));
		groupheader2.createCell(COL_FILE_STATS).setCellValue(rch.boldFace("File Stats"));

		Row header2 = sheet.createRow(getNextActiveRow());
		header2.createCell(COL_CONN).setCellValue(rch.boldFace("Connection"));
		header2.createCell(COL_CONN+1).setCellValue(rch.boldFace("Connection Details"));
		header2.createCell(COL_CONN+2).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(COL_COMP_STATS).setCellValue(rch.boldFace("Components"));
		header2.createCell(COL_COMP_STATS+1).setCellValue(rch.boldFace("Hierarchy Depth (avg)"));
		header2.createCell(COL_COMP_STATS+2).setCellValue(rch.boldFace("Hierarchy Depth (max)"));
		header2.createCell(COL_COMP_STATS+3).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(COL_FOLDERS).setCellValue(rch.boldFace("Folders (sum)"));
		header2.createCell(COL_FOLDERS+1).setCellValue(rch.boldFace("Files (sum)"));
		header2.createCell(COL_FOLDERS+2).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(COL_FOLDER_STATS).setCellValue(rch.boldFace("Files/Folder"));
		header2.createCell(COL_FOLDER_STATS+1).setCellValue(rch.boldFace("Folder Depth(avg)"));
		header2.createCell(COL_FOLDER_STATS+2).setCellValue(rch.boldFace("Folder Depth(sum)"));
		header2.createCell(COL_FOLDER_STATS+3).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(COL_FOLDER_DEPTH_STATS).setCellValue(rch.boldFace("log(e)"));
		header2.createCell(COL_FOLDER_DEPTH_STATS+1).setCellValue(rch.boldFace("Folder Depth(max)"));
		header2.createCell(COL_FOLDER_DEPTH_STATS+2).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(COL_FILE_STATS).setCellValue(rch.boldFace("File Size (avg)"));
		header2.createCell(COL_FILE_STATS+1).setCellValue(rch.boldFace("File Size (max)"));
		header2.createCell(COL_FILE_STATS+2).setCellValue(rch.boldFace("File Size (sum)"));
		header2.createCell(COL_FILE_STATS+3).setCellValue(rch.boldFace("File depth (avg)"));
		header2.createCell(COL_FILE_STATS+4).setCellValue(rch.boldFace("File depth (max)"));

		for (Iterator<? extends IWorkspaceConnection> iterator = connections.iterator(); iterator.hasNext();) {
			IWorkspaceConnection workspaceConnection = (IWorkspaceConnection) iterator.next();
			String workspaceConnectionName = workspaceConnection.getName();
			logger.info("Analyzing: '{}'", workspaceConnectionName);
			ConnectionAnalyzer analyzer = new ConnectionAnalyzer(teamRepository, monitor,
					fCrossWorkspaceRangeStatistics);
			String workbookFileName = workspaceConnectionName + ".xls";
			Workbook workBook = SheetUtils.createWorkBook();
			try {
				analyzer.analyzeWorkspace(workspaceConnection);
				ConnectionStats connectionStats = analyzer.getConnectionStats();
				connectionStats.updateWorkBook(workBook);
				repoStat.addConnectionStats(connectionStats);

				Row activeRow = sheet.createRow(getNextActiveRow());
				rch.setText(activeRow.createCell(COL_CONN), workspaceConnectionName);
				rch.setFileHyperLink(activeRow.createCell(COL_CONN+1), workbookFileName, workbookFileName);
				// 
				rch.setNumber(activeRow.createCell(COL_COMP_STATS), connectionStats.getNoComponents());
				rch.setNumber(activeRow.createCell(COL_COMP_STATS+1), connectionStats.getAverageHierarchyDepth());
				rch.setNumber(activeRow.createCell(COL_COMP_STATS+2), connectionStats.getMaxHierarchyDepth());
				//
				rch.setNumber(activeRow.createCell(COL_FOLDERS), connectionStats.getCumulatedFolders());
				rch.setNumber(activeRow.createCell(COL_FOLDERS+1), connectionStats.getCumulatedFiles());
				//
				rch.setNumberP2(activeRow.createCell(COL_FOLDER_STATS), connectionStats.getAverageFilesPerFolder());
				rch.setNumberP2(activeRow.createCell(COL_FOLDER_STATS+1), connectionStats.getAverageFolderDepth());
				rch.setNumber(activeRow.createCell(COL_FOLDER_STATS+2), connectionStats.getCumulatedFolderDepth());
				//
				rch.setNumberP2(activeRow.createCell(COL_FOLDER_DEPTH_STATS), Math.log(connectionStats.getCumulatedFolders()));
				rch.setNumber(activeRow.createCell(COL_FOLDER_DEPTH_STATS+1), connectionStats.getMaxFolderDepth());
				//
				rch.setNumberP2(activeRow.createCell(COL_FILE_STATS), connectionStats.getAverageFileSize());
				rch.setNumber(activeRow.createCell(COL_FILE_STATS+1), connectionStats.getMaxFileSize());
				rch.setNumber(activeRow.createCell(COL_FILE_STATS+2), connectionStats.getCumulatedFileSize());
				rch.setNumberP2(activeRow.createCell(COL_FILE_STATS+3), connectionStats.getAverageFileDepth());
				rch.setNumber(activeRow.createCell(COL_FILE_STATS+4), connectionStats.getMaxFileDepth());

				// Add the range statistics
				analyzer.getConnectionRangeStats().updateWorkBook(workBook);
				SheetUtils.writeWorkBook(workBook, fOutputFolder, workbookFileName);
			} catch (IOException e) {
				logger.error("I/O Exception writing workbook.");
				e.printStackTrace();
				return result;
			}
		}
		

		int totalComponents = ComponentUtil.getComponentCount(teamRepository, monitor);
		rch.setNumberP2(summaryRow.createCell(COL_COMP_STATS), totalComponents);
		rch.setNumber(summaryRow.createCell(COL_COMP_STATS+1), repoStat.getAverageHierarchyDepth());
		rch.setNumber(summaryRow.createCell(COL_COMP_STATS+2), repoStat.getMaxHierarchyDepth());
		//
		rch.setNumber(summaryRow.createCell(COL_FOLDERS), repoStat.getCumulatedFolders());
		rch.setNumber(summaryRow.createCell(COL_FOLDERS+1), repoStat.getCumulatedFiles());
		//
		rch.setNumberP2(summaryRow.createCell(COL_FOLDER_STATS), repoStat.getCumulatedFilesPerFolder());
		rch.setNumberP2(summaryRow.createCell(COL_FOLDER_STATS+1), repoStat.getAverageFolderDepth());
		rch.setNumber(summaryRow.createCell(COL_FOLDER_STATS+2), repoStat.getCumulatedFolderDepth());
		//
		rch.setNumberP2(summaryRow.createCell(COL_FOLDER_DEPTH_STATS), Math.log(repoStat.getCumulatedFolderDepth()));
		rch.setNumber(summaryRow.createCell(COL_FOLDER_DEPTH_STATS+1), repoStat.getMaxFolderDepth());
		//
		rch.setNumberP2(summaryRow.createCell(COL_FILE_STATS), repoStat.getAverageFileSize());
		rch.setNumber(summaryRow.createCell(COL_FILE_STATS+1), repoStat.getMaxFileSize());
		rch.setNumber(summaryRow.createCell(COL_FILE_STATS+2), repoStat.getCumulatedFileSize());
		rch.setNumberP2(summaryRow.createCell(COL_FILE_STATS+3), repoStat.getAverageFileDepth());
		rch.setNumber(summaryRow.createCell(COL_FILE_STATS+4), repoStat.getMaxFileDepth());

		for (int i = 0; i < 26; i++) {
			sheet.autoSizeColumn(i);
		}
		try {
			// Add the cross repository range statistics.
			fCrossWorkspaceRangeStatistics.updateWorkBook(repositoryWorkBook);
			SheetUtils.writeWorkBook(repositoryWorkBook, fOutputFolder, repositoryWorkBookName);
		} catch (IOException e) {
			logger.error("I/O Exception writing workbook.");
			e.printStackTrace();
			return result;
		}

		result = true;
		return result;
	}

	private int getActiveRow() {
		return activeRow;
	}

	private int getNextActiveRow() {
		return ++this.activeRow;
	}

}
