/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import com.ibm.js.team.supporttools.scm.utils.CalcUtil;

/**
 * Creates repository statistics adding the statistics for a number of connections.
 *
 */
public class RepositoryStat {
	private int noConnections = 0;
	// Connection Level
	private int cumulatedComponents = 0;
	private int maxComponents = 0;
	// Average Components
	// Hierarchy
	private long cumulatedHierarchyDepth = 0;
	// private double avgHierarchyDepth = 0;
	private long maxHierarchyDepth = 0;
	// Files
	private long cumulatedFiles = 0;
	private long cumulatedFileSize = 0;
	private long cumulatedFileDepth = 0;
	private long maxFileDepth = 0;
	private long maxFileSize = 0;
	// Folders
	private long cumulatedFolders = 0;
	private long cumulatedFolderDepth = 0;
	// private long averageFolderDepth = 0; // Calculated
	private long maxFolderDepth = 0;

	public long getMaxFolderDepth() {
		return maxFolderDepth;
	}

	public int getNoConnections() {
		return noConnections;
	}

	public int getMaxComponents() {
		return maxComponents;
	}

	public void addConnectionStats(ConnectionStats connectionStats) {
		this.noConnections++;

		// Connection level
		this.cumulatedComponents += connectionStats.getNoComponents();

		// Hierarchy
		this.cumulatedHierarchyDepth += connectionStats.getCumulatedHierarchyDepth();
		// this.avgHierarchyDepth = 0; // calculated
		this.maxHierarchyDepth = CalcUtil.calcMax(this.maxHierarchyDepth, connectionStats.getMaxHierarchyDepth());

		// Files
		this.cumulatedFiles += connectionStats.getCumulatedFiles();
		this.cumulatedFileSize += connectionStats.getCumulatedFileSize();
		this.cumulatedFileDepth += connectionStats.getCumulatedFileDepth();
		this.maxFileDepth = CalcUtil.calcMax(this.maxFileDepth, connectionStats.getMaxFileDepth());
		this.maxFileSize = CalcUtil.calcMax(this.maxFileSize, connectionStats.getMaxFileSize());

		// Folders
		this.cumulatedFolders += connectionStats.getCumulatedFolders();
		this.cumulatedFolderDepth += connectionStats.getCumulatedFolderDepth();
		this.maxFolderDepth = CalcUtil.calcMax(this.maxFolderDepth, connectionStats.getMaxFolderDepth());
	}

	public Double getAverageFolderDepth() {
		return CalcUtil.divide(getCumulatedFolderDepth(), getCumulatedFolders());
	}

	public double getAverageHierarchyDepth() {
		return CalcUtil.divide(getCumulatedHierarchyDepth(), getNoConnections());
	}

	public int getCumulatedComponents() {
		return cumulatedComponents;
	}

	public long getCumulatedHierarchyDepth() {
		return cumulatedHierarchyDepth;
	}

	public long getCumulatedFileDepth() {
		return cumulatedFileDepth;
	}

	public long getCumulatedFileSize() {
		return cumulatedFileSize;
	}

	public Double getCumulatedFilesPerFolder() {
		return CalcUtil.divide(getCumulatedFiles(), getCumulatedFolders());
	}

	public long getCumulatedFiles() {
		return cumulatedFiles;
	}

	public long getCumulatedFolders() {
		return cumulatedFolders;
	}

	public void setCumulatedFolders(long cumulatedFolders) {
		this.cumulatedFolders = cumulatedFolders;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public long getCumulatedFolderDepth() {
		return cumulatedFolderDepth;
	}

	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public Double getAverageFileSize() {
		return CalcUtil.divide(getCumulatedFileSize(), getCumulatedFiles());
	}

	public Double getAverageFileDepth() {
		return CalcUtil.divide(getCumulatedFileDepth(), getCumulatedFiles());
	}

	public long getMaxFileDepth() {
		return this.maxFileDepth;
	}

	public long getMaxHierarchyDepth() {
		return this.maxHierarchyDepth;
	}

}
