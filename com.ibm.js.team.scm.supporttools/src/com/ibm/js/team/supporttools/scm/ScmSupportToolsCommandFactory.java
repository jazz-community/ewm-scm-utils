/*******************************************************************************
 * Copyright (c) 2012 - 2013, 2018 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Ralph Schoon - Initial implementation
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm;

import com.ibm.js.team.supporttools.framework.commands.AbstractCommandFactory;
import com.ibm.js.team.supporttools.scm.commands.ExportRepositoryWorkspace;
import com.ibm.js.team.supporttools.scm.commands.ImportRepositoryWorkspace;

/**
 * Factory class dealing with commands. A new command has to be added to the
 * constructor.
 *
 */
public class ScmSupportToolsCommandFactory extends AbstractCommandFactory {

	@Override
	public void setAvailableCommands() {
		put(new ExportRepositoryWorkspace());
		put(new ImportRepositoryWorkspace());

		// Enable sample command
		// put(new SampleCommandCmd());
	}
}
