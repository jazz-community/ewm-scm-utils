/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * For operations on a sandbox. Can ignore folders and files such as .jazz5
 * or .metadata.
 *
 */
public class SandboxOperation {
	public static final Logger logger = LoggerFactory.getLogger(SandboxOperation.class);
	private int fProgress = 0;
	private HashSet<String> ignoreFolderSet = new HashSet<String>(20);
	private HashSet<String> ignoreFileSet = new HashSet<String>(20);
	private IFileOperation fileOp = null;
	private File fSandbox = null;

	public SandboxOperation(File sandboxFolder, IFileOperation op) {
		this.fSandbox = sandboxFolder;
		this.fileOp = op;
	}

	public void addIgnoreDirectory(String name) {
		ignoreFolderSet.add(name);
	}

	public void addIgnoreFile(String name) {
		ignoreFileSet.add(name);
	}

	public void execute() {
		File sandbox = this.fSandbox;
		if (sandbox == null) {
			logger.info("Sandbox undefined");
			return;
		}
		if (!sandbox.isDirectory()) {
			logger.info("Sandbox is not a directory");
			return;
		}
		processFolder(sandbox);
	}

	/**
	 * @param folder
	 * @param path
	 * @param compStat
	 * @param depth
	 */
	private void processFolder(File folder) {
		File[] contents = folder.listFiles();
		for (File file : contents) {
			if (file.isDirectory()) {
				if (!isIgnoredDirectory(file)) {
					fileOp.execute(file);
					processFolder(file);
				}
			} else {
				if (!isIgnoredFile(file)) {
					fileOp.execute(file);
				}
			}
			showProgress();
		}
	}

	private boolean isIgnoredDirectory(File file) {
		if (file == null) {
			return false;
		}
		if (ignoreFolderSet.contains(file.getName())) {
			return true;
		}
		return false;
	}

	private boolean isIgnoredFile(File file) {
		if (file == null) {
			return false;
		}
		if (ignoreFolderSet.contains(file.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	private void showProgress() {
		fProgress++;
		if (fProgress % 10 == 9) {
			System.out.print(".");
		}
	}

}
