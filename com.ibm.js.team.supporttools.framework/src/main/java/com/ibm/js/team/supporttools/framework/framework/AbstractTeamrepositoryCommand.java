package com.ibm.js.team.supporttools.framework.framework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.scenario.ExpensiveScenarioService;
import com.ibm.js.team.supporttools.framework.scenario.IExpensiveScenarioService;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;

public abstract class AbstractTeamrepositoryCommand extends AbstractCommand implements ICommand {

	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_URL, true,
				SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_USER, true,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_PASSWORD, true,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION);
		return addTeamRepositoryCommandOptions(options);
	}

	/**
	 * Method to add the additional options this command requires.
	 *
	 * @param options
	 * @return
	 */
	public abstract Options addTeamRepositoryCommandOptions(Options options);

	@Override
	public boolean checkParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_URL)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_USER)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_PASSWORD))) {
			isValid = false;
		}
		isValid &= checkTeamreposiroyCommandParameters(cmd);
		return isValid;
	}

	/**
	 * Method to check if the additional required options/parameters required to
	 * perform the command are available.
	 *
	 * @param cmd
	 * @return
	 */
	public abstract boolean checkTeamreposiroyCommandParameters(CommandLine cmd);

	public static final Logger logger = LoggerFactory.getLogger(AbstractTeamrepositoryCommand.class);

	public AbstractTeamrepositoryCommand(String commandName) {
		super(commandName);
	}

	private ITeamRepository teamRepository = null;
	private IExpensiveScenarioService scenarioService = null;
	private String scenarioInstance = null;
	private IProgressMonitor monitor = new NullProgressMonitor();

	/**
	 * 
	 */
	private void startScenario() {
		try {
			String scenarioName = getScenarioName();
			if (scenarioName == null) {
				scenarioName = SupportToolsFrameworkConstants.SUPPORTTOOLSFRAMEWORK + "_"
						+ SupportToolsFrameworkConstants.FRAMEWORKVERSIONINFO + "_" + getCommandName();
			}
			this.scenarioService = new ExpensiveScenarioService(teamRepository, scenarioName);
			this.scenarioInstance = this.scenarioService.start();
		} catch (Exception e) {
			logger.error("Exception: {}", e.getMessage());
		}
	}

	/**
	 * 
	 */
	private void stopScenario() {
		if (this.scenarioInstance != null) {
			if (this.scenarioService != null) {
				try {
					this.scenarioService.stop(this.scenarioInstance);
					this.scenarioInstance=null;
					this.scenarioService=null;
				} catch (Exception e) {
					logger.error("Exception: {}", e.getMessage());
				}
			}
		}
	}

	public IProgressMonitor getMonitor() {
		return monitor;
	}

	/**
	 * @return get the teamRepository.
	 */
	public ITeamRepository getTeamRepository() {
		return teamRepository;
	}

	/**
	 * Return a scenarioName Implement in subclass
	 */
	public abstract String getScenarioName();

	@Override
	public boolean execute() {
		logger.info("Executing Command {}", this.getCommandName());
		boolean result = false;
		// Execute the code
		// Get all the option values
		String repositoryURI = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_URL);
		final String userId = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_USER);
		final String userPassword = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_PASSWORD);

		TeamPlatform.startup();
		try {
			monitor = new NullProgressMonitor();
			teamRepository = TeamPlatform.getTeamRepositoryService().getTeamRepository(repositoryURI);
			teamRepository.registerLoginHandler(new ITeamRepository.ILoginHandler() {
				public ILoginInfo challenge(ITeamRepository repository) {
					return new ILoginInfo() {
						public String getUserId() {
							return userId;
						}

						public String getPassword() {
							return userPassword;
						}
					};
				}
			});
			teamRepository.login(monitor);
			startScenario();
			result = executeTeamRepositoryCommand();
			stopScenario();
		} catch (TeamRepositoryException e) {
			logger.error("TeamRepositoryException: {}", e.getMessage());
		} finally {
			TeamPlatform.shutdown();
		}
		return result;
	}

	/**
	 * @return true if execution was successful.
	 */
	public abstract boolean executeTeamRepositoryCommand() throws TeamRepositoryException;
}
