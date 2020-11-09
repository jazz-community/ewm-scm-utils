package com.ibm.js.team.supporttools.scmutils.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scmutils.SupplierExportTool;

public class ExportInformation {
	public static final Logger logger = LoggerFactory.getLogger(ExportInformation.class);
	private static final String ZIP_EXT = ".zip";

	public ExportInformation() {
		super();
	}
	
	public ExportInformation(String project) {
		super();
		this.project=project;
	}
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public String getSupplier() {
		return supplier;
	}
	
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	
	private String project = null;
	private String supplier = null;

}
