package com.ibm.js.team.supporttools.scm.statistics;

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

import com.ibm.js.team.supporttools.scm.utils.POICellHelper;
import com.ibm.js.team.supporttools.scm.utils.SheetUtils;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;

public class RepositoryAnalyzer {

	public static final Logger logger = LoggerFactory.getLogger(RepositoryAnalyzer.class);
	private ITeamRepository teamRepository = null;
	private IProgressMonitor monitor = null;
	private RepositoryStat repoStat = new RepositoryStat();
	private int activeRow = 0;

	public RepositoryAnalyzer(ITeamRepository teamRepository, IProgressMonitor monitor) {
		this.teamRepository = teamRepository;
		this.monitor = monitor;
	}

	public boolean analyze(List<? extends IWorkspaceConnection> connections) throws TeamRepositoryException {
		boolean result = false;
		String repositoryWorkBookName = "_repository.xls";
		Workbook repositoryWorkBook = SheetUtils.createWorkBook(repositoryWorkBookName);
		String sheetName = WorkbookUtil.createSafeSheetName("Repository Stats");
		Sheet sheet = repositoryWorkBook.createSheet(sheetName);

		POICellHelper rch = new POICellHelper(repositoryWorkBook);
		Row groupheader = sheet.createRow(getActiveRow());

		groupheader.createCell(5).setCellValue(rch.boldFace("Component Stats"));
		groupheader.createCell(11).setCellValue(rch.boldFace("Folder Stats"));
		groupheader.createCell(15).setCellValue(rch.boldFace("Folder Depth Limits"));
		groupheader.createCell(18).setCellValue(rch.boldFace("File Stats"));

		Row header = sheet.createRow(getNextActiveRow());
		header.createCell(1).setCellValue(rch.boldFace("Connections"));
		header.createCell(2).setCellValue(rch.boldFace("Connection"));
		header.createCell(3).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(4).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(5).setCellValue(rch.boldFace("Hierarchy Depth (avg)"));
		header.createCell(6).setCellValue(rch.boldFace("Hierarchy Depth (max)"));
		header.createCell(7).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(8).setCellValue(rch.boldFace("Folders (sum)"));
		header.createCell(9).setCellValue(rch.boldFace("Files (sum)"));
		header.createCell(10).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(11).setCellValue(rch.boldFace("Files/Folder"));
		header.createCell(12).setCellValue(rch.boldFace("Folder Depth(avg)"));
		header.createCell(13).setCellValue(rch.boldFace("Folder Depth(sum)"));
		header.createCell(14).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(15).setCellValue(rch.boldFace("log(e)"));
		header.createCell(16).setCellValue(rch.boldFace("Folder Depth(max)"));
		header.createCell(17).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header.createCell(18).setCellValue(rch.boldFace("File Size (avg)"));
		header.createCell(19).setCellValue(rch.boldFace("File Size (max)"));
		header.createCell(20).setCellValue(rch.boldFace("File Size (sum)"));
		header.createCell(21).setCellValue(rch.boldFace("File depth (avg)"));
		header.createCell(22).setCellValue(rch.boldFace("File depth (max)"));

		Row summaryRow = sheet.createRow(getNextActiveRow());
		summaryRow.createCell(1).setCellValue(new Double(connections.size()));
		sheet.createRow(getNextActiveRow());
		sheet.createRow(getNextActiveRow());
		Row groupheader2 = sheet.createRow(getNextActiveRow());

		groupheader2.createCell(5).setCellValue(rch.boldFace("Component Stats"));
		groupheader2.createCell(11).setCellValue(rch.boldFace("Folder Stats"));
		groupheader2.createCell(15).setCellValue(rch.boldFace("Folder Depth Limits"));
		groupheader2.createCell(18).setCellValue(rch.boldFace("File Stats"));

		Row header2 = sheet.createRow(getNextActiveRow());
		header2.createCell(2).setCellValue(rch.boldFace("Connection"));
		header2.createCell(3).setCellValue(rch.boldFace("Connection Details"));
		header2.createCell(4).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(5).setCellValue(rch.boldFace("Hierarchy Depth (avg)"));
		header2.createCell(6).setCellValue(rch.boldFace("Hierarchy Depth (max)"));
		header2.createCell(7).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(8).setCellValue(rch.boldFace("Folders (sum)"));
		header2.createCell(9).setCellValue(rch.boldFace("Files (sum)"));
		header2.createCell(10).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(11).setCellValue(rch.boldFace("Files/Folder"));
		header2.createCell(12).setCellValue(rch.boldFace("Folder Depth(avg)"));
		header2.createCell(13).setCellValue(rch.boldFace("Folder Depth(sum)"));
		header2.createCell(14).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(15).setCellValue(rch.boldFace("log(e)"));
		header2.createCell(16).setCellValue(rch.boldFace("Folder Depth(max)"));
		header2.createCell(17).setCellValue(rch.boldFace(POICellHelper.XLS_COLUMN_SEPARATOR));
		header2.createCell(18).setCellValue(rch.boldFace("File Size (avg)"));
		header2.createCell(19).setCellValue(rch.boldFace("File Size (max)"));
		header2.createCell(20).setCellValue(rch.boldFace("File Size (sum)"));
		header2.createCell(21).setCellValue(rch.boldFace("File depth (avg)"));
		header2.createCell(22).setCellValue(rch.boldFace("File depth (max)"));

		for (Iterator<? extends IWorkspaceConnection> iterator = connections.iterator(); iterator.hasNext();) {
			IWorkspaceConnection workspaceConnection = (IWorkspaceConnection) iterator.next();
			String workspaceConnectionName = workspaceConnection.getName();
			logger.info("Analyzing: '{}'", workspaceConnectionName);
			ConnectionAnalyzer analyzer = new ConnectionAnalyzer(teamRepository, monitor);
			String workbookFileName = workspaceConnectionName + ".xls";
			Workbook workBook = SheetUtils.createWorkBook(workbookFileName);
			try {
				analyzer.analyzeWorkspace(workspaceConnection);
				ConnectionStats connectionStats = analyzer.getConnectionStats();
				connectionStats.updateWorkBook(workBook);
				repoStat.addConnectionStats(connectionStats);

				Row activeRow = sheet.createRow(getNextActiveRow());
				rch.setText(activeRow.createCell(2), workspaceConnectionName);
				rch.setFileHyperLink(activeRow.createCell(3), workbookFileName, workbookFileName);
				rch.setURLHyperLink(activeRow.createCell(3), workbookFileName, workbookFileName);
				rch.setNumberP2(activeRow.createCell(5), connectionStats.getAverageHierarchyDepth());
				rch.setNumber(activeRow.createCell(6), connectionStats.getMaxHierarchyDepth());
				//
				rch.setNumber(activeRow.createCell(8), connectionStats.getCumulatedFolders());
				rch.setNumber(activeRow.createCell(9), connectionStats.getCumulatedFiles());
				//
				rch.setNumberP2(activeRow.createCell(11), connectionStats.getAverageFilesPerFolder());
				rch.setNumberP2(activeRow.createCell(12), connectionStats.getAverageFolderDepth());
				rch.setNumber(activeRow.createCell(13), connectionStats.getCumulatedFolderDepth());
				//
				rch.setNumberP2(activeRow.createCell(15), Math.log(connectionStats.getCumulatedFolders()));
				rch.setNumber(activeRow.createCell(16), connectionStats.getMaxFolderDepth());
				//
				rch.setNumberP2(activeRow.createCell(18), connectionStats.getAverageFileSize());
				rch.setNumber(activeRow.createCell(19), connectionStats.getMaxFileSize());
				rch.setNumber(activeRow.createCell(20), connectionStats.getCumulatedFileSize());
				rch.setNumberP2(activeRow.createCell(21), connectionStats.getAverageFileDepth());
				rch.setNumber(activeRow.createCell(22), connectionStats.getMaxFileDepth());

				SheetUtils.writeWorkBook(workBook, workbookFileName);
			} catch (IOException e) {
				logger.error("I/O Exception writing workbook.");
				e.printStackTrace();
				return result;
			}
		}
		rch.setNumberP2(summaryRow.createCell(5), repoStat.getAverageHierarchyDepth());
		rch.setNumber(summaryRow.createCell(6), repoStat.getMaxHierarchyDepth());
		//
		rch.setNumber(summaryRow.createCell(8), repoStat.getCumulatedFolders());
		rch.setNumber(summaryRow.createCell(9), repoStat.getCumulatedFiles());
		//
		rch.setNumberP2(summaryRow.createCell(11), repoStat.getCumulatedFilesPerFolder());
		rch.setNumberP2(summaryRow.createCell(12), repoStat.getAverageFolderDepth());
		rch.setNumber(summaryRow.createCell(13), repoStat.getCumulatedFolderDepth());
		//
		rch.setNumberP2(summaryRow.createCell(15), Math.log(repoStat.getCumulatedFolderDepth()));
		rch.setNumber(summaryRow.createCell(16), repoStat.getMaxFolderDepth());
		//
		rch.setNumberP2(summaryRow.createCell(18), repoStat.getAverageFileSize());
		rch.setNumber(summaryRow.createCell(19), repoStat.getMaxFileSize());
		rch.setNumber(summaryRow.createCell(20), repoStat.getCumulatedFileSize());
		rch.setNumberP2(summaryRow.createCell(21), repoStat.getAverageFileDepth());
		rch.setNumber(summaryRow.createCell(22), repoStat.getMaxFileDepth());

		for (int i = 0; i < 26; i++) {
			sheet.autoSizeColumn(i);
		}
		try {
			SheetUtils.writeWorkBook(repositoryWorkBook, repositoryWorkBookName);
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
