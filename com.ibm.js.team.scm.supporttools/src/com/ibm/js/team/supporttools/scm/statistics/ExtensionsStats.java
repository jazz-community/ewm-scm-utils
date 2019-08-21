package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;
import java.util.Set;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public class ExtensionsStats {

	HashMap<String, FileTypeStat> extensions = new HashMap<String, FileTypeStat>(50);

	public void analyze(String name, FileLineDelimiter lineDelimiter, String encoding) {
		String[] result = name.split("\\.");
		if (result.length == 2) {
			String ext = result[1];
			if (ext != null && ext.length() > 0) {
				logExtension(ext, lineDelimiter, encoding);
			}
		}
	}

	private void logExtension(String ext, FileLineDelimiter lineDelimiter, String encoding) {
		if(ext==null) {
			return;
		}
		FileTypeStat extension = extensions.get(ext);
		if (extension == null) {
			extension = new FileTypeStat(ext);
		}
		extensions.put(ext, extension);
		extension.analyze(ext, lineDelimiter, encoding);
	}

	@Override
	public String toString() {
		String seperator = " ";
		String message = "File Extensions: " + extensions.size() + " {";
		Set<String> keys = extensions.keySet();
		for (String key : keys) {
			FileTypeStat extension = extensions.get(key);
			message += seperator + extension.toString();
			seperator = "; ";
		}
		message += " }";
		return message;
	}

}
