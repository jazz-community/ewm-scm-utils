package com.ibm.js.team.supporttools.scm.statistics;

public class PrintStat {

	public static String getFileAndFolderStatistics(final long cumulatedFolders, final long cumulatedFolderDepth,
			final long maxFolderDepth, final long cumulatedFiles, final long maxFileSize, final long cumulatedFileSize,
			final long maxFileDepth, final long cumulatedFileDepth) {
		String message = "";
		message += " Folders:\t " + cumulatedFolders + ConnectionStat.COLUMN_SEPERATOR;
		message += " Files/Folder:\t\t " + CalcUtil.divideFloatWithPrecision2AsString(cumulatedFiles, cumulatedFolders)
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += " Folder Depth(avg):\t "
				+ CalcUtil.divideFloatWithPrecision2AsString(cumulatedFolderDepth, cumulatedFolders)
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += " Folder Depth(max):\t " + maxFolderDepth + ConnectionStat.COLUMN_SEPERATOR;
		message += " Folder Depth(sum):\t " + cumulatedFolderDepth + ConnectionStat.COLUMN_SEPERATOR;
		message += " Folder Depth Limits: " + ConnectionStat.COLUMN_SEPERATOR;
		message += " log(e):\t " + CalcUtil.formatPrecision2(Math.log(cumulatedFolders))
		+ ConnectionStat.COLUMN_SEPERATOR;
		message += " (max):\t " + cumulatedFolders
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += "\n";
		message += " Files:\t\t " + cumulatedFiles + ConnectionStat.COLUMN_SEPERATOR;
		message += " File Size(avg):\t "
				+ CalcUtil.byBinaryMagnitudeAsString(CalcUtil.divideLong(cumulatedFileSize, cumulatedFiles))
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += " File Size(max):\t " + CalcUtil.byBinaryMagnitudeAsString(maxFileSize)
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += " File Size(sum):\t " + CalcUtil.byBinaryMagnitudeAsString(cumulatedFileSize)
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += " File Depth(avg):\t "
				+ CalcUtil.divideFloatWithPrecision2AsString(cumulatedFileDepth, cumulatedFiles)
				+ ConnectionStat.COLUMN_SEPERATOR;
		message += " File Depth(max):\t " + maxFileDepth + ConnectionStat.COLUMN_SEPERATOR;
		message += " File Depth(sum):\t " + cumulatedFileDepth;
		return message;
	}

}
