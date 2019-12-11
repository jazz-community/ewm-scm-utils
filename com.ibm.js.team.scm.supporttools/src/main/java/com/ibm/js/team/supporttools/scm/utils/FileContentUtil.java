/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.ZipOutputStream;

import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;

/**
 * This class allows to randomize, obfuscate or copy files.s
 *
 */
public class FileContentUtil {

	private ArrayList<String> fSampleLines = null;
	private int fNumberSamples = 0;

	/**
	 * Create the tool
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public FileContentUtil() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		initializeSampleLines();
	}

	/**
	 * Reads a file line by line. The line content will be changed to some code
	 * that is randomly constructed from sample code snippets. The original
	 * content is replaced by arbitrary code snippets, but the line structure
	 * and the file length is preserved.
	 * 
	 * @param in
	 * @param zos
	 * @param lineDelimiter
	 * @param encoding
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public void obfuscateSource(InputStream in, ZipOutputStream zos, FileLineDelimiter lineDelimiter, String encoding)
			throws IOException, UnsupportedEncodingException {
		String delimiter = getLineDelimiter(lineDelimiter);
		if (FileLineDelimiter.LINE_DELIMITER_NONE.equals(lineDelimiter)) {
			obfuscateBinary(in, zos);
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String lineread = null;
		while (null != (lineread = reader.readLine())) {
			// Fake a line
			String line = obfuscateLine(lineread.length());
			line += delimiter;
			zos.write(line.getBytes(encoding));
		}
		zos.closeEntry();
	}

	/**
	 * Create a binary file based on the code samples, instead of generating
	 * random file content.
	 * 
	 * @param in
	 * @param zos
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	public void obfuscateBinary(InputStream in, ZipOutputStream zos) throws IOException, UnsupportedEncodingException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			String line = obfuscateLine(arr.length);
			zos.write(line.getBytes(), 0, w);
		}
	}

	/**
	 * Create the content for one line from the sample table. Use multiple
	 * samples to increase the line length.
	 * 
	 * @param length
	 * @return
	 */
	private String obfuscateLine(int length) {
		String line = getRandomSampleLine();
		while (line.length() < length) {
			line += getRandomSampleLine();
		}
		return line.substring(0, length);
	}

	/**
	 * Get a random
	 * 
	 * @return
	 */
	private String getRandomSampleLine() {
		String line = fSampleLines.get(getRandomSampleIndex());
		return line;
	}

	/**
	 * Get a random index selecting one of the list of a sample lines.
	 * 
	 * @return
	 */
	private int getRandomSampleIndex() {
		int sample = new Random().nextInt(fNumberSamples);
		return sample;
	}

	/**
	 * Randomize a file. The file content is read and random data of the same
	 * size is created to be stored.
	 * 
	 * @param in
	 * @param zos
	 * @throws IOException
	 */
	public void randomizeBinary(InputStream in, ZipOutputStream zos) throws IOException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			byte[] orr = new byte[arr.length];
			new Random().nextBytes(orr);
			zos.write(orr, 0, w);
		}
	}

	/**
	 * TODO: Random Generation
	 * Generate a random file file from an input file. 
	 * The file content is not read, a file with random data is created and stored.
	 * 
	 * @param in
	 * @param zos
	 * @throws IOException
	 */
	public void generateRandom(InputStream in, OutputStream out) throws IOException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			byte[] orr = new byte[arr.length];
			new Random().nextBytes(orr);
			out.write(orr, 0, w);
		}
	}

	
	/**
	 * Copy and preserve the input content into the output.
	 * 
	 * @param in
	 * @param zos
	 * @throws IOException
	 */
	public void copyInput(InputStream in, ZipOutputStream zos) throws IOException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			zos.write(arr, 0, w);
		}
	}

	/**
	 * Get the line delimiter to be used to terminate the line.
	 * 
	 * @param lineDelimiter
	 * @return
	 */
	private String getLineDelimiter(FileLineDelimiter lineDelimiter) {
		String delimiter = System.lineSeparator(); // Platform
		if (FileLineDelimiter.LINE_DELIMITER_PLATFORM.equals(lineDelimiter)) {
			return delimiter;
		}
		if (FileLineDelimiter.LINE_DELIMITER_CRLF.equals(lineDelimiter)) {
			return "\n\r";
		}
		if (FileLineDelimiter.LINE_DELIMITER_LF.equals(lineDelimiter)) {
			return "\n";
		}
		if (FileLineDelimiter.LINE_DELIMITER_CR.equals(lineDelimiter)) {
			return "\r";
		}
		return null;
	}

	/**
	 * Read a file with sample code lines that is used to obfuscate input data.
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void initializeSampleLines() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(ScmSupportToolsConstants.CODE_SAMPLE_INPUT_FILE_NAME),
						IFileContent.ENCODING_UTF_8));
		fSampleLines = new ArrayList<String>(200);
		String line;
		do {
			line = reader.readLine();
			if (line != null) {
				fSampleLines.add(line);
			}
		} while (line != null);
		reader.close();
		fNumberSamples = fSampleLines.size();
	}

}
