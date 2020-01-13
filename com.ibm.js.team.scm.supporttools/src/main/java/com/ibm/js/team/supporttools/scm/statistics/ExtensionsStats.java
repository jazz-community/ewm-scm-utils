/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public class ExtensionsStats implements IExtensions {
	public static final Logger logger = LoggerFactory.getLogger(ExtensionsStats.class);

	HashMap<String, IFileTypeStat> extensions = new HashMap<String, IFileTypeStat>(50);

	@Override
	public int getNoExtensions() {
		return extensions.entrySet().size();
	}

	public HashMap<String, IFileTypeStat> getExtensions() {
		return extensions;
	}

	@Override
	public String getExtensionsCompressed() {
		String seperator = " ";
		String message = "";
		Set<String> keys = extensions.keySet();
		if (keys.size() <= 0) {
			return message;
		}
		message += " {";
		for (String key : keys) {
			message += seperator + key;
			seperator = "; ";
		}
		message += " }";
		return message;
	}

	public String extensionsSimple() {
		String seperator = " ";
		String message = "Extensions: " + extensions.size();
		Set<String> keys = extensions.keySet();
		if (keys.size() <= 0) {
			return message;
		}
		message += " {";
		for (String key : keys) {
			message += seperator + key;
			seperator = "; ";
		}
		message += " }";
		return message;
	}

	public void analyze(String name, FileLineDelimiter lineDelimiter, String encoding) {
		// String ext2 = new FileUtil().getExtension(name);
		String ext = FilenameUtils.getExtension(name);
		if (ext != null && ext.length() > 0) {
			logExtension(ext, lineDelimiter, encoding);
		}
	}

	private void logExtension(String ext, FileLineDelimiter lineDelimiter, String encoding) {
		if (ext == null) {
			return;
		}
		IFileTypeStat extension = extensions.get(ext);
		if (extension == null) {
			extension = new FileTypeStat(ext);
		}
		extensions.put(ext, extension);
		extension.analyze(ext, lineDelimiter, encoding);
	}

	public String extensionsAll() {
		String seperator = " ";
		String message = "Extensions: " + extensions.size() + " {";
		Set<String> keys = extensions.keySet();
		for (String key : keys) {
			IFileTypeStat extension = extensions.get(key);
			message += seperator + extension.toString();
			seperator = "; ";
		}
		message += " }";
		return message;
	}

	@Override
	public String toString() {
		return extensionsAll();
	}

}
