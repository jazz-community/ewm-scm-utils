package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.changegeneration.TouchFile.Touchmode;

public class ExternalChangeGenerator {
	public static final Logger logger = LoggerFactory.getLogger(ExternalChangeGenerator.class);
	private int fProgress = 0;
	private String fSandboxPAth;

	public ExternalChangeGenerator(String sandboxFolderPath) {
		fSandboxPAth=sandboxFolderPath;
	}

	/**
	 */
	public boolean generateLoad() {
		boolean result = false;
		logger.info("Analyze sandbox '{}'...", fSandboxPAth);
		File sandboxFolder = new File(fSandboxPAth);
		if (!sandboxFolder.exists()) {
			logger.error("Error: Sandboxfolder '{}' could not be created.", fSandboxPAth);
			return result;
		}
		if (!sandboxFolder.isDirectory()) {
			logger.error("Error: Sandboxfolder '{}' is not a directory.", fSandboxPAth);
			return result;
		}
		logger.info("1..");
		SandboxOperation op = new SandboxOperation(sandboxFolder, new TouchFile(Touchmode.ANY));
		op.addIgnoreDirectory(".metadata");
		op.addIgnoreDirectory(".jazz5");
		op.execute();
		return true;
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
