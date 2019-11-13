package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;

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
		rangeStats.add((IRangeStat) new RangeStat(new LinearBaseRangeCalculator(1000)));
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

	public void logRangeInfo() throws IOException {
//		for (IRangeStat iRangeStat : rangeStats) {
//			iRangeStat.printRangeInfos();
//		}

		logger.info("Creating workbook...");
		Workbook wb = new HSSFWorkbook();
		int sheetNo = 0;
		int rowOffset = 1;
		for (IRangeStat iRangeStat : rangeStats) {
			IRangeCalculator rangeCalc = iRangeStat.getRangeCalculator();
			logger.info("Creating sheet...");
			String safeName = WorkbookUtil.createSafeSheetName(sheetNo++ + "- " + rangeCalc.getName());
			Sheet sheet = wb.createSheet(safeName);
			CreationHelper createHelper = wb.getCreationHelper();
			Row header = sheet.createRow(0);
			header.createCell(1).setCellValue(createHelper.createRichTextString("Total Files"));
			header.createCell(2).setCellValue(iRangeStat.getTotalFiles());
			header.createCell(5).setCellValue(createHelper.createRichTextString("Top Limit bytes"));
			header.createCell(4).setCellValue(createHelper.createRichTextString("Range index"));
			header.createCell(6).setCellValue(createHelper.createRichTextString("File count"));
			header.createCell(7).setCellValue(createHelper.createRichTextString("Extension count"));
			header.createCell(8).setCellValue(createHelper.createRichTextString("Extensions"));
			for (Iterator<IRangeInfo> iterator = iRangeStat.iterator(); iterator.hasNext();) {
				IRangeInfo iRangeInfo = iterator.next();
				int index = (int) iRangeInfo.getIndex();
				int count = (int) iRangeInfo.getFileCount();
				double threshold = rangeCalc.getTopThreshold(index);

				Row row = sheet.createRow(index + rowOffset);

				row.createCell(5).setCellValue((new Double(threshold)).intValue());
				row.createCell(4).setCellValue(index);
				row.createCell(6).setCellValue(count);
				row.createCell(7).setCellValue(iRangeInfo.getExtensionStatus().getNoExtensions());
				row.createCell(8).setCellValue(createHelper.createRichTextString(iRangeInfo.getExtensionStatus().getExtensionsCompressed()));
			}
			logger.info("Autosizing...");				
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.autoSizeColumn(6);
			sheet.autoSizeColumn(7);
			sheet.autoSizeColumn(8);
			sheet.autoSizeColumn(9);
		}
		logger.info("Writing...");
		try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
			wb.write(fileOut);
		}
		logger.info("Written");
	}
}
