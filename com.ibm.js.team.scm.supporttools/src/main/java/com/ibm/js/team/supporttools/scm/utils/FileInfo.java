/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import java.io.File;

import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.filesystem.common.IFileItem;

public class FileInfo {

	String name = null;
	long size = 0;
	long rawlength = 0;
	long estLength = 0;
	FileLineDelimiter lineDelimiter = null;
	String encoding = null;

	public FileInfo(IFileItem file) {
		super();
		analyze(file);
	}

	public FileInfo(File file) {
		super();
		analyze(file);
	}

	private void analyze(IFileItem file) {
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
		analyze(name, size, rawlength, estLength, lineDelimiter, encoding);
	}

	private void analyze(File file) {
		long size = file.length();
		String name = file.getName();
		analyze(name, size, size, size, null, null);
	}

	private void analyze(String name, long size, long rawlength, long estLength, FileLineDelimiter lineDelimiter,
			String encoding) {
		setName(name);
		setSize(size);
		setRawlength(rawlength);
		setEstLength(estLength);
		setLineDelimiter(lineDelimiter);
		setEncoding(encoding);
	}

	public static FileInfo getFileInfo(IFileItem file) {
		return new FileInfo(file);
	}

	public static FileInfo getFileInfo(File file) {
		return new FileInfo(file);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getRawlength() {
		return rawlength;
	}

	public void setRawlength(long rawlength) {
		this.rawlength = rawlength;
	}

	public long getEstLength() {
		return estLength;
	}

	public void setEstLength(long estLength) {
		this.estLength = estLength;
	}

	public FileLineDelimiter getLineDelimiter() {
		return lineDelimiter;
	}

	public void setLineDelimiter(FileLineDelimiter lineDelimiter) {
		this.lineDelimiter = lineDelimiter;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
}
