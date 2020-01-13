/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;
import com.ibm.js.team.supporttools.scm.utils.POICellHelper;
import com.ibm.js.team.supporttools.scm.utils.SheetUtils;

/**
 * @see https://www.callicoder.com/java-write-excel-file-apache-poi/
 *
 */
public class RangeStats {
	public static final Logger logger = LoggerFactory.getLogger(RangeStats.class);
	ArrayList<IRangeStat> rangeStats = new ArrayList<IRangeStat>();

	public RangeStats() {
		super();
		rangeStats.add((IRangeStat) new RangeStat(new Base2RangeCalculator()));
		rangeStats.add((IRangeStat) new RangeStat(new Base10RangeCalculator()));
		// rangeStats.add((IRangeStat) new RangeStat(new
		// LinearBaseRangeCalculator(1000)));
		// rangeStats.add((IRangeStat) new RangeStat(new
		// LinearBaseRangeCalculator(1000000)));
	}

	class RangeStatsIterator implements Iterator<IRangeStat> {

		Iterator<IRangeStat> iterator = null;

		public RangeStatsIterator() {
			super();
			this.iterator = rangeStats.iterator();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public IRangeStat next() {
			return this.iterator.next();
		}
	}

	public void analyze(FileInfo fileInfo) {
		for (IRangeStat iRangeStat : rangeStats) {
			iRangeStat.analyze(fileInfo);
		}
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Workbook createWorkBook() throws IOException {
		return SheetUtils.createWorkBook();
	}

	/**
	 * @throws IOException
	 */
	public Workbook updateWorkBook(Workbook workBook) throws IOException {
		logger.info("Creating range statistics...");
		if (workBook == null) {
			workBook = createWorkBook();
		}
		POICellHelper ch = new POICellHelper(workBook);
		int sheetNo = 1;
		int rowOffset = 4;
		for (IRangeStat iRangeStat : rangeStats) {
			IRangeCalculator rangeCalc = iRangeStat.getRangeCalculator();
			logger.info("Creating sheet...");
			String safeName = WorkbookUtil.createSafeSheetName(sheetNo++ + " - " + rangeCalc.getName());
			Sheet sheet = workBook.createSheet(safeName);
			Row header1 = sheet.createRow(0);

			ch.setBoldText(header1.createCell(1), "Total Files");
			ch.setBoldText(header1.createCell(2), POICellHelper.XLS_COLUMN_SEPARATOR);

			Row header1row = sheet.createRow(1);
			ch.setNumber(header1row.createCell(1), iRangeStat.getTotalFiles());

			Row header2 = sheet.createRow(3);

			ch.setBoldText(header2.createCell(3), "Range index");
			ch.setBoldText(header2.createCell(4), "Range Top Limit");
			ch.setBoldText(header2.createCell(5), "File count");
			ch.setBoldText(header2.createCell(6), "Extension count");
			ch.setBoldText(header2.createCell(7), "Extensions");

			for (Iterator<IRangeInfo> iterator = iRangeStat.iterator(); iterator.hasNext();) {
				IRangeInfo iRangeInfo = iterator.next();
				int index = (int) iRangeInfo.getIndex();
				int count = (int) iRangeInfo.getFileCount();
				double threshold = rangeCalc.getTopThreshold(index);

				Row row = sheet.createRow(index + rowOffset);

				ch.setNumber(row.createCell(3), index);
				ch.setNumber(row.createCell(4), threshold);
				ch.setNumber(row.createCell(5), count);
				ch.setNumber(row.createCell(6), iRangeInfo.getExtensionStatus().getNoExtensions());
				ch.setText(row.createCell(7), iRangeInfo.getExtensionStatus().getExtensionsCompressed());
			}
			logger.info("Autosizing...");
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
		}
		logger.info("Done...");
		return workBook;
	}
}
