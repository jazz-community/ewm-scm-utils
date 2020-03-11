/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.changegeneration;

import java.io.File;

/**
 * Interface for file operations.
 *
 */
public interface IFileOperation {

	void execute(File file);

}
