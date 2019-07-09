/**
 * Licensed Materials - Property of IBM
 *
 * © Copyright IBM Corporation 2010.
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 */
package com.ibm.js.team.supporttools.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 
 * Abstract class for unzipping
 * 
 * From RTC3.0 SDK
 * 
 * @see com.ibm.team.repository.service.tests.migration.ZipUtils
 * 
 */
public abstract class AbstractUnzip {

	private static final String UX_PATH_SEPARATOR = "/";
	private static final String WIN_PATH_SEPARATOR = "\\\\";
	static final int BUFFER = 5000;

	// the zip file
	private File fZipFile;

	// the root folder that is used to unzip relative paths in the zip file
	private File fZipRoot;

	private ZipInputStream fZipInStream = null;
	private FileInputStream fInputStream = null;
	private ZipEntry fCurrEntry = null;

	public AbstractUnzip(File zipFile, File zipRootFolder) {
		super();
		this.fZipFile = zipFile;
		this.fZipRoot = zipRootFolder;
	}

	/**
	 * Unzip the archive and restore the folder modification time if this
	 * information is available. Handles files without folder information in the
	 * archive by creating the required parent folders. If the parent folders
	 * have to be created, the modification time of the folders is not
	 * preserved.
	 * 
	 * @return
	 * @throws Exception
	 * 
	 *             From RTC3.0 SDK
	 * @see com.ibm.team.repository.service.tests.migration.ZipUtils
	 */
	public boolean unZip() throws Exception {
		initialize();
		log("Unzip: " + fZipRoot.getPath());
		try {
			if (!fZipRoot.exists()) {
				FileUtil.createFolderWithParents(fZipRoot);
			}

			fInputStream = new FileInputStream(fZipFile);
			try {
				fZipInStream = new ZipInputStream(fInputStream);
				unZip(null);
				return true;
			} finally {
				fInputStream.close();
			}
		} catch (Exception e) {
			logException("Unzip Exception", e);
			return false;
		}
	}

	/**
	 * Initial settings for fields
	 */
	private void initialize() {
		fZipInStream = null;
		fInputStream = null;
		fCurrEntry = null;
	}

	/**
	 * Geta the current ZipEntry, gets the next entry if there is no current
	 * entry.
	 * 
	 * @return An entry or null if there is no more entry
	 * 
	 * @throws IOException
	 */
	private ZipEntry getZipEntry() throws IOException {
		if (null == fCurrEntry) {
			fCurrEntry = fZipInStream.getNextEntry();
		}
		return fCurrEntry;
	}

	/**
	 * Closes the current ZipEntry and makes the current invalid
	 * 
	 * @throws IOException
	 */
	private void closeZipEntry() throws IOException {
		fZipInStream.closeEntry();
		fCurrEntry = null;
	}

	/**
	 * Uncompresses the content of an archive. To be able to restore folder
	 * modification times folders are delegated to uncompress in a recursive
	 * approach.
	 * 
	 * @param currentFolder
	 *            null if there is no folder, or a string with the folder name.
	 * @throws IOException
	 */
	private void unZip(String currentFolder) throws IOException {
		ZipEntry entry = getZipEntry();

		while (entry != null) {
			// Handle zip files created with windows
			String name = entry.toString().replaceAll(WIN_PATH_SEPARATOR,
					UX_PATH_SEPARATOR);
			if (null != currentFolder) {
				// If this entry does not belong to the folder we are currently
				// trying to compress,
				// we go up in the recursion to find a place where this is the
				// case.
				if (!name.startsWith(currentFolder)) {
					return;
				}
			}
			if (entry.isDirectory()) {
				// wrap unzipping a directory in its own method to be able to
				// restore the modification time
				unZipFolder(currentFolder);
			} else {
				// Unzip a file in the current folder
				File targetFile = new File(fZipRoot, name);
				unZipFile(fZipInStream, targetFile);
				// Restore the timestamp
				targetFile.setLastModified(entry.getTime());
				closeZipEntry();
			}
			entry = getZipEntry();
		}
		return;
	}

	/**
	 * Unzips a folder. Delegates to a new unZip(folder) to unzip nested folders
	 * 
	 * @param currentFolder
	 * @throws IOException
	 */
	private void unZipFolder(String currentFolder) throws IOException {
		ZipEntry entry = getZipEntry();

		// Handle zip files created with windows
		String name = entry.toString().replaceAll(WIN_PATH_SEPARATOR,
				UX_PATH_SEPARATOR);
		if (null != currentFolder) {
			// A new folder is not part of this folder structure.
			// Let this be dealt with higher up
			if (!name.startsWith(currentFolder)) {
				return;
			}
		}

		File targetFile = new File(fZipRoot, name);
		log("Create Folder: " + targetFile.getAbsolutePath());
		FileUtil.createFolderWithParents(targetFile);
		long modified = entry.getTime();
		closeZipEntry();
		// Unzip nested files and folders.
		unZip(name);
		// Set the modification time.
		targetFile.setLastModified(modified);
		return;

	}

	/**
	 * Unzip a file
	 * 
	 * @param zInputStream
	 * @param targetFile
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void unZipFile(ZipInputStream zipInStream, File targetFile)
			throws FileNotFoundException, IOException {

		File parent = targetFile.getParentFile();
		log("Target: " + targetFile.getAbsolutePath());

		if (parent != null) {
			FileUtil.createFolderWithParents(parent);
		}

		FileOutputStream output = new FileOutputStream(targetFile);
		try {
			byte[] buffer = new byte[BUFFER];

			while (zipInStream.available() == 1) {
				int count = zipInStream.read(buffer, 0, buffer.length);
				if (count > 0) {
					output.write(buffer, 0, count);
				}
			}
		} finally {
			output.close();
		}
	}

	/**
	 * Log a message. Override to implement
	 * 
	 * @param message
	 */
	abstract protected void log(String message);

	/**
	 * Log an exception. Override to implement
	 * 
	 * @param message
	 * @param e
	 */
	abstract protected void logException(String message, Exception e);

}