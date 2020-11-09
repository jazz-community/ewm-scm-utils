/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils;

import com.ibm.js.team.supporttools.framework.commands.AbstractCommandFactory;
import com.ibm.js.team.supporttools.scmutils.commands.AnalyzeRepository;
import com.ibm.js.team.supporttools.scmutils.commands.AnalyzeSandbox;
import com.ibm.js.team.supporttools.scmutils.commands.AnalyzeWorkspace;
import com.ibm.js.team.supporttools.scmutils.commands.ConvertLoadrule;
import com.ibm.js.team.supporttools.scmutils.commands.DownloadComponentBaseline;
import com.ibm.js.team.supporttools.scmutils.commands.ExportWorkspace;
import com.ibm.js.team.supporttools.scmutils.commands.ExtractPreferredIDs;
import com.ibm.js.team.supporttools.scmutils.commands.FlattenLoadrule;
import com.ibm.js.team.supporttools.scmutils.commands.ImportWorkspace;
import com.ibm.js.team.supporttools.scmutils.commands.UploadToStream;

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
		put(new UploadToStream());
		put(new DownloadComponentBaseline());
		put(new ExtractPreferredIDs());
		// Enable sample command
		// put(new SampleCommandCmd());
	}
}
