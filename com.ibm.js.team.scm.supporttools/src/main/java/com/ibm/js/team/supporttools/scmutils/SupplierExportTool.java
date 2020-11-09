package com.ibm.js.team.supporttools.scmutils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.util.FileUtil;
//import com.daimler.rm.reqif.correction2.ReqIFCorrectionCommand;
import com.ibm.js.team.supporttools.framework.util.TimeStampUtil;
import com.ibm.js.team.supporttools.scmutils.statistics.ExportInformation;
import com.ibm.js.team.supporttools.scmutils.statistics.PrefIDAnalyzer;
import com.ibm.js.team.supporttools.scmutils.utils.AppLauncherUtil;
import com.ibm.team.repository.common.serialize.internal.message.Request;

/**
 * 
 *
 */
public class SupplierExportTool {

	private static final String DASH_FIXED = "-Fixed";
	private static final String _FIXED = "_Fixed";
	private static final String _FIXED_CAP = "_FIXED";
	private static final String _FIXED_SML = "_fixed";
	private static final String ZIP_EXT = ".zip";
	private static final String REQUIFZ_EXT = ".reqifz";
	private static final String DATA_ROOT = "C:\\d_test_data";
	private static final String INITIAL_CSV_FILE = DATA_ROOT + "\\csv.log";
	private static final String PROCESSINGLOGSFOLDER = DATA_ROOT + "\\processingLogs\\";
	private static final String PROCESSINGFOLDER = DATA_ROOT + "\\processing\\";
	private static final String PROCESSINGFIXESFOLDER = DATA_ROOT + "\\processingFixed\\";
	private static final String DB_FOLDER = "C:\\d_test_data\\processingTemp\\db";
	private static final String OUTPUT_FOLDER = "C:\\d_test_data\\processingTemp\\output";
	private static final String EXTRACT_FOLDER = "C:\\d_test_data\\processingTemp\\extract";

	public static final Logger logger = LoggerFactory.getLogger(SupplierExportTool.class);
	private ExportInformation fExportInformation;
	private List<ReqIfElement> reqIfs = new ArrayList<ReqIfElement>(20);

	/**
	 *
	 *
	 */
	class ReqIfElement {
		File fFile = null;
		String fFilename = null;
		Date fDate = null;
		String fDateString = null;

		/**
		 * @param file
		 * @param fileName
		 * @param creationDate
		 * @param dateInfo
		 */
		public ReqIfElement(File file, String fileName, Date creationDate, String dateInfo) {
			fFile = file;
			fFilename = fileName;
			fDate = creationDate;
			fDateString = dateInfo;
		}

		public File getFile() {
			return fFile;
		}

		public void setFile(File fFile) {
			this.fFile = fFile;
		}

		public String getFilename() {
			return fFilename;
		}

		public void setFilename(String fFilename) {
			this.fFilename = fFilename;
		}

		public Date getDate() {
			return fDate;
		}

		public void setDate(Date fDate) {
			this.fDate = fDate;
		}

		public String getDateString() {
			return fDateString;
		}
	}

	/**
	 * To sort by date
	 *
	 */
	class Sortbydate implements Comparator<ReqIfElement> {

		@Override
		public int compare(ReqIfElement o1, ReqIfElement o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	}

	public SupplierExportTool(ExportInformation exportInformation) {
		fExportInformation = exportInformation;
	}

	public void analyze(File file) {
		ReqIfElement reqIfEl = analyzeFile(file);
		if (reqIfEl != null) {
			logger.info("Adding '{}'", file.getAbsolutePath());
			reqIfs.add(reqIfEl);
		}
	}

	/**
	 * @param file
	 */
	public ReqIfElement analyzeFile(File file) {

		String fileName = file.getName();

		int nameLength = fileName.length();
		int start = nameLength - ZIP_EXT.length();
		String extension = fileName.substring(start);
		if (!ZIP_EXT.equalsIgnoreCase(extension)) {
			start = nameLength - REQUIFZ_EXT.length();
			extension = fileName.substring(start);
			if (!REQUIFZ_EXT.equalsIgnoreCase(extension)) {
				String error = "Unexpected file extension " + file.getAbsolutePath();
				logger.error(error);
				throw new RuntimeException(error);
			}
		}
		if (start == 0) {
			String error = "Unexpected file name" + file.getAbsolutePath();
			logger.error(error);
			throw new RuntimeException(error);
		}
		String fname = fileName.substring(0, start);
		if (fname.endsWith("_IGNORE")) {
			return null; // Ignore
			// logger.info("IGNORE file name '{}'", fileName);
		}
		if (fname.endsWith(_FIXED_CAP) || fname.endsWith(_FIXED) || fname.endsWith(_FIXED_SML)
				|| fname.endsWith(DASH_FIXED)) {
			return null; // Ignore
			// logger.info("Suspicious file name '{}'", fileName);
			// fname = fname.substring(0, fname.length()-6);
		}

		int indSupplier = fname.lastIndexOf(fExportInformation.getSupplier());
		int indProject = fname.lastIndexOf(fExportInformation.getProject().replace(" ", "_"));
		if (indSupplier == -1) {
			String error = "Unexpected file name missing supplier info " + file.getAbsolutePath();
			logger.error(error);
			throw new RuntimeException(error);
		}
		if (indProject == -1) {
			String error = "Unexpected file name missing project info " + file.getAbsolutePath();
			logger.error(error);
			throw new RuntimeException(error);
		}
		// Get the last 19 as date/time.
		String dateInfo = fname.substring(fname.length() - 19, fname.length());
		if (dateInfo == null) {
			String error = "Date not found in file name " + file.getAbsolutePath();
			logger.error(error);
			throw new RuntimeException(error);
		}

		Date creationDate = null;
		try {
			creationDate = TimeStampUtil.getDateFromString(dateInfo, "dd-MM-yyyy_HH-mm-ss");
		} catch (ParseException e) {

			// Try other pattern
			int timestart = fname.lastIndexOf("_");
			if (timestart < 0) {
				String error = "Time not found in file name " + file.getAbsolutePath();
				logger.error(error);
				throw new RuntimeException(error);
			}
			String time = fname.substring(timestart);
			String tempName = fname.substring(0, timestart);
			int datestart = tempName.lastIndexOf("_");
			if (datestart < 0) {
				String error = "Date not found in file name " + file.getAbsolutePath();
				logger.error(error);
				throw new RuntimeException(error);
			}
			String date = tempName.substring(datestart + 1);
			try {
				dateInfo = date + time;
				creationDate = TimeStampUtil.getDateFromString(dateInfo, "dd-MM-yyyy_HH-mm-ss");
			} catch (ParseException e1) {
				String error = "Date not found in file name " + file.getAbsolutePath();
				logger.error(error);
				throw new RuntimeException(error);
			}
		}
		int indNum = fileName.indexOf("-");
		String number = fileName.substring(0, indNum);
		// Create sorted list
		return new ReqIfElement(file, fileName, creationDate, dateInfo);
	}

	/**
	 * 
	 */
	public void execute(boolean firstOnly) {
		File proocessingFixesFolder = new File(PROCESSINGFIXESFOLDER);
		FileUtil.createFolderWithParents(proocessingFixesFolder);
		File proocessingFolder = new File(PROCESSINGFOLDER);
		FileUtil.createFolderWithParents(proocessingFolder);
		File proocessingLogFolder = new File(PROCESSINGLOGSFOLDER);
		FileUtil.createFolderWithParents(proocessingLogFolder);
		File extractFolderFile = new File(EXTRACT_FOLDER);
		FileUtil.createFolderWithParents(extractFolderFile);
		File outputFolderFile = new File(OUTPUT_FOLDER);
		FileUtil.createFolderWithParents(outputFolderFile);
		File dbFolderFile = new File(DB_FOLDER);
		FileUtil.createFolderWithParents(dbFolderFile);

		Collections.sort(reqIfs, new Sortbydate());
		List<ReqIfElement> reqIfCorrectionFiles = new ArrayList<ReqIfElement>(20);
		int size = reqIfs.size();
		if (size > 1) {
				ReqIfElement referenceReqIF = reqIfs.get(0);
				for (int i = 1; i < size; i++) {
					ReqIfElement invalidReIf = reqIfs.get(i);
					File corrected = processReqIf(referenceReqIF, invalidReIf);
					if (corrected != null) {
						if(!corrected.exists()){
							logger.error("Error - correction file not found '{}'" , corrected.getAbsolutePath());
						}
						ReqIfElement reqIFCorrected = analyzeFile(corrected);
						reqIfCorrectionFiles.add(reqIFCorrected);
						//The corrected file is the next reference file
						referenceReqIF=reqIFCorrected;   
						if(firstOnly){
							break;
						}
					} else {
						logger.error("Error - Operation failed to create correction file for '{}'" , invalidReIf.getFilename());
						// Keep the previous correction file 
					}
				}
		}
		try {
			FileUtil.eraseAllRecursive(extractFolderFile);
			FileUtil.eraseAllRecursive(outputFolderFile);
			FileUtil.eraseAllRecursive(dbFolderFile);
		} catch (IOException e) {
			logger.error("EraseFolder:" + e.getMessage());
		}
	}

	/**
	 * @param correctReference
	 * @param invalidReqIFExport
	 * @return
	 */
	private File processReqIf(ReqIfElement correctReference, ReqIfElement invalidReqIFExport) {

		String date = invalidReqIFExport.getDateString();
		String CSVFileName = fExportInformation.getProject().replaceAll(" ", "_") + "-"
				+ fExportInformation.getSupplier() + "-" + date + ".csv";
		logger.info("\nProcessing...");
		logger.info("Compare '{}' with '{}'.", correctReference.getFilename(), invalidReqIFExport.getFilename());
		logger.info("\tCSV file name: '{}'", CSVFileName);

		File initialCSV = new File(INITIAL_CSV_FILE);
		File correctionLog = new File(DATA_ROOT + "\\reqif-correction.log");
		try {
			FileUtil.eraseAllRecursive(initialCSV);
		} catch (IOException e) {
			logger.error("DeleteFile:" + e.getMessage());
		}

		if(!new File(invalidReqIFExport.getFile().getAbsolutePath()).exists()){
			logger.error("File does not exist '{}'", invalidReqIFExport.getFile().getAbsolutePath());
		}
		if(!new File(correctReference.getFile().getAbsolutePath()).exists()){
			logger.error("File does not exist '{}'", correctReference.getFile().getAbsolutePath());
		}
		
		String[] callCommand = new String[12];
		callCommand[0] = "-d";
		callCommand[1] = "\"" + invalidReqIFExport.getFile().getAbsolutePath()+"\"";
		callCommand[2] = "-r";
		callCommand[3] = "\""+correctReference.getFile().getAbsolutePath()+"\"";
		callCommand[4] = "-e";
		callCommand[5] = EXTRACT_FOLDER;
		callCommand[6] = "-t";
		callCommand[7] = OUTPUT_FOLDER;
		callCommand[8] = "-x";
		callCommand[9] = DB_FOLDER;
		callCommand[10] = "-m";
		callCommand[11] = "export";
		// ReqIFCorrectionCommand correct = new ReqIFCorrectionCommand();
		// correct.main(callCommand);
		File executable = new File(DATA_ROOT + "\\RepairTool.bat");
		File workingDir = new File(DATA_ROOT);
		int result = AppLauncherUtil.executeBatch(executable, callCommand, new String[0], workingDir, true);
		if (result < 0) {
			logger.error("Repairtool returned error '{}'.", result);
		}
		if (!correctionLog.exists()) {
			logger.error("Error - CorrectionLog not created for '{}' '{}'", correctReference.getFile().getAbsolutePath(),
					invalidReqIFExport.getFile().getAbsolutePath());
		} else {
			correctionLog.renameTo(new File(PROCESSINGLOGSFOLDER + CSVFileName + "_correctionLog.log"));
		}
		if (!initialCSV.exists()) {
			logger.error("Error - CSV File Not Created for '{}' '{}'", correctReference.getFile().getAbsolutePath(),
					invalidReqIFExport.getFile().getAbsolutePath());
		} else {
			initialCSV.renameTo(new File(PROCESSINGFOLDER + CSVFileName));
		}
		File correctionFile = new File(OUTPUT_FOLDER + "\\" + invalidReqIFExport.getFilename());
		if (!correctionFile.exists()) {
			logger.error("Error - Correctionfile Not Created for '{}' '{}'",
					correctReference.getFile().getAbsolutePath(), invalidReqIFExport.getFile().getAbsolutePath());
			return null;
		} else {
			correctionFile.renameTo(new File(PROCESSINGFIXESFOLDER + correctionFile.getName()));
			String filename = correctionFile.getAbsolutePath();
			return new File(PROCESSINGFIXESFOLDER + correctionFile.getName());

		}
	}
}
