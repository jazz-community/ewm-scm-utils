/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.util.FileUtil;

/**
 * Utility for excel workbooks
 *
 */
public class SheetUtils {
	public static final Logger logger = LoggerFactory.getLogger(SheetUtils.class);

	/**
	 * @return
	 */
	public static Workbook createWorkBook() {
		logger.info("Creating workbook '{}'...");
		Workbook wb = new HSSFWorkbook();
		return wb;
	}

	public static boolean writeWorkBook(Workbook workBook, String folderName, String workbookName)
			throws FileNotFoundException, IOException {
		boolean result = false;
		logger.info("Writing...");
		String fileName = workbookName;
		if (folderName != null) {
			fileName = folderName + "//" + workbookName;
			FileUtil.createFolderWithParents(new File(folderName));
		}
		try (OutputStream fileOut = new FileOutputStream(fileName)) {
			workBook.write(fileOut);
			result = true;
		} finally {
			workBook.close();
		}
		logger.info("Written");
		return result;
	}

}
