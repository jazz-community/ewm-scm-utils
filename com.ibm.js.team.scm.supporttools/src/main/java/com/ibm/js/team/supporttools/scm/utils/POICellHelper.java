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

/**
 * Helper class to help creating cells in excel.
 *
 */
public class POICellHelper {

	private Workbook fWorkBook;
	private Font fBold = null;
	public static final String XLS_COLUMN_SEPARATOR = "     ";

	public POICellHelper(Workbook workBook) {
		this.fWorkBook = workBook;
		fBold = fWorkBook.createFont();
		fBold.setBold(true);
	}

	/**
	 * Create bold text
	 * 
	 * @param value
	 * @return
	 */
	public RichTextString boldFace(String value) {
		RichTextString string = fWorkBook.getCreationHelper().createRichTextString(value);
		string.applyFont(fBold);
		return string;
	}

	/**
	 * Set a number cell, round the value to 0 decimal.
	 *  
	 * @param cell
	 * @param value
	 */
	public void setNumberP0(Cell cell, long value) {
		setNumberP0(cell, new Double(value));
	}

	/**
	 * Set a number cell, round the value to 0 decimal.
	 * 
	 * @param cell
	 * @param value
	 */
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

	/**
	 * Set a number cell, round the value to 2 decimal.	 
	 * 
	 * @param cell
	 * @param value
	 */
	public void setNumberP2(Cell cell, long value) {
		setNumberP2(cell, new Double(value));
	}

	/**
	 * Set a number cell, round the value to 2 decimal.	 
	 * 
	 * @param cell
	 * @param value
	 */
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

	/**
	 * Set a number cell, no rounding.	 
	 * 
	 * @param cell
	 * @param value
	 */
	public void setNumber(Cell cell, long value) {
		setNumber(cell, new Double(value));
	}

	/**
	 * Set a number cell, no rounding.	 
	 * Set cell blank if infinite or null
	 * 
	 * @param cell
	 * @param value
	 */
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

	/**
	 * Set a text value or blank if text is null
	 * 
	 * @param cell
	 * @param value
	 */
	public void setText(Cell cell, String value) {
		if (value == null) {
			cell.setBlank();
		}
		cell.setCellValue(value);
	}

	/**
	 * Set text bold face
	 * 
	 * @param cell
	 * @param value
	 */
	public void setBoldText(Cell cell, String value) {
		cell.setCellValue(boldFace(value));
	}

	/**
	 * Set a Hyperlink providing a URI
	 * 
	 * @param cell
	 * @param label
	 * @param url
	 */
	public void setURLHyperLink(Cell cell, String label, String url) {
		Hyperlink link = fWorkBook.getCreationHelper().createHyperlink(HyperlinkType.URL);
		link.setAddress(url);
		cell.setCellValue(label);
		cell.setHyperlink(link);
		CellStyle hlink_style = getLinkCellStyle();
		cell.setCellStyle(hlink_style);
	}

	/**
	 * Set a Hyperlink providing a file name
	 * 
	 * @param cell
	 * @param lable
	 * @param fileName
	 */
	public void setFileHyperLink(Cell cell, String lable, String fileName) {
		Hyperlink link = fWorkBook.getCreationHelper().createHyperlink(HyperlinkType.FILE);
		link.setAddress(fileName);
		cell.setCellValue(lable);
		cell.setHyperlink(link);
		CellStyle hlink_style = getLinkCellStyle();
		cell.setCellStyle(hlink_style);
	}

	/**
	 * Create the style for hyper links.
	 * 
	 * @return
	 */
	private CellStyle getLinkCellStyle() {
		CellStyle hlink_style = fWorkBook.createCellStyle();
		Font hlink_font = fWorkBook.createFont();
		hlink_font.setUnderline(Font.U_SINGLE);
		hlink_font.setColor(IndexedColors.BLUE.getIndex());
		hlink_style.setFont(hlink_font);
		return hlink_style;
	}
}
