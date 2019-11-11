package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;
import java.util.Set;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public class ExtensionsStats implements IExtensions {

	HashMap<String, IFileTypeStat> extensions = new HashMap<String, IFileTypeStat>(50);

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


	public HashMap<String, IFileTypeStat> getExtensions() {
		return extensions;
	}

	public String extensionsSimple() {
		String seperator = " ";
		String message = "File Extensions: " + extensions.size();
		Set<String> keys = extensions.keySet();
		if (keys.size()<=0){
			return message;
		}
		message+= " {";
		for (String key : keys) {
			message += seperator + key;
			seperator = "; ";
		}
		message += " }";
		return message;
	}

	public String extensionsAll() {
		String seperator = " ";
		String message = "File Extensions: " + extensions.size() + " {";
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
