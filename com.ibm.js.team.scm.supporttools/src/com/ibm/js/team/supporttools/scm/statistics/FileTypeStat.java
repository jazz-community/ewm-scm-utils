package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashSet;
import java.util.Set;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public class FileTypeStat {
	String extensionName = null;
	Set<String> lineDelimiters = new HashSet<String>();
	Set<String> encodings = new HashSet<String>();

	public FileTypeStat(String ext) {
		extensionName = ext;
	}

	public void analyze(String ext, FileLineDelimiter lineDelimiter, String encoding) {
		lineDelimiters.add(lineDelimiter.toString());
		encodings.add(encoding);
	}

	@Override
	public String toString() {
		String extension = extensionName + ": delimiters " + lineDelimiters.toString() + " encodings "
				+ lineDelimiters.toString();
		return extension;
	}
}
