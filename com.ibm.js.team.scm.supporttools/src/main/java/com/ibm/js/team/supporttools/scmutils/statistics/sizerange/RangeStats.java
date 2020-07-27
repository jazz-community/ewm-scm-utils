/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.statistics.sizerange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scmutils.statistics.FileInfo;
import com.ibm.js.team.supporttools.scmutils.utils.POICellHelper;
import com.ibm.js.team.supporttools.scmutils.utils.SheetUtils;

/**
 * Calculates range statistics for several range calculation options.
 * 
 * @see https://www.callicoder.com/java-write-excel-file-apache-poi/
 *
 */
public class RangeStats {
	private static final int RANGE_LIMIT_COLUMN = 3;
	private static final int RANGE_INDEX_COLUMN = 4;
	private static final int FILE_COUNT_COLUMN = 5;
	private static final int EXTENSION_COUNT_COLUMN = 6;
	private static final int EXTENSION_DETAIL_COLUMN = 7;

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
	 * @throws IOException
	 */
	public Workbook updateWorkBook(Workbook workBook) throws IOException {
		logger.info("Creating range statistics...");
		if (workBook == null) {
			workBook = SheetUtils.createWorkBook();
		}
		POICellHelper ch = new POICellHelper(workBook);
		int sheetNo = 2;
		int rowOffset = 1;
		for (IRangeStat iRangeStat : rangeStats) {
			IRangeCalculator rangeCalc = iRangeStat.getRangeCalculator();
			logger.info("Creating sheet...");
			String safeName = WorkbookUtil.createSafeSheetName(sheetNo++ + " - " + rangeCalc.getName());
			Sheet sheet = workBook.createSheet(safeName);
			Row header1 = sheet.createRow(0);

			ch.setBoldText(header1.createCell(1), "Total Files");
			ch.setBoldText(header1.createCell(2), POICellHelper.XLS_COLUMN_SEPARATOR);
			
			ch.setBoldText(header1.createCell(RANGE_INDEX_COLUMN), "Range index");
			ch.setBoldText(header1.createCell(RANGE_LIMIT_COLUMN), "Range Top Limit");
			ch.setBoldText(header1.createCell(FILE_COUNT_COLUMN), "File count");
			ch.setBoldText(header1.createCell(EXTENSION_COUNT_COLUMN), "Extension count");
			ch.setBoldText(header1.createCell(EXTENSION_DETAIL_COLUMN), "Extensions");

//			Row header1row = sheet.createRow(1);
//			ch.setNumber(header1row.createCell(1), iRangeStat.getTotalFiles());


			for (Iterator<IRangeInfo> iterator = iRangeStat.iterator(); iterator.hasNext();) {
				IRangeInfo iRangeInfo = iterator.next();
				int index = (int) iRangeInfo.getIndex();
				int count = (int) iRangeInfo.getFileCount();
				double threshold = rangeCalc.getTopThreshold(index);

				Row row = sheet.createRow(index + rowOffset);
				if(index==0){
					ch.setNumber(row.createCell(1), iRangeStat.getTotalFiles());
				}
				
				ch.setNumber(row.createCell(RANGE_LIMIT_COLUMN), threshold);
				ch.setNumber(row.createCell(RANGE_INDEX_COLUMN), index);
				ch.setNumber(row.createCell(FILE_COUNT_COLUMN), count);
				ch.setNumber(row.createCell(EXTENSION_COUNT_COLUMN), iRangeInfo.getExtensionStatus().getNoExtensions());
				ch.setText(row.createCell(EXTENSION_DETAIL_COLUMN), iRangeInfo.getExtensionStatus().getExtensionsCompressed());
			}
			logger.info("Autosizing...");
			for (int i = 1; i < 8; i++) {
				sheet.autoSizeColumn(i);				
			}
		}
		logger.info("Done...");
		return workBook;
	}
}
