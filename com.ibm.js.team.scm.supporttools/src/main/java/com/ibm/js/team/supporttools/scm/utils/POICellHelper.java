package com.ibm.js.team.supporttools.scm.utils;

import java.math.BigDecimal;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;

public class POICellHelper {

	private Workbook fWorkBook;
	private Font fBold = null;
	public static final String XLS_COLUMN_SEPARATOR = "     ";

	public POICellHelper(Workbook workBook) {
		this.fWorkBook = workBook;
		fBold = fWorkBook.createFont();
		fBold.setBold(true);
	}

	public RichTextString boldFace(String value) {
		RichTextString string = fWorkBook.getCreationHelper().createRichTextString(value);
		string.applyFont(fBold);
		return string;
	}

	public void setNumberP2(Cell cell, long value) {
		setNumberP2(cell, new Double(value));
	}

	public void setNumberP2(Cell cell, Double value) {
		if (value == null) {
			cell.setBlank();
			return;
		}
		if (!Double.isFinite(value)) {
			cell.setBlank();
			return;
		}
		value = Math.round(value * 100d) / 100d;
		cell.setCellValue(value);
	}

	public void setNumberP2(Cell cell, BigDecimal value) {
		Double doubleValue = null;
		if (value != null) {
			doubleValue = value.doubleValue();
		}
		setNumberP2(cell, doubleValue);
	}

	public void setNumber(Cell cell, long value) {
		setNumber(cell, new Double(value));
	}

	public void setNumber(Cell cell, Double value) {
		if (value == null) {
			cell.setBlank();
			return;
		}
		if (!Double.isFinite(value)) {
			cell.setBlank();
			return;
		}
		cell.setCellValue(value);
	}

	public void setNumber(Cell cell, BigDecimal value) {
		if (value == null) {
			cell.setBlank();
			return;
		}
		cell.setCellValue(value.doubleValue());
	}

	public void setText(Cell cell, String value) {
		if (value == null) {
			cell.setBlank();
		}
		cell.setCellValue(value);
	}

	public void setBoldText(Cell cell, String value) {
		cell.setCellValue(boldFace(value));
	}
}
