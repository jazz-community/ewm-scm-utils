/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import com.ibm.js.team.supporttools.scm.utils.CalcUtil;
import com.ibm.js.team.supporttools.scm.utils.PrintUtil;

public class PrintStat {

	public static String getFileAndFolderStatistics(final long cumulatedFolders, final long cumulatedFolderDepth,
			final long maxFolderDepth, final long cumulatedFiles, final long maxFileSize, final long cumulatedFileSize,
			final long maxFileDepth, final long cumulatedFileDepth) {
		String message = "";
		message += " Folders (sum):\t " + cumulatedFolders + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " Files/Folder:\t\t " + PrintUtil.asPrecision2(CalcUtil.divide(cumulatedFiles, cumulatedFolders))
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " Folder Depth(avg):\t "
				+ PrintUtil.asPrecision2(CalcUtil.divide(cumulatedFolderDepth, cumulatedFolders))
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " Folder Depth(max):\t " + maxFolderDepth + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " Folder Depth(sum):\t " + cumulatedFolderDepth + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " Folder Depth Limits: " + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " log(e):\t " + PrintUtil.asPrecision2(Math.log(cumulatedFolders))
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " (max):\t " + cumulatedFolders + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += "\n";
		message += " Files (sum):\t\t " + cumulatedFiles + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " File Size(avg):\t "
				+ PrintUtil.asBinaryMagnitude(CalcUtil.divide(cumulatedFileSize, cumulatedFiles))
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " File Size(max):\t " + PrintUtil.asBinaryMagnitude(maxFileSize)
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " File Size(sum):\t " + PrintUtil.asBinaryMagnitude(cumulatedFileSize)
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " File Depth(avg):\t " + PrintUtil.asPrecision2(CalcUtil.divide(cumulatedFileDepth, cumulatedFiles))
				+ ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " File Depth(max):\t " + maxFileDepth + ConnectionStats.TABBED_COLUMN_SEPERATOR;
		message += " File Depth(sum):\t " + cumulatedFileDepth;
		return message;
	}

}
