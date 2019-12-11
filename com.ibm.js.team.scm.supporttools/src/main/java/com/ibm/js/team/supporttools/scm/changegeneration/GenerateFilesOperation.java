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

public class GenerateFilesOperation implements IFileOperation {
	public static final Logger logger = LoggerFactory.getLogger(GenerateFilesOperation.class);
	private HashSet<String> supportedExtensions = new HashSet<String>(20);
	private String generationExtension = null;

	public HashSet<String> getSupportedExtensions() {
		return supportedExtensions;
	}

	public String[] getSupportedExtensionList() {
		return supportedExtensions.toArray(new String[0]);
	}

	
	public void addSupportedExtension(String extension){
		supportedExtensions.add(extension);
	}

	public GenerateFilesOperation(String generationExtension) {
		super();
		this.generationExtension = generationExtension;
		if(generationExtension==null){
			throw new RuntimeException("Extension for Generation can not be null.");
		}
		if("".equals(generationExtension)){
			throw new RuntimeException("Extension for Generation can not be empty.");
		}
	}

	@Override
	public void execute(File file) {
		if(!isSupportedFile(file)){
			return;
		}
		generate(file);
	}

	private void generate(File file) {
		try {
			String generationFileName = file.getAbsolutePath() + "." + generationExtension;
			File generated = new File(generationFileName);
			FileInputStream in = new FileInputStream(file);
			FileOutputStream out = new FileOutputStream(generated);
			FileContentUtil fCont = new FileContentUtil();
			fCont.generateRandom(in,out);
			logger.info("Created '{}'", generationFileName );
			out.close();
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException");
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException");
		} catch (IOException e) {
			logger.error("IOException");
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
