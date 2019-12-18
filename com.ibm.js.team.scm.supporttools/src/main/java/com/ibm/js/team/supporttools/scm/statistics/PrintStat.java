/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

public class PrintStat {

	public static String getFileAndFolderStatistics(final long cumulatedFolders, final long cumulatedFolderDepth,
			final long maxFolderDepth, final long cumulatedFiles, final long maxFileSize, final long cumulatedFileSize,
			final long maxFileDepth, final long cumulatedFileDepth) {
		String message = "";
		message += " Folders:\t " + cumulatedFolders + ConnectionStats.COLUMN_SEPERATOR;
		message += " Files/Folder:\t\t " + CalcUtil.divideFloatWithPrecision2AsString(cumulatedFiles, cumulatedFolders)
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " Folder Depth(avg):\t "
				+ CalcUtil.divideFloatWithPrecision2AsString(cumulatedFolderDepth, cumulatedFolders)
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " Folder Depth(max):\t " + maxFolderDepth + ConnectionStats.COLUMN_SEPERATOR;
		message += " Folder Depth(sum):\t " + cumulatedFolderDepth + ConnectionStats.COLUMN_SEPERATOR;
		message += " Folder Depth Limits: " + ConnectionStats.COLUMN_SEPERATOR;
		message += " log(e):\t " + CalcUtil.formatPrecision2(Math.log(cumulatedFolders))
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " (max):\t " + cumulatedFolders + ConnectionStats.COLUMN_SEPERATOR;
		message += "\n";
		message += " Files:\t\t " + cumulatedFiles + ConnectionStats.COLUMN_SEPERATOR;
		message += " File Size(avg):\t "
				+ CalcUtil.byBinaryMagnitudeAsString(CalcUtil.divideLong(cumulatedFileSize, cumulatedFiles))
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " File Size(max):\t " + CalcUtil.byBinaryMagnitudeAsString(maxFileSize)
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " File Size(sum):\t " + CalcUtil.byBinaryMagnitudeAsString(cumulatedFileSize)
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " File Depth(avg):\t "
				+ CalcUtil.divideFloatWithPrecision2AsString(cumulatedFileDepth, cumulatedFiles)
				+ ConnectionStats.COLUMN_SEPERATOR;
		message += " File Depth(max):\t " + maxFileDepth + ConnectionStats.COLUMN_SEPERATOR;
		message += " File Depth(sum):\t " + cumulatedFileDepth;
		return message;
	}

}
