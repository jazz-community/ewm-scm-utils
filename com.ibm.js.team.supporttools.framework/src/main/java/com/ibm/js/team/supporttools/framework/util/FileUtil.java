/**
 * Licensed Materials - Property of IBM
 *
 * NewFileUtil.java
 * © Copyright IBM Corporation 2010.
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 */

package com.ibm.js.team.supporttools.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Tool for file copying
 * 
 * From RTC3.0 SDK
 * 
 * @see com.ibm.team.filesystem.rcp.core.internal.compare.ExternalCompareToolsUtil
 * @see com.ibm.team.filesystem.setup.junit.internal.SourceControlContribution
 * 
 * 
 */
public class FileUtil {

	/**
	 * Copy a file and don't complain if the source File does not exist. This
	 * operation fails if the parent folder of the destination file does not
	 * exist. Returns false in case the source file does not exist;
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static boolean copyFileIfExists(File srcFile, File destFile)
			throws IOException {
		if (!srcFile.exists()) {
			return false;
		}
		copyFile(srcFile, destFile);
		return true;
	}

	/**
	 * Copy a file, create the containing target folder if it does not exist
	 * 
	 * @param srcFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void copyFileCreateFolder(File srcFile, File destFile)
			throws IOException {
		File targetFolder = new File(destFile.getParent());
		createFolderWithParents(targetFolder);
		copyFile(srcFile, destFile);

	}

	/**
	 * Copy a folder into another folder. Copy contents recursively The
	 * destination folder gets created if it does not exist. If the source file
	 * is not a folder the operation fails and returns false. Otherwise the
	 * operation returns true.
	 * 
	 * @param srcFolder
	 * @param destFolder
	 * @throws IOException
	 */
	public static boolean copyFolder(File srcFolder, File destFolder)
			throws IOException {
		if (!srcFolder.isDirectory())
			return false;
		copyFolderRecursive(srcFolder, destFolder);
		return true;
	}

	/**
	 * Create a folder
	 * 
	 * @param aFolder
	 */
	public static void createFolderWithParents(File aFolder) {
		if (!aFolder.exists()) {
			aFolder.mkdirs();
		}
	}

	/**
	 * Delete recursively
	 * 
	 * @param aFile
	 * @param prompt
	 * @throws IOException
	 */
	public static void eraseRecursivePrompt(File aFile, boolean prompt)
			throws IOException {
		if (prompt && aFile.exists() && aFile.isDirectory()) {
			UserIO.prompt("Really delete: " + aFile.getAbsolutePath()
					+ " ? (Y/N)");
			String choice = UserIO.userInput();
			if (!choice.equalsIgnoreCase("y")) {
				return;
			}
		}
		eraseAllRecursive(aFile);
	}

	/**
	 * Erase a file or a
	 * 
	 * @param aFile
	 * @throws IOException
	 */
	private static void eraseAllRecursive(File aFile) throws IOException {
		if (!aFile.exists()) {
			return;
		}
		if (!aFile.isDirectory()) {
			aFile.delete();
		} else {
			deleteFolder(aFile);
		}
	}

	/**
	 * Deletes all files and sub directories under given directory. If a
	 * deletion fails, the method stops attempting to delete and returns false.
	 * 
	 * @return -true if all deletions were successful, false otherwise
	 */
	private static boolean deleteFolder(File dir) {
		if (dir.delete() || !dir.exists()) {
			return true;
		}
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteFolder(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * @param inputFile
	 * @param outputFile
	 * @throws IOException
	 * 
	 *             From RTC3.0 SDK
	 * @see com.ibm.team.filesystem.setup.junit.internal.SourceControlContribution
	 */
	private static void copyFile(File inputFile, File outputFile)
			throws IOException {
		// get channels
		FileInputStream fis = new FileInputStream(inputFile);
		FileOutputStream fos = new FileOutputStream(outputFile);
		FileChannel fcin = fis.getChannel();
		FileChannel fcout = fos.getChannel();
		// do the file copy
		try {
			fcin.transferTo(0, fcin.size(), fcout);
		} finally {
			fcin.close();
			fcout.close();
			fis.close();
			fos.close();
		}
	}

	/**
	 * Recursively copy a folder. Automatically create the destination folder.
	 * This operation fails if the parent of the destination directory
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @throws IOException
	 * 
	 *             From RTC3.0 SDK
	 * @see com.ibm.team.filesystem.setup.junit.internal.SourceControlContribution
	 * 
	 */
	private static void copyFolderRecursive(File inputFile, File outputFile)
			throws IOException {
		if (!outputFile.exists()) {
			boolean success = outputFile.mkdir();
			if (!success) {
				throw new IOException(
						"Cannot create destination directory" + outputFile.getAbsolutePath()); //$NON-NLS-1$
			}
		}
		if (!outputFile.isDirectory()) {
			throw new IOException(
					"Target is not a directory" + outputFile.getAbsolutePath()); //$NON-NLS-1$  	
		}
		String[] children = inputFile.list();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				String filename = children[i];
				File source = new File(inputFile, filename);
				File target = new File(outputFile, filename);
				if (source.isDirectory())
					copyFolderRecursive(source, target);
				else
					copyFile(source, target);
			}
		}
	}

	/**
	 * @param messagePrefix
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static File promptCreateFolder(String messagePrefix, File folder)
			throws IOException {
		String choice = UserIO.userChoice(messagePrefix + ": Create folder "
				+ folder.getCanonicalPath() + "? (y/n)", "[ynYN]");
		if (null != choice && choice.equalsIgnoreCase("y")) {
			createFolderWithParents(folder);
			if (folder.exists())
				return folder;
			return null;
		}
		return null;
	}

}
