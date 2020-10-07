package com.ibm.js.team.supporttools.framework.framework;

public interface IProgress {

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	void showProgress();

}