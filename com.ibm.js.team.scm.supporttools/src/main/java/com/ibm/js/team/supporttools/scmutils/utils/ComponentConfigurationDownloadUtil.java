/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.js.team.supporttools.framework.framework.IProgress;
import com.ibm.js.team.supporttools.framework.util.FileUtil;
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IFolder;
import com.ibm.team.scm.common.IFolderHandle;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;

/**
 * Supports downloading a component configuration from a workspace connection directly into a folder on disk.
 *
 */
public class ComponentConfigurationDownloadUtil {
	
	public enum ExportMode {
		RANDOMIZE, OBFUSCATE, PRESERVE
	}
	
	private FileContentUtil fFileUtil;
	public ExportMode fExportMode = ExportMode.RANDOMIZE;
	private IProgress fProgress = null;

	public ComponentConfigurationDownloadUtil(ExportMode fExportMode, IProgress progress) {
		super();
		this.fExportMode = fExportMode;
		this.fProgress=progress;
	}

	/**
	 * Recreate the content of a component on the file system.
	 * 
	 * @param teamRepository
	 * @param connection
	 * @param component
	 * @param basePath
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	public boolean download(ITeamRepository teamRepository, IWorkspaceConnection connection,
			IComponentHandle component, String basePath, IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		boolean result = false;
		// Start walking the workspace contents
		IFileContentManager contentManager = FileSystemCore.getContentManager(teamRepository);

		try {
			IConfiguration compConfig = connection.configuration(component);
			// Fetch the items at the root of each component. We do this to
			// initialize our queue of stuff to download.
			@SuppressWarnings("unchecked")
			Map<String, IVersionableHandle> handles = compConfig.childEntriesForRoot(monitor);
			@SuppressWarnings("unchecked")
			List<IVersionable> items = compConfig
					.fetchCompleteItems(new ArrayList<IVersionableHandle>(handles.values()), monitor);

			// Recursion into each folder in the root
			storeDirectory(contentManager, compConfig, basePath, items, monitor);
			result = true;
		} finally {
			System.out.println("");
		}
		return result;
	}

	/**
	 * @param contentManager
	 * @param compConfig
	 * @param zos
	 * @param path
	 * @param items
	 * @param monitor
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	private void storeDirectory(IFileContentManager contentManager, IConfiguration compConfig, 
			String path, List<IVersionable> items, IProgressMonitor monitor)
			throws IOException, TeamRepositoryException {

		for (IVersionable v : items) {
			if (v instanceof IFolder) {
				// Write the directory
				//FileOutputStream out = new FileOutputStream(new File(base, component.getName().trim() + ".zip"));
				File folder = new File (path,v.getName() );
				folder.mkdir();
				@SuppressWarnings("unchecked")
				Map<String, IVersionableHandle> children = compConfig.childEntries((IFolderHandle) v, monitor);
				@SuppressWarnings("unchecked")
				List<IVersionable> completeChildren = compConfig
						.fetchCompleteItems(new ArrayList<IVersionableHandle>(children.values()), monitor);

				// Recursion into the contained folders
				storeDirectory(contentManager, compConfig, folder.getAbsolutePath(), completeChildren, monitor);

			} else if (v instanceof IFileItem) {
				// Get the file contents. Generate contents to save them into
				// the directory
				File file = new File(path,v.getName());
				FileUtil.createFolderWithParents(new File(path));
				if(!file.exists()){
					file.createNewFile();				
				}
				FileOutputStream out = new FileOutputStream(file);
				generateContent((IFileItem) v, contentManager, out, monitor);
				out.close();
			}
			showProgress();
		}
	}

	/**
	 * This generates the content to be persisted. There are three different
	 * options available that can be used.
	 * 
	 * For all options: the file and folder names are kept.
	 * 
	 * Randomize (default): Generates data based on random values. The size of
	 * the data a generated is the same as the original data. The generated data
	 * does not keep line ending and file encoding.
	 * 
	 * Obfuscate: Generates data based on sample code available. The size of the
	 * data a generated is similar or the same as the original data. The data is
	 * generated from example code snippets from a file. The generated data
	 * keeps line ending and file encoding, where available.
	 * 
	 * Preserve: Stores the original file and folder content unchanged.
	 * 
	 * @param file
	 * @param contentManager
	 * @param zos
	 * @param monitor
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private void generateContent(IFileItem file, IFileContentManager contentManager, FileOutputStream os,
			IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		FileLineDelimiter lineDelimiter = FileLineDelimiter.LINE_DELIMITER_NONE;
		String encoding = null;
		IFileContent filecontent = file.getContent();
		if (filecontent != null) {
			encoding = filecontent.getCharacterEncoding();
			lineDelimiter = filecontent.getLineDelimiter();
		}

		InputStream in = contentManager.retrieveContentStream(file, filecontent, monitor);
		switch (fExportMode) {
		case OBFUSCATE:
			getFileContentUtil().obfuscateSource(in, os, lineDelimiter, encoding);
			break;
		case PRESERVE:
			getFileContentUtil().copyInput(in, os);
			break;
		case RANDOMIZE:
			getFileContentUtil().randomizeBinary(in, os);
			break;
		default:
			getFileContentUtil().randomizeBinary(in, os);
			break;
		}
	}

	/**
	 * Instantiate the FileContentUtil that performs the data conversion.
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	FileContentUtil getFileContentUtil() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		if (null == fFileUtil) {
			return new FileContentUtil();
		}
		return fFileUtil;
	}
	
	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	private void showProgress() {
		fProgress.showProgress();
	}


}
