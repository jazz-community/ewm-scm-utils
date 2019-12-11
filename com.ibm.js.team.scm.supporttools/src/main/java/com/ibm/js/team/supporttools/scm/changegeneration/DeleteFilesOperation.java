package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.utils.ArchiveToSCMExtractor;
import com.ibm.js.team.supporttools.scm.utils.FileContentUtil;

public class DeleteFilesOperation implements IFileOperation {
	public static final Logger logger = LoggerFactory.getLogger(DeleteFilesOperation.class);
	private HashSet<String> supportedExtensions = new HashSet<String>(20);

	public HashSet<String> getSupportedExtensions() {
		return supportedExtensions;
	}
	
	public void addSupportedExtension(String extension){
		supportedExtensions.add(extension);
	}

	public DeleteFilesOperation() {
		super();
	}

	@Override
	public void execute(File file) {
		if(!isSupportedFile(file)){
			return;
		}
		delete(file);
	}

	private void delete(File file) {
		String fileName = file.getAbsolutePath();
		if(file.exists()){
			file.delete();
			logger.info("Deleted '{}'", fileName );
		}
	}

	private boolean isSupportedFile(File file) {
		String name = file.getName();
		if (name==null){
			return false;
		}
		String extension = FilenameUtils.getExtension(name);
		return supportedExtensions.contains(extension);
	}
}
