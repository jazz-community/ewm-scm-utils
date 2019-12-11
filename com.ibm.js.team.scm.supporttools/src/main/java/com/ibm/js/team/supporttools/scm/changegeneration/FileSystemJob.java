package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

public class FileSystemJob extends Job {

	private File fsandboxFolder;
	private IFileOperation fIFileOperation;

	public FileSystemJob(String name) {
		super(name);
	}

	public FileSystemJob(String name, File sandboxFolder, IFileOperation op) {
		super(name);
		fsandboxFolder=sandboxFolder;
		fIFileOperation=op;
	}

	@Override
	protected IStatus run(IProgressMonitor arg0) {
		fIFileOperation.execute(fsandboxFolder);
		return null;
	}

}
