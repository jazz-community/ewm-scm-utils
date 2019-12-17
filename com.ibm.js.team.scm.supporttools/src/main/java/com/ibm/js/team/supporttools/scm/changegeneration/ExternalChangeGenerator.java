package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.changegeneration.TouchFile.Touchmode;

public class ExternalChangeGenerator {
	private static final String GENERATE_2 = "objtest123";
	private static final String GENERATE_1 = "test123";
	public static final Logger logger = LoggerFactory.getLogger(ExternalChangeGenerator.class);
	private int fProgress = 0;
	private String fSandboxPAth;

	public ExternalChangeGenerator(String sandboxFolderPath) {
		fSandboxPAth = sandboxFolderPath;
	}

	/**
	 */
	public boolean generateLoad() {
		boolean result = false;
		logger.info("Build sandbox '{}'...", fSandboxPAth);
		File sandboxFolder = new File(fSandboxPAth);
		if (!sandboxFolder.exists()) {
			logger.error("Error: Sandboxfolder '{}' could not be created.", fSandboxPAth);
			return result;
		}
		if (!sandboxFolder.isDirectory()) {
			logger.error("Error: Sandboxfolder '{}' is not a directory.", fSandboxPAth);
			return result;
		}
		logger.info("\n\n1..");
		DeleteFilesOperation deleter = new DeleteFilesOperation();

		// FileSystemJob fs1 = new FileSystemJob("Touch",sandboxFolder,new
		// TouchFile(Touchmode.ANY));
		SandboxOperation op = new SandboxOperation(sandboxFolder, new TouchFile(Touchmode.ANY));
		op.addIgnoreDirectory(".metadata");
		op.addIgnoreDirectory(".jazz5");
		op.execute();
		showProgress();

		logger.info("\n\n2..");
		GenerateFilesOperation compiler1 = new GenerateFilesOperation(GENERATE_1);
		compiler1.addSupportedExtension("java");
		deleter.addSupportedExtension(GENERATE_1);
		SandboxOperation op1 = new SandboxOperation(sandboxFolder, compiler1);
		op1.addIgnoreDirectory(".metadata");
		op1.addIgnoreDirectory(".jazz5");
		op1.execute();
		showProgress();

		logger.info("\n\n3..");
		GenerateFilesOperation compiler2 = new GenerateFilesOperation(GENERATE_2);
		compiler2.addSupportedExtension("c");
		compiler2.addSupportedExtension("h");
		SandboxOperation op2 = new SandboxOperation(sandboxFolder, compiler2);
		op2.addIgnoreDirectory(".metadata");
		op2.addIgnoreDirectory(".jazz5");
		deleter.addSupportedExtension(GENERATE_2);
		op2.execute();
		showProgress();
		logger.info("\n\n4..");
		SandboxOperation delete = new SandboxOperation(sandboxFolder, deleter);
		delete.execute();
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
