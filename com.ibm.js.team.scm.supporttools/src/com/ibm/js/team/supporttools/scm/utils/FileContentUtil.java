package com.ibm.js.team.supporttools.scm.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.ZipOutputStream;

import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;

public class FileContentUtil {

	public static final String CODE_SAMPLE_INPUT_FILE_NAME = "./CodeSampleInput.txt";
	private ArrayList<String> fSampleLines = null;
	private int fNumberSamples = 0;

	public FileContentUtil() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		initializeSampleLines();
	}

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

	private void initializeSampleLines() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(FileContentUtil.CODE_SAMPLE_INPUT_FILE_NAME), IFileContent.ENCODING_UTF_8));
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

	public void randomizeBinary(InputStream in, ZipOutputStream zos) throws IOException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			byte[] orr = new byte[arr.length];
			new Random().nextBytes(orr);
			zos.write(orr, 0, w);
		}
	}

	public void copyInput(InputStream in, ZipOutputStream zos) throws IOException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			zos.write(arr, 0, w);
		}
	}

	private String obfuscateLine(int length) {
		String line = getSampleLine();
		while (line.length() < length) {
			line += getSampleLine();
		}
		return line.substring(0, length);
	}

	private String getSampleLine() {
		String line = fSampleLines.get(getSampleIndex());
		return line;
	}

	private int getSampleIndex() {
		int sample = new Random().nextInt(fNumberSamples);
		return sample;
	}

	public void obfuscateBinary(InputStream in, ZipOutputStream zos) throws IOException, UnsupportedEncodingException {
		byte[] arr = new byte[1024];
		int w;
		while (-1 != (w = in.read(arr))) {
			String line = obfuscateLine(arr.length);
			zos.write(line.getBytes(), 0, w);
		}
	}

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

}
