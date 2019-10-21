package com.ibm.js.team.supporttools.scm.statistics;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.scm.common.IFolder;

public class ComponentStat {
	public static final String COLUMN_SEPERATOR = " \t";
	public static final Logger logger = LoggerFactory.getLogger(ComponentStat.class);
	private ExtensionsStats extensions = new ExtensionsStats();
	private String componentName;
	private long componentHierarchyDepth = 0;
	private long noFiles = 0;
	private long noFolders = 0;
	private long maxFileDepth = 0;
	private long maxFolderDepth = 0;
	private long maxFileSize = 0;
	private long cumulatedFileSize = 0;
	private long cumulatedFolders = 0;
	private long cumulatedFiles = 0;
	private long cumulatedFileDepth = 0;
	private long cumulatedFolderDepth = 0;

	public ComponentStat(String name) {
		componentName = name;
	}

	public void setComponentName(String name) {
		componentName = name;
	}

	public void setComponentHierarchyDepth(long depth) {
		componentHierarchyDepth = depth;
	}

	public long getCumulatedFolders() {
		return cumulatedFolders;
	}

	public void setCumulatedFolders(long cumulatedFolders) {
		this.cumulatedFolders = cumulatedFolders;
	}

	public ExtensionsStats getExtensions() {
		return extensions;
	}

	public String getComponentName() {
		return componentName;
	}

	public long getComponentHierarchyDepth() {
		return componentHierarchyDepth;
	}

	public long getNoFiles() {
		return noFiles;
	}

	public long getNoFolders() {
		return noFolders;
	}

	public long getMaxFileDepth() {
		return maxFileDepth;
	}

	public long getMaxFolderDepth() {
		return maxFolderDepth;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public long getCumulatedFileSize() {
		return cumulatedFileSize;
	}

	public long getCumulatedFiles() {
		return cumulatedFiles;
	}

	public long getCumulatedFileDepth() {
		return cumulatedFileDepth;
	}

	public long getCumulatedFolderDepth() {
		return cumulatedFolderDepth;
	}

	private void addCumulatedFileSize(long size) {
		cumulatedFileSize += size;
		if (size > maxFileSize) {
			maxFileSize = size;
		}
	}

	private void calcFileMaxDepth(long depth) {
		if (depth > maxFileDepth) {
			maxFileDepth = depth;
		}
	}

	private void calcFolderDepth(long depth) {
		if (depth > maxFolderDepth) {
			maxFolderDepth = depth;
		}
	}

	public void addFileStat(IFileItem file, int depth) {
		long rawlength = file.getContent().getRawLength();
		long size = file.getContent().getSize();
		long estLength = file.getContent().getEstimatedConvertedLength();
		FileLineDelimiter lineDelimiter = FileLineDelimiter.LINE_DELIMITER_NONE;
		String encoding = null;
		IFileContent filecontent = file.getContent();
		if (filecontent != null) {
			encoding = filecontent.getCharacterEncoding();
			lineDelimiter = filecontent.getLineDelimiter();
		}
		String name = file.getName();
		addFileStat(name, size, rawlength, estLength, depth, lineDelimiter, encoding);
	}

	public void addFileStat(File file, int depth) {
		long size = file.length();
		String name = file.getName();
		addFileStat(name, size, size, size, depth, null, null);
	}

	public void addFileStat(String name, long size, long rawlength, long estLength, int depth,
			FileLineDelimiter lineDelimiter, String encoding) {
		// logger.info("File: {} {} {} {} {} {} '{}'.", size, rawlength, estLength,
		// depth, lineDelimiter, encoding, name);
		extensions.analyze(name, lineDelimiter, encoding);
		noFiles++;
		calcFileMaxDepth(depth);
		addCumulatedFileSize(size);
	}

	public void addFolderStat(IFolder v, int depth, String path) {
		addFolderStat(depth);
	}

	public void addFolderStat(File file, int depth) {
		addFolderStat(depth);
	}

	private void addFolderStat(int depth) {
		noFolders++;
		calcFolderDepth(depth);
	}

	@Override
	public String toString() {
		String message = "";
		message += componentName + " \n";
		message += " Hierarchy Depth:\t " + componentHierarchyDepth + " \n";
		message += PrintStat.getFileAndFolderStatistics(cumulatedFolders, cumulatedFolderDepth, maxFolderDepth,
				cumulatedFiles, maxFileSize, cumulatedFileSize, maxFileDepth, cumulatedFileDepth);
//		message += " Folders:\t " + cumulatedFolders + COLUMN_SEPERATOR;
//		
//		message += " Files/Folder:\t\t " + CalcUtil.divideFloatWithPrecision2AsString(cumulatedFiles, cumulatedFolders) + COLUMN_SEPERATOR;
//		message += " Folder Depth(avg):\t " + CalcUtil.divideFloatWithPrecision2AsString(cumulatedFolderDepth, cumulatedFolders) + COLUMN_SEPERATOR;
//		message += " Folder Depth(max):\t " + maxFolderDepth + COLUMN_SEPERATOR;
//		message += " Folder Depth(sum):\t " + cumulatedFolderDepth + COLUMN_SEPERATOR;
//		message += "\n";
//		message += " Files:\t\t " + cumulatedFiles + COLUMN_SEPERATOR;
//		message += " File Size(avg):\t " + CalcUtil.byBinaryMagnitudeAsString(CalcUtil.divideLong(cumulatedFileSize, cumulatedFiles)) + COLUMN_SEPERATOR;
////		message += " File Size(max):\t " + CalcUtil.byBinaryMagnitudeAsString(CalcUtil.divideLong(cumulatedFileSize, cumulatedFiles)) + COLUMN_SEPERATOR;
//		message += " File Size(max):\t " + CalcUtil.byBinaryMagnitudeAsString(maxFileSize) + COLUMN_SEPERATOR;
//		message += " File Size(sum):\t " + CalcUtil.byBinaryMagnitudeAsString(cumulatedFileSize) + COLUMN_SEPERATOR;
//		message += " File Depth(avg):\t " + CalcUtil.divideFloatWithPrecision2AsString(cumulatedFileDepth, cumulatedFiles) + COLUMN_SEPERATOR;
//		message += " File Depth(max):\t " + maxFileDepth + COLUMN_SEPERATOR;
//		message += " File Depth(sum):\t " + cumulatedFileDepth + COLUMN_SEPERATOR;
		message += "\n";
		message += " " + extensions.toString() + COLUMN_SEPERATOR;
		message += "\n";
		return message;
	}

	public long getHierarchyDepth() {
		return componentHierarchyDepth;
	}

	public void addFolderStats(long folders, long files, int depth) {
		cumulatedFolders += folders;
		cumulatedFiles += files;
		cumulatedFileDepth += files * depth;
		cumulatedFolderDepth += folders * depth;
	}

}
