/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2013. All Rights Reserved. 
 * 
 * ArchiveToSCMExtractor
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.content.util.VersionedContentManagerByteArrayInputStreamPovider;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IFolder;
import com.ibm.team.scm.common.IFolderHandle;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;

/**
 * Extracts data from a compressed archive file into the RTC Jazz SCM. The data
 * is compressed directly into the component, provided in the call. The
 * assumption is, that the data in the archive is inside of one or more folders,
 * which can be loaded with the component.
 * 
 * @see com.ibm.team.repository.service.tests.migration.ZipUtils
 * 
 */
public class ArchiveToSCMExtractor {

	public static final String SUBCOMPONENT_INFO = ".subcomponent_info";
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	public static final Logger logger = LoggerFactory.getLogger(ArchiveToSCMExtractor.class);
	// The ZipInputStream
	private ZipInputStream fZipInStream = null;
	// The team repository
	private ITeamRepository fTeamRepository = null;
	// The workspace connection
	private IWorkspaceConnection fWorkspace = null;
	// The change set
	private IChangeSetHandle fChangeSet = null;
	// The configuration to access the SCM data
	private IConfiguration fConfiguration = null;
	// The progress monitor we are using
	private IProgressMonitor fMonitor = new NullProgressMonitor();
	private int fProgress = 0;

	/**
	 * Simple constructor
	 * 
	 */
	public ArchiveToSCMExtractor() {
		super();
	}

	/**
	 * Extract the archive. This is basically the entry point for the extraction.
	 * 
	 * @param zipFile
	 * @param teamRepository
	 * @param targetWorkspace
	 * @param component
	 * @param changeSetComment
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	public boolean extractFileToComponent(String archiveFileName, IWorkspaceConnection targetWorkspace,
			IComponentHandle component, String changeSetComment, IProgressMonitor monitor) throws Exception {

		File archiveFile = new File(archiveFileName);
		fMonitor = monitor;
		fTeamRepository = (ITeamRepository) component.getOrigin();
		fWorkspace = targetWorkspace;
		fChangeSet = fWorkspace.createChangeSet(component, changeSetComment, true, monitor);
		fConfiguration = fWorkspace.configuration(component);
		logger.trace("Extract: " + archiveFile.getPath());
		try {
			FileInputStream fileInputStream = new FileInputStream(archiveFile);
			fZipInStream = new ZipInputStream(fileInputStream);
			try {
				return extract();
			} finally {
				fileInputStream.close();
			}
		} catch (Exception e) {
			logger.error("Exception extract file '{}': {}", archiveFile.getPath(), e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Extract the content of an archive to a component. This assumes that the
	 * archive contains folders on top level. These folders can act as projects when
	 * loading,
	 * 
	 * @return
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	private boolean extract() throws IOException, TeamRepositoryException {
		ZipEntry entry = fZipInStream.getNextEntry();
		boolean result = true;
		while (entry != null) {
			File targetEntry = new File(entry.toString());
			try {
				if (entry.isDirectory()) {
					logger.trace("Extracting Folder: " + targetEntry.getPath());
					findOrCreateFolderWithParents(targetEntry);

				} else {
					logger.trace("Extracting File: " + targetEntry.getPath());
					extractFile(targetEntry, entry);
				}
			} catch (Exception e) {
				logger.error("Exception extract file '{}': {}", targetEntry.getPath(), e.getMessage());
				e.printStackTrace();
				result = false;
			} finally {
				fZipInStream.closeEntry();
			}
			entry = fZipInStream.getNextEntry();
			showProgress();
		}
		fWorkspace.closeChangeSets(Collections.singletonList(fChangeSet), fMonitor);
		return result;
	}

	/**
	 * Extract a file from a ZipEntry to the Jazz SCM system. Currently only
	 * Text/UTF8 files are supported.
	 * 
	 * Commit the file if there are changes because the file did not exist or there
	 * are changes in the new content compared to the existing file.
	 * 
	 * @param targetFile
	 * @param zipEntry
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws TeamRepositoryException
	 * @throws InterruptedException
	 */
	private void extractFile(File targetFile, ZipEntry zipEntry)
			throws FileNotFoundException, IOException, InterruptedException, TeamRepositoryException {

		// Ignore the subcomponent.info
		if (SUBCOMPONENT_INFO.equals(targetFile.getName())) {
			logger.info("Skipping " + SUBCOMPONENT_INFO);
			return;
		}

		IFolder parentFolder = findOrCreateFolderWithParents(targetFile.getParentFile());
		IFileItem aFile = getFile(targetFile, parentFolder);
		if (aFile == null) {
			aFile = createFileItem(targetFile.getName(), zipEntry, parentFolder);
			logger.trace(" ... Created");
		}
		ByteArrayOutputStream contents = copyFileData(fZipInStream);
		try {
			IFileContentManager contentManager = FileSystemCore.getContentManager(fTeamRepository);
			FileLineDelimiter lineDelimiter = FileLineDelimiter.LINE_DELIMITER_NONE;
			String encoding = null;
			IFileContent filecontent = aFile.getContent();
			if (filecontent != null) {
				encoding = filecontent.getCharacterEncoding();
				lineDelimiter = filecontent.getLineDelimiter();
			}
			String contentType = aFile.getContentType();
			if (CONTENT_TYPE_TEXT_PLAIN.equals(contentType)) {
				if (FileLineDelimiter.LINE_DELIMITER_NONE.equals(lineDelimiter)) {
					lineDelimiter = FileLineDelimiter.LINE_DELIMITER_NONE;
					encoding = IFileContent.ENCODING_US_ASCII;
				}
			}
			IFileContent storedzipContent = contentManager.storeContent(encoding, lineDelimiter,
					new VersionedContentManagerByteArrayInputStreamPovider(contents.toByteArray()), null, fMonitor);
			// Compare the files. If there is a difference, set the new content
			// and commit the change
			if (!storedzipContent.sameContent(aFile.getContent())) {
				IFileItem fileWorkingCopy = (IFileItem) aFile.getWorkingCopy();
				fileWorkingCopy.setContent(storedzipContent);
				fWorkspace.commit(fChangeSet,
						Collections.singletonList(fWorkspace.configurationOpFactory().save(fileWorkingCopy)), fMonitor);
				logger.trace(" ... Content");
			}
		} catch (TeamRepositoryException e) {
			e.printStackTrace();
			throw e;
		} finally {
			contents.close();
		}
	}

	/**
	 * Tries to find a IFileItem node in a given IFolder. Returns the IFileItem
	 * found or null if none was found.
	 * 
	 * @param file
	 * @param parentFolder
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IFileItem getFile(File file, IFolderHandle parentFolder) throws TeamRepositoryException {
		IVersionable foundItem = getVersionable(file.getName(), parentFolder);
		if (null != foundItem) {
			if (foundItem instanceof IFileItem) {
				return (IFileItem) foundItem;
			}
		}
		return null;
	}

	/**
	 * Tries to create a IFileItem node in a given IFolder. Returns the IFileItem.
	 * 
	 * @param string
	 * @param zipEntry
	 * @param parentFolder
	 * 
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IFileItem createFileItem(String name, ZipEntry zipEntry, IFolder parentFolder)
			throws TeamRepositoryException {
		IFileItem aFile = (IFileItem) IFileItem.ITEM_TYPE.createItem();
		aFile.setParent(parentFolder);
		aFile.setName(name);
		aFile.setContentType(IFileItem.CONTENT_TYPE_TEXT);
		aFile.setFileTimestamp(new Date(zipEntry.getTime()));
		return aFile;
	}

	/**
	 * Copy the data from an input stream to an output stream. This is done to avoid
	 * the Jazz SCM closing the stream that contains the original data.
	 * 
	 * @param zipInStream
	 * @return
	 * @throws IOException
	 */
	private ByteArrayOutputStream copyFileData(InputStream zipInStream) throws IOException {
		ByteArrayOutputStream contents = new ByteArrayOutputStream();
		byte[] buf = new byte[2048];
		int read;
		while ((read = zipInStream.read(buf)) != -1) {
			contents.write(buf, 0, read);
		}
		contents.flush();
		return contents;
	}

	/**
	 * Find a folder in the Jazz SCM system. Create the folder if required. Also
	 * finds and if necessary creates the required parent folders. Could be
	 * optimized by keeping the folder stack.
	 * 
	 * @param folder
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IFolder findOrCreateFolderWithParents(File folder) throws TeamRepositoryException {

		if (folder == null) {
			return fConfiguration.completeRootFolder(fMonitor);
		}
		IFolder parent = null;
		String folderName = folder.getName();
		String parentName = folder.getParent();
		if (parentName == null) {
			parent = fConfiguration.completeRootFolder(fMonitor);
		} else {
			// Recursively find the parent folders
			parent = findOrCreateFolderWithParents(new File(parentName));
		}
		IFolder found = getFolder(folderName, parent);
		if (found == null) {
			found = createFolder(folderName, parent);
		}
		return found;
	}

	/**
	 * Find a folder in an existing parent folder.
	 * 
	 * @param folderName
	 * @param parentFolder
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IFolder getFolder(String folderName, IFolderHandle parentFolder) throws TeamRepositoryException {

		IVersionable foundItem = getVersionable(folderName, parentFolder);
		if (null != foundItem) {
			if (foundItem instanceof IFolder) {
				return (IFolder) foundItem;
			}
		}
		return null;
	}

	/**
	 * Gets a versionable with a specific name from a parent folder.
	 * 
	 * @param name
	 * @param parentFolder
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IVersionable getVersionable(String name, IFolderHandle parentFolder) throws TeamRepositoryException {
		// get all the child entries
		@SuppressWarnings("unchecked")
		Map<String, IVersionableHandle> handles = fConfiguration.childEntries(parentFolder, fMonitor);
		// try to find an entry with the name
		IVersionableHandle foundHandle = handles.get(name);
		if (null != foundHandle) {
			return fConfiguration.fetchCompleteItem(foundHandle, fMonitor);
		}
		return null;
	}

	/**
	 * Create a folder and commit it to SCM.
	 * 
	 * @param folderName
	 * @param parent
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IFolder createFolder(String folderName, IFolder parent) throws TeamRepositoryException {
		IFolder newFolder = (IFolder) IFolder.ITEM_TYPE.createItem();
		newFolder.setParent(parent);
		newFolder.setName(folderName);
		fWorkspace.commit(fChangeSet, Collections.singletonList(fWorkspace.configurationOpFactory().save(newFolder)),
				fMonitor);
		return newFolder;
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
