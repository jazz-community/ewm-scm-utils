/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.statistics.sizerange.RangeStats;

public class SheetUtils {
	public static final Logger logger = LoggerFactory.getLogger(RangeStats.class);

	public static Workbook createWorkBook(String workBookName) {
		logger.info("Creating workbook '{}'...", workBookName);
		Workbook wb = new HSSFWorkbook();
		return wb;
	}

	public static boolean writeWorkBook(Workbook workBook, String fileName) throws FileNotFoundException, IOException {
		boolean result = false;
		logger.info("Writing...");
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
