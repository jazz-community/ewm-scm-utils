/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm;

import com.ibm.js.team.supporttools.framework.commands.AbstractCommandFactory;
import com.ibm.js.team.supporttools.scm.commands.AnalyzeSandbox;
import com.ibm.js.team.supporttools.scm.commands.AnalyzeWorkspaceConnection;
import com.ibm.js.team.supporttools.scm.commands.ConvertLoadrule;
import com.ibm.js.team.supporttools.scm.commands.ExportRepositoryWorkspace;
import com.ibm.js.team.supporttools.scm.commands.FlattenLoadrule;
import com.ibm.js.team.supporttools.scm.commands.GenerateExternalChanges;
import com.ibm.js.team.supporttools.scm.commands.ImportRepositoryWorkspace;

/**
 * Factory class dealing with commands. A new command has to be added to the
 * constructor.
 *
 */
public class ScmSupportToolsCommandFactory extends AbstractCommandFactory {

	@Override
	public void setAvailableCommands() {
		put(new AnalyzeWorkspaceConnection());
		put(new AnalyzeSandbox());
		put(new ExportRepositoryWorkspace());
		put(new ImportRepositoryWorkspace());
		put(new ConvertLoadrule());
		put(new FlattenLoadrule());
		put(new GenerateExternalChanges());
		// Enable sample command
		// put(new SampleCommandCmd());
	}
}
