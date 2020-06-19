/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm;

import com.ibm.js.team.supporttools.framework.commands.AbstractCommandFactory;
import com.ibm.js.team.supporttools.scm.commands.AnalyzeRepository;
import com.ibm.js.team.supporttools.scm.commands.AnalyzeSandbox;
import com.ibm.js.team.supporttools.scm.commands.AnalyzeWorkspace;
import com.ibm.js.team.supporttools.scm.commands.ConvertLoadrule;
import com.ibm.js.team.supporttools.scm.commands.ExportWorkspace;
import com.ibm.js.team.supporttools.scm.commands.FlattenLoadrule;
import com.ibm.js.team.supporttools.scm.commands.ImportWorkspace;

/**
 * Factory class dealing with commands. A new command has to be added to the
 * method setAvailableCommands.
 *
 */
public class ScmSupportToolsCommandFactory extends AbstractCommandFactory {

	@Override
	public void setAvailableCommands() {
		put(new AnalyzeRepository());
		put(new AnalyzeWorkspace());
		put(new AnalyzeSandbox());
		put(new ExportWorkspace());
		put(new ImportWorkspace());
		put(new ConvertLoadrule());
		put(new FlattenLoadrule());
		// Enable sample command
		// put(new SampleCommandCmd());
	}
}
