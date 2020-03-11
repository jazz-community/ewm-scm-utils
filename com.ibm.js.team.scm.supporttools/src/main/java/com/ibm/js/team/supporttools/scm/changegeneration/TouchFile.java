/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Touch a file to stimulate a data update.
 *
 */
public class TouchFile implements IFileOperation {
	public static final Logger logger = LoggerFactory.getLogger(TouchFile.class);
	// private long touchTime = System.currentTimeMillis();
	private Touchmode mode = Touchmode.ANY;

	// private void setTouchTime() {
	// touchTime = System.currentTimeMillis();
	// }

	public TouchFile(Touchmode mode) {
		super();
		this.mode = mode;
	}

	public enum Touchmode {
		FILE, DIRECTORY, ANY
	}

	@Override
	public void execute(File file) {
		touch(file);
	}

	private void touch(File file) {
		try {
			if (!file.exists()) {
				new FileOutputStream(file).close();
			}
			switch (mode) {
			case DIRECTORY:
				if (file.isDirectory()) {
					touchNow(file);
				}
				break;
			case FILE:
				if (file.isFile()) {
					touchNow(file);
				}
				break;
			default:
				touchNow(file);
				break;
			}

		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException file '{}'", file.getAbsolutePath());
		} catch (IOException e) {
			logger.error("IOException file '{}'", file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	private void touchNow(File file) {
		file.setLastModified(System.currentTimeMillis());
	}

}
