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
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * Abstract class for archiving data.
 * 
 * From RTC3.0 SDK
 * 
 * @see com.ibm.team.repository.service.tests.migration.ZipUtils
 */
public abstract class AbstractZip {

	private static final String PATH_SEPARATOR = "/";
	private static final String WIN_PATH_SEPARATOR = "\\\\";

	// the zip file that will be consumed or produced
	private File zipFile;
	// the root folder that is used to unzip relative paths in the zip file
	private File zipRoot;

	// the ZIP Output Stream
	private ZipOutputStream zipOutStream;

	// Can't close a zip file if there is not at least one file in it.
	private boolean zipHasContent = false;

	/**
	 * @param zipFile
	 * @param zipRootFolder
	 */
	public AbstractZip(File zipFile, File zipRootFolder) {
		super();
		this.zipFile = zipFile;
		this.zipRoot = zipRootFolder;
		open();
	}

	/**
	 * 
	 */
	private void open() {
		try {
			FileOutputStream output = new FileOutputStream(zipFile);
			this.zipOutStream = new ZipOutputStream(output);
		} catch (FileNotFoundException e) {
			logException("ZIP Exception - File not Found", e);
		}
	}

	/**
	 * Set zip level
	 * 
	 * @param level
	 */
	public void setLevel(int level) {
		this.zipOutStream.setLevel(level);
	}

	/**
	 * Add a directory and its content to a zip file
	 * 
	 * @param folderToZip
	 * @return From RTC3.0 SDK
	 * @see com.ibm.team.repository.service.tests.migration.ZipUtils
	 * 
	 */
	public boolean addFolder(File folderToZip) {

		if (!isChild(folderToZip.getAbsoluteFile())) {
			return false;
		}
		if (!folderToZip.exists()) {
			return false;
		}

		if (!folderToZip.isDirectory()) {
			log("Add directory: " + folderToZip.getAbsolutePath() + " is not a directory!!!" + zipRoot.getAbsolutePath()
					+ " !!!!!\n");
			return false;
		}

		try {
			zipOutStream.putNextEntry(createEntry(folderToZip));
		} catch (IOException e) {
			logException("ZIP Exception - IO Exception", e);
		}
		File[] files = folderToZip.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				addFolder(file);
			} else if (file.isFile()) {
				addFile(file);
			} else
				log("can't locate " + file.getAbsolutePath() + " !!!!!\n");
		}
		return true;
	}

	/**
	 * Add a file to the zip file
	 * 
	 * @param fileToZip
	 * @return From RTC3.0 SDK
	 * @see com.ibm.team.repository.service.tests.migration.ZipUtils
	 * 
	 */
	public boolean addFile(File fileToZip) {
		try {
			log("Add file: " + fileToZip.getAbsolutePath() + " Root " + zipRoot.getAbsolutePath());
			if (!isChild(fileToZip))
				return false;
			if (!fileToZip.exists())
				return false;
			ZipEntry entry = createEntry(fileToZip);

			zipOutStream.putNextEntry(entry);
			WritableByteChannel zipChannel = Channels.newChannel(zipOutStream);
			FileInputStream input = new FileInputStream(fileToZip);
			try {
				FileChannel inputChannel = input.getChannel();
				try {
					long length = entry.getSize();
					long bytesCopied = 0;
					while (bytesCopied < length) {
						bytesCopied += inputChannel.transferTo(bytesCopied, length - bytesCopied, zipChannel);
					}
				} finally {
					inputChannel.close();
				}
			} finally {
				input.close();
			}
			zipHasContent = true;
			return true;
		} catch (FileNotFoundException e) {
			logException("ZIP Exception - File not Found", e);
		} catch (IOException e) {
			logException("ZIP Exception - IO Exception", e);
		}
		return false;
	}

	private ZipEntry createEntry(File fileToZip) {
		String relativePath = fileToZip.getAbsolutePath().substring(zipRoot.getAbsolutePath().length());

		if (relativePath.startsWith(File.separator)) {
			relativePath = relativePath.substring(1);
		}
		// change path separators in UNIX format to avoid issues on UNIX
		// operating systems
		String filename = relativePath.replace(WIN_PATH_SEPARATOR, PATH_SEPARATOR);
		if (fileToZip.isDirectory()) {
			filename = filename + "/";
		}
		ZipEntry entry = new ZipEntry(filename);
		entry.setTime(fileToZip.lastModified());
		entry.setSize(fileToZip.length());
		entry.setMethod(ZipEntry.DEFLATED);
		return entry;
	}

	/**
	 * Close the zip file
	 */
	public void close() {
		try {
			if (zipHasContent) {
				zipOutStream.close();
			} else {
				log("WARNING!: ZIP FILE HAS NO ENTRY!");
			}
		} catch (IOException e) {
			logException("ZIP Exception - IO Exception", e);
		}
		zipOutStream = null;
	}

	/**
	 * Make sure the file to be zipped is in the hierarchy underneath the zipRoot
	 * 
	 * @param fileToZip
	 * @return
	 */
	private boolean isChild(File fileToZip) {
		if (!fileToZip.getAbsolutePath().startsWith(zipRoot.getAbsolutePath())) {
			log("Waring" + fileToZip.getAbsolutePath() + " is not in rootpath " + zipRoot.getAbsolutePath());
			return false;
		}
		return true;
	}

	/**
	 * Get the current Zip file
	 * 
	 * @return
	 */
	public File getZipFile() {
		return zipFile;
	}

	/**
	 * Get the current zipRoot
	 * 
	 * @return
	 */
	public File getZipRoot() {
		return zipRoot;
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