package com.ibm.js.team.supporttools.scm.statistics;

import java.util.Set;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public interface IFileTypeStat {

	String getExtensionName();

	Set<String> getLineDelimiters();

	Set<String> getEncodings();

	void analyze(String ext, FileLineDelimiter lineDelimiter, String encoding);

}