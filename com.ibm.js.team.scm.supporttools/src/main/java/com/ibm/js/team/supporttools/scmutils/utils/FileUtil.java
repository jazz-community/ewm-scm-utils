/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports general file and file name operations.
 *
 */
public class FileUtil {
	public static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static void touchFile(File file, long time) {
		if (!file.isDirectory()) {
			touchAll(file, time);
		}
	}

	public static void touchAll(File file, long time) {
		try {
			if (!file.exists()) {
				new FileOutputStream(file).close();
			}
			file.setLastModified(time);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException file '{}'", file.getAbsolutePath());
		} catch (IOException e) {
			logger.error("IOException file '{}'", file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	public static String getExtension(String fileName) {
		String[] result = fileName.split("\\.");
		if (result.length == 2) {
			String ext = result[1];
			return ext;
		} 
		return null;
	}
	
	public static String getRelativePath(String filepath, String rootFolder){
		if(filepath==null){
			throw new RuntimeException("File path can not be null.");
		}
		if(rootFolder==null){
			throw new RuntimeException("Root folder can not be null.");
		}
		
		if(!filepath.startsWith(rootFolder)){
			throw new RuntimeException("File must have root folder as prefix.");
		}
		String relativePAth = filepath.substring(rootFolder.length());		
		if("".equals(relativePAth)){
			return null;
		}
		return relativePAth;
	}
}
