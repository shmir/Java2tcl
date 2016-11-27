/*
 * Created on Mar 17, 2005
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.ignissoft.java2tcl;

import jsystem.utils.FileUtils;

import com.aqua.sysobj.conn.CliApplication;

/**
 * @author guy.arieli
 */
public abstract class TclShellImpl extends CliApplication implements TclShell {
	
	private String prompt = "---DONE---";
	
	private String logFile = null;
	
	private String pureTclLogFile = null;
	
	private boolean printCommand = true;
	
	private boolean printReturn = false;
	
	private long timeout = 60000;
	
	@Override
	public void init() throws Exception {
		super.init();
	}
	
	@Override
	public void setWindowTitle(String title) {
		ShellCommand cmd = new ShellCommand("console title", new Object[] { title });
		executeCommand(cmd);
	}
	
	/**
	 * @return Returns the timeout.
	 */
	@Override
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * @param timeout
	 *        The timeout to set.
	 */
	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @return Log file name.
	 */
	@Override
	public String getLogFile() {
		return logFile;
	}
	
	/**
	 * @param logFile
	 *        Log file to set.
	 */
	@Override
	public void setLogFile(String logFile) {
		this.logFile = logFile;
		try {
			FileUtils.write(logFile, "");
			setPureTclLogFile(FileUtils.getPath(logFile)
					+ System.getProperty("file.separator")
					+ "PureTcl-"
					+ FileUtils.getFileNameWithoutFullPath(getLogFile()));
		} catch (Exception e) {}
		logFileS = new StringBuffer();
		pureTclLogFileS = new StringBuffer();
		logFileS.append("\n");
		pureTclLogFileS.append("\n");
	}
	
	protected StringBuffer logFileS = null;
	
	protected StringBuffer pureTclLogFileS = null;
	
	/**
	 * @return Name of pure Tcl log file (commands only, no return values).
	 */
	@Override
	public String getPureTclLogFile() {
		return pureTclLogFile;
	}
	
	/**
	 * @param pureTclLogFile
	 *        Name of pure Tcl log file (commands only, no return values).
	 */
	@Override
	public void setPureTclLogFile(String pureTclLogFile) {
		this.pureTclLogFile = pureTclLogFile;
		try {
			FileUtils.write(pureTclLogFile, "");
		} catch (Exception e) {}
	}
	
	@Override
	public boolean isPrintCommand() {
		return printCommand;
	}
	
	@Override
	public void setPrintCommand(boolean printCommand) {
		this.printCommand = printCommand;
	}
	
	@Override
	public boolean isPrintReturn() {
		return printReturn;
	}
	
	@Override
	public void setPrintReturn(boolean printReturn) {
		this.printReturn = printReturn;
	}
	
	public String getPrompt() {
		return prompt;
	}
	
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
}
