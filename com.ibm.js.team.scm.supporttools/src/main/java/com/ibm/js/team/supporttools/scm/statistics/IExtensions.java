package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public interface IExtensions {

	public HashMap<String, IFileTypeStat> getExtensions();
	public void analyze(String ext, FileLineDelimiter lineDelimiter, String encoding);
	String extensionsSimple();

}