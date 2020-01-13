/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
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

	public void setNumberP0(Cell cell, long value) {
		setNumberP0(cell, new Double(value));
	}

	public void setNumberP0(Cell cell, Double value) {
		if (value == null) {
			cell.setBlank();
			return;
		}
		if (!Double.isFinite(value)) {
			cell.setBlank();
			return;
		}
		value = (double) Math.round(value);
		cell.setCellValue(value);
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

	public void setText(Cell cell, String value) {
		if (value == null) {
			cell.setBlank();
		}
		cell.setCellValue(value);
	}

	public void setBoldText(Cell cell, String value) {
		cell.setCellValue(boldFace(value));
	}

	public void setURLHyperLink(Cell cell, String lable, String workbookFileName) {
		Hyperlink link = fWorkBook.getCreationHelper().createHyperlink(HyperlinkType.URL);
		link.setAddress(workbookFileName);
		cell.setCellValue(lable);
		cell.setHyperlink(link);
		CellStyle hlink_style = getLinkCellStyle();
		cell.setCellStyle(hlink_style);
	}

	public void setFileHyperLink(Cell cell, String lable, String workbookFileName) {
		Hyperlink link = fWorkBook.getCreationHelper().createHyperlink(HyperlinkType.FILE);
		link.setAddress(workbookFileName);
		cell.setCellValue(lable);
		cell.setHyperlink(link);
		CellStyle hlink_style = getLinkCellStyle();
		cell.setCellStyle(hlink_style);
	}

	private CellStyle getLinkCellStyle() {
		CellStyle hlink_style = fWorkBook.createCellStyle();
		Font hlink_font = fWorkBook.createFont();
		hlink_font.setUnderline(Font.U_SINGLE);
		hlink_font.setColor(IndexedColors.BLUE.getIndex());
		hlink_style.setFont(hlink_font);
		return hlink_style;
	}
}
